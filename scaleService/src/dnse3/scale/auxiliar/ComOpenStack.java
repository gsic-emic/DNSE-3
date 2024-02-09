package dnse3.scale.auxiliar;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Clase auxiliar con los métodos necesarios para aumentar o disminuir el número
 * de máquinas activas en OpenStack
 * 
 * @author GSIC gsic.uva.es
 * @version 20191218
 */
public class ComOpenStack {
	/** Token con el que el usuario se autentica frente a OpenStack */
	private String token;
	/** Valores necesarios para llevar a cabo la conexión con OpenStack */
	private Properties propiedades;
	/**
	 * Mapa clave valor para almacenar el nombre de las máquinas virtuales en
	 * OpenStack
	 */
	private HashMap<String, String> mapaStack;
	/** Objeto JSON preparado para enviar a OpenStack */
	private JSONObject credenciales;
	/** Logger del Servicio de Escalado. */
	private static final Logger logger = Logger.getLogger("DNSE3ScaleLogger");

	/**
	 * Constructor de la calse. Se solicitan las propiedades cargadas del fichero
	 * cloud.properties
	 * 
	 * @param properties Valores necesarios para llevar a cabo la conexión con
	 *                   OpenStack
	 */
	public ComOpenStack(Properties properties) {
		this.propiedades = properties;
		mapaStack = new HashMap<>();
		creaCredenciales(propiedades);
	}

	/**
	 * Método para crear el objeto JSON con las credenciales de usuario y proyecto
	 * para utilizarlo en la autenticación frente a OpenStack.
	 * 
	 * @param propiedades Valores necesarios para llevar a cabo la conexión con
	 *                    OpenStack
	 */
	public void creaCredenciales(Properties propiedades) {
		credenciales = new JSONObject();
		JSONObject auth = new JSONObject();
		JSONObject identity = new JSONObject();
		JSONObject scope = new JSONObject();
		JSONObject project = new JSONObject();
		JSONArray methods = new JSONArray();
		JSONObject password = new JSONObject();
		JSONObject user = new JSONObject();
		credenciales.put("auth", auth);
		auth.put("identity", identity);
		identity.put("methods", methods);
		methods.put("password");
		identity.put("password", password);
		password.put("user", user);
		user.put("id", propiedades.getProperty("USER_ID"));
		user.put("password", propiedades.getProperty("PASSWORD"));
		auth.put("scope", scope);
		scope.put("project", project);
		project.put("id", propiedades.getProperty("PROJECT_ID"));
	}

	/**
	 * Método para aumentar el tamaño del clúster mediante la creación de una nueva
	 * stack. Agregará el nombre e identificador de la nueva máquina a mapaStack.
	 * 
	 * @param nombreStack Nombre que se le desea dar a la nueva stack
	 * @return Devolverá verdadero cuando el sistema nos indique que se ha recibido
	 *         la petición de manera correcta.
	 */
	public boolean crearStack(String nombreStack) {
		boolean salida = false;
		if (generaToken()) {
			try {
				CloseableHttpClient httpClient = HttpClients.createDefault();
				CloseableHttpResponse response = null;
				HttpPost postReq = new HttpPost(propiedades.getProperty("BASE_URL") + ":8004/v1/"
						+ propiedades.getProperty("PROJECT_ID") + "/stacks");
				postReq.addHeader("X-Auth-Token", token);
				postReq.addHeader("Accept", "application/json");

				JSONObject cuerpo = new JSONObject();
				cuerpo.put("stack_name", nombreStack);

				File file = new File("worker-template.json");
				if (!file.exists()) {
					System.err.println("El fichero worker-template.json no existe en el directorio de trabajo");
					System.exit(-3);
				}
				InputStream is = new FileInputStream(file);
				String plantilla = IOUtils.toString(is, "UTF-8");
				is = null;
				cuerpo.put("template", plantilla);

				StringEntity body = new StringEntity(cuerpo.toString(), "UTF-8");
				body.setContentType("application/json");
				postReq.setEntity(body);
				EntityUtils.consume(body);
				cuerpo = null;

				response = httpClient.execute(postReq);
				int codigoEstado = response.getStatusLine().getStatusCode();
				switch (codigoEstado) {
				case 201:
					HttpEntity entity = response.getEntity();
					JSONObject respuestaJ = new JSONObject(EntityUtils.toString(entity));
					mapaStack.put(nombreStack, respuestaJ.getJSONObject("stack").getString("id"));
					salida = true;
					EntityUtils.consume(entity);
					respuestaJ = null;
					break;
				case 400:
					notificaError("crearStack", 400, "Bad Request. Some content in the request was invalid.");
					break;
				case 401:
					notificaError("crearStack", 401, "Unauthorized. User must authenticate before making a request.");
					break;
				case 409:
					notificaError("crearStack", 409,
							"Conflict. This operation conflicted with another operation on this resource.");
					break;
				default:
					notificaError("crearStack", codigoEstado, "Código de estado sin registrar.");
					break;
				}
				if (response != null)
					response.close();
				httpClient.close();
			} catch (Exception e) {
				logger.error("| crearStack | " + e.toString() + " MSG: " + e.getMessage());
			}
		}
		return salida;
	}

	/**
	 * Método para eliminar una máquina del sistema. Eliminará el par nombre -
	 * identificador de mapaStack.
	 * 
	 * @param nombreStack Nombre de la stack a eliminar.
	 * @return Devolverá verdadero cuando todo el proceso se haya realizado de
	 *         manera correcta.
	 */
	public boolean eliminarStack(String nombreStack) {
		boolean salida = false;
		if (generaToken()) {
			CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = null;
			try {
				HttpDelete httpReq = new HttpDelete(
						propiedades.getProperty("BASE_URL") + ":8004/v1/" + propiedades.getProperty("PROJECT_ID")
								+ "/stacks/" + nombreStack + "/" + mapaStack.get(nombreStack));
				httpReq.addHeader("X-Auth-Token", token);
				httpReq.addHeader("Accept", "application/json");
				response = httpClient.execute(httpReq);
				int codigoEstado = response.getStatusLine().getStatusCode();
				switch (codigoEstado) {
				case 204:
					mapaStack.remove(nombreStack);
					salida = true;
					break;
				case 400:
					notificaError("eliminarStack", 400, "Bad Request. Some content in the request was invalid.");
					break;
				case 401:
					notificaError("eliminarStack", 401,
							"Unauthorized. User must authenticate before making a request.");
					break;
				case 404:
					notificaError("eliminarStack", 404, "Not Found. The requested resource could not be found.");
					break;
				case 500:
					notificaError("eliminarStack", 500, "Internal Server Error.");
					break;
				default:
					notificaError("eliminarStack", codigoEstado, "Código de estado sin registrar.");
					break;
				}
				if (response != null)
					response.close();
				httpClient.close();
			} catch (Exception e) {
				logger.error("| eliminarStack | " + e.toString() + " MSG: " + e.getMessage());
			}
		}
		return salida;
	}

	/**
	 * Método necesario para obtener el token con el cual el usuario se identifica
	 * frente al sistema.
	 * 
	 * @return Devolverá verdadero cuando la respuesta sea satisfactoria.
	 */
	private boolean generaToken() {
		boolean salida = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpPost httpPost = new HttpPost(propiedades.getProperty("AUTH_URL") + "/auth/tokens");
			StringEntity contenido = new StringEntity(credenciales.toString(), "UTF-8");
			contenido.setContentType("application/json");
			httpPost.setEntity(contenido);

			response = httpClient.execute(httpPost);
			EntityUtils.consume(contenido);
			httpPost = null;

			int codigoEstado = response.getStatusLine().getStatusCode();
			switch (codigoEstado) {
			case 201:
				token = response.getFirstHeader("X-Subject-Token").getValue();
				salida = true;
				break;
			case 400:
				notificaError("generaToken", 400, "Bad Request. Some content in the request was invalid.");
				break;
			case 401:
				notificaError("generaToken", 401, "Unauthorized. User must authenticate before making a request.");
				break;
			case 403:
				notificaError("generaToken", 403,
						"Forbidden. Policy does not allow current user to do this operation.");
				break;
			case 404:
				notificaError("generaToken", 404, "Not Found. The requested resource could not be found.");
				break;
			default:
				notificaError("generaToken", codigoEstado, "Caso no contemplado");
				break;
			}
			if (response != null)
				response.close();
			httpClient.close();
		} catch (Exception e) {
			logger.error("| generaToken | " + e.toString() + " MSG: " + e.getMessage());
		}
		return salida;
	}

	/**
	 * Método para formatear la salida de error.
	 * 
	 * @param metodo       Nombre del método que ha generado un código de estado no
	 *                     satisfactorio.
	 * @param codigoEstado Código de estado que nos ha llegado en la respuesta.
	 * @param explica      Explición del valor del código de estado.
	 */
	private void notificaError(String metodo, int codigoEstado, String explica) {
		logger.error("| " + metodo + " | Codigo de error: " + codigoEstado + " - " + explica);
	}

}