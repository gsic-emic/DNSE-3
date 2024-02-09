package dnse3.scale.auxiliar;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Clase con los métodos necesarios controlar al motor de Docker utilizando la
 * API REST
 * 
 * @author GSIC gsic.uva.es
 * @version 20191218
 */
public class ComDocker {
	/** JSON con la estructura del servicio */
	private JSONObject specS;
	/** IP del manager del clúster + puerto expuesto para Docker */
	private String MANAGER_URL;
	/** Nombre del servicio */
	private String servicioPD;
	/** Identificador único del servicio */
	private String idS;
	/** Logger del Servicio de Escalado. */
	private static final Logger logger = Logger.getLogger("DNSE3ScaleLogger");

	/**
	 * Constructor de la clase para gestionar un servicio Docker
	 * 
	 * @param MANAGER_URL Dirección IP y puerto del mánager del clúster
	 * @param servicioPD  Nombre del servicio que se va a gestionar
	 */
	public ComDocker(String MANAGER_URL, String servicioPD) {
		this.MANAGER_URL = MANAGER_URL;
		this.servicioPD = servicioPD;
		if ((idS = idService()) == null) {
			logger.error("| ComDocker | El servicio indicado no está activo en la máquina manager");
			System.exit(-2);
		}
	}

	/**
	 * Método para obtener el identificador único que da Docker al servicio. También
	 * se obtiene la especificación del servicio en un objeto JSON que servirá para
	 * facilitar el escalado de contenedores.
	 * 
	 * @return Identificador único del servicio en el sistema
	 */
	private String idService() {
		String salida = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpReq = new HttpGet(MANAGER_URL + "/services");
			response = httpClient.execute(httpReq);
			int codigoEstado = response.getStatusLine().getStatusCode();
			switch (codigoEstado) {
			case 200:
				JSONArray vectorRespuesta = new JSONArray(EntityUtils.toString(response.getEntity()));
				for (int i = 0; i < vectorRespuesta.length(); i++) {
					JSONObject j = vectorRespuesta.getJSONObject(i);
					if (j.getJSONObject("Spec").getString("Name").contentEquals(servicioPD)) {
						specS = j.getJSONObject("Spec");
						salida = j.getString("ID");
						break;
					}
				}
				break;
			case 500:
				notificaError("idService", 500, "Server error.");
				break;
			case 503:
				notificaError("idService", 503, "Node is not part of a swarm.");
				break;
			default:
				notificaError("idService", codigoEstado, "Error no contemplado en la API");
				break;
			}
			if (response != null)
				response.close();
			httpClient.close();
		} catch (Exception e) {
			logger.error("| idService | " + e.toString() + " MSG: " + e.getMessage());
		}
		return salida;
	}

	/**
	 * Método para obtener el número de contenedores activos que forman el servicio
	 * gestionado.
	 * 
	 * @return Si el servicio está activo devuelve el número de contenedores
	 *         activos. Si se produce algún error devuelve -1.
	 */
	public int nContainer() {
		int valor = -1;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpReq = new HttpGet(MANAGER_URL + "/services");
			response = httpClient.execute(httpReq);
			int codigoEstado = response.getStatusLine().getStatusCode();
			switch (codigoEstado) {
			case 200:
				JSONArray vectorRespuesta = new JSONArray(EntityUtils.toString(response.getEntity()));
				for (int i = 0; i < vectorRespuesta.length(); i++) {
					JSONObject j = vectorRespuesta.getJSONObject(i).getJSONObject("Spec");
					if (j.getString("Name").contentEquals(servicioPD)) {
						valor = j.getJSONObject("Mode").getJSONObject("Replicated").getInt("Replicas");
						break;
					}
				}
				break;
			case 500:
				notificaError("nContainer", 500, "Server error.");
				break;
			case 503:
				notificaError("nContainer", 503, "Node is not part of a swarm.");
				break;
			default:
				notificaError("nContainer", codigoEstado, "Error no contemplado en la API");
				break;
			}
			if (response != null)
				response.close();
			httpClient.close();
		} catch (Exception e) {
			logger.error("| nContainer | " + e.toString() + " MSG: " + e.getMessage());
		}
		return valor;
	}

	/**
	 * Método para conocer el número de nodos activos en el clúster.
	 * 
	 * @return Número de nodos activos en el clúster. Si se produce algún error
	 *         devuelve -1.
	 */
	public int nNodes() {
		int valor = -1;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpReq = new HttpGet(MANAGER_URL + "/nodes");
			response = httpClient.execute(httpReq);
			int codigoEstado = response.getStatusLine().getStatusCode();
			switch (codigoEstado) {
			case 200:
				JSONArray vectorRespuesta = new JSONArray(EntityUtils.toString(response.getEntity()));
				valor = 0;
				for (int i = 0; i < vectorRespuesta.length(); i++)
					if (vectorRespuesta.getJSONObject(i).getJSONObject("Status").getString("State").toLowerCase()
							.contentEquals("ready"))
						++valor;
				break;
			case 500:
				notificaError("nNodes", 500, "Server error.");
				break;
			case 503:
				notificaError("nNodes", 503, "Node is not part of a swarm.");
				break;
			default:
				notificaError("nNodes", codigoEstado, "Error no contemplado en la API");
				break;
			}
			if (response != null)
				response.close();
			httpClient.close();
		} catch (Exception e) {
			logger.error("| nNodes | " + e.toString() + " MSG: " + e.getMessage());
		}
		return valor;
	}

	/**
	 * Método para aumentar o disminuir el número de contenedores que forman un
	 * servicio especificado.
	 * 
	 * @param incremento Incremento del número de contenedores. Puede ser negativo
	 *                   para que sea un decremento
	 * @return Nuevo número de contenedores que forma el servicio. Si el método
	 *         devuelve -1 no se ha podido llevar a cabo el escalado.
	 */
	public int scaleContainer(int incremento) {
		int contenedores = nContainer();
		if (contenedores != -1) {
			contenedores += incremento;
			if (contenedores >= 0) {
				contenedores = defineNumberContainer(contenedores);
			} else
				contenedores = -1;
		}
		return contenedores;
	}

	/**
	 * Método para escalar el número de contenedores que forman un servicio.
	 * 
	 * @param nuevoValor Número de contenedores que se desea que forme el servicio.
	 * @return Nuevo número de contenedores que forma el servicio. -1 si no se ha
	 *         conseguido escalar el servicio.
	 */
	public int defineNumberContainer(int nuevoValor) {
		int valor = -1;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			int index;
			if ((index = getVersionIndex()) == -1)
				return valor;
			HttpPost postReq = new HttpPost(MANAGER_URL + "/services/" + idS + "/update?version=" + index);

			postReq.addHeader("Accept", "application/json");

			JSONObject Mode = new JSONObject();
			JSONObject Replicated = new JSONObject();
			Mode.put("Replicated", Replicated);
			Replicated.put("Replicas", nuevoValor);
			specS.put("Mode", Mode);
			StringEntity body = new StringEntity(specS.toString(), "UTF-8");
			body.setContentType("application/json");
			postReq.setEntity(body);
			EntityUtils.consume(body);

			response = httpClient.execute(postReq);
			int codigoEstado = response.getStatusLine().getStatusCode();
			switch (codigoEstado) {
			case 200:
				do {
					Thread.sleep(250);
				} while (!convergenciaServicio(nuevoValor));
				valor = nuevoValor;
				break;
			case 400:
				notificaError("defineNumberContainer", 400, "Bad parameter.");
				break;
			case 404:
				notificaError("defineNumberContainer", 404, "No such service.");
				break;
			case 500:
				notificaError("defineNumberContainer", 500, "Server error.");
				break;
			case 503:
				notificaError("defineNumberContainer", 503, "Node is not part of a swarm.");
				break;
			default:
				notificaError("defineNumberContainer", codigoEstado, "Error no contemplado en la API");
				break;
			}
			if (response != null)
				response.close();
			httpClient.close();
		} catch (Exception e) {
			logger.error("| defineNumberContainer | " + e.toString() + " MSG: " + e.getMessage());
		}
		return valor;
	}

	/**
	 * Método para comprobar si el se servicio a covergido al número de contenedores
	 * deseado.
	 * 
	 * @param nuevoValor Número de contenedores activos que tiene que tendría que
	 *                   tener el sistema.
	 * @return Devuelve verdadero si el número de contenedores deseados es igual al
	 *         número de contenedores activos.
	 */
	private boolean convergenciaServicio(int nuevoValor) {
		boolean salida = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			URIBuilder builder = new URIBuilder(MANAGER_URL + "/tasks");
			JSONObject servicio = new JSONObject();
			String[] s = new String[1];
			s[0] = servicioPD;
			servicio.put("service", s);
			builder.setCustomQuery("filters=" + servicio.toString());
			HttpGet httpReq = new HttpGet(builder.build());
			builder = null;
			response = httpClient.execute(httpReq);
			int codigoEstado = response.getStatusLine().getStatusCode();
			switch (codigoEstado) {
			case 200:
				JSONArray vectorRespuesta = new JSONArray(EntityUtils.toString(response.getEntity()));
				int longitud = vectorRespuesta.length();
				int activos = 0;
				for (int i = 0; i < longitud; i++) {
					String estado = vectorRespuesta.getJSONObject(i).getJSONObject("Status").getString("State");
					if ((vectorRespuesta.getJSONObject(i).getString("DesiredState").equals(estado))
							&& estado.equals("running"))
						++activos;
				}
				if (activos == nuevoValor)
					salida = true;
				break;
			case 404:
				notificaError("control", 404, "No such task");
				break;
			case 500:
				notificaError("control", 500, "Server error.");
				break;
			case 503:
				notificaError("control", 503, "Node is not part of a swarm.");
				break;
			default:
				notificaError("control", codigoEstado, "Error no contemplado en la API");
				break;
			}
			if (response != null)
				response.close();
			httpClient.close();
		} catch (Exception e) {
			logger.error("| control | " + e.toString() + " MSG: " + e.getMessage());
		}
		return salida;
	}

	/**
	 * Método para obtener el identificador único de la versión. Docker utiliza este
	 * número para evitar conflictos en la escritura.
	 * https://docs.docker.com/engine/api/v1.39/#operation/ServiceUpdate
	 * 
	 * @return Identificador único de la versión
	 */
	private int getVersionIndex() {
		int valor = -1;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try {
			HttpGet httpReq = new HttpGet(MANAGER_URL + "/services/" + servicioPD);
			response = httpClient.execute(httpReq);
			int codigoEstado = response.getStatusLine().getStatusCode();
			switch (codigoEstado) {
			case 200:
				JSONObject vectorRespuesta = new JSONObject(EntityUtils.toString(response.getEntity()));
				valor = vectorRespuesta.getJSONObject("Version").getInt("Index");
				break;
			case 404:
				notificaError("getVersionIndex", 404, "No such service.");
				break;
			case 500:
				notificaError("getVersionIndex", 500, "Server error.");
				break;
			case 503:
				notificaError("getVersionIndex", 503, "Node is not part of a swarm.");
				break;
			default:
				notificaError("getVersionIndex", codigoEstado, "Error no contemplado en la API");
				break;
			}
			if (response != null)
				response.close();
			httpClient.close();
		} catch (Exception e) {
			logger.error("| getVersionIndex | " + e.toString() + " MSG: " + e.getMessage());
		}
		return valor;
	}

	/**
	 * Método para formatear la salida de error.
	 * 
	 * @param metodo       Nombre del método que ha generado un código de estado no
	 *                     satisfactorio.
	 * @param codigoEstado Código de estado que nos ha llegado en la respuesta.
	 * @param explica      Explición del valor del código de estado.
	 */
	private static void notificaError(String metodo, int codigoEstado, String explica) {
		logger.error("| " + metodo + " | Codigo de error: " + codigoEstado + " - " + explica);
	}
}