package dnse3.scale.server;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Options;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Clase que recoge el valor de las métricas que llegan desde el Servicio de
 * Colas.
 * 
 * @author GSIC gsic.uva.es
 * @version 20191218
 */
public class ManageQueue extends ServerResource {
	/**
	 * Tipo de cola que se va a gestionar. Como los dos servicios que gestiona el
	 * servicio de colas utilizan la métrica de forma similiar no se hace distinción
	 * del servicio.
	 */
	public String queue;
	/** Logger del Servicio de Escalado. */
	private static final Logger logger = Logger.getLogger("DNSE3ScaleLogger");

	/**
	 * Método que siempre se inicia cada vez que se atiende una petición. Si el
	 * valor del atributo queue no está contemplado lanza una excepción.
	 */
	@Override
	protected void doInit() throws ResourceException {
		switch (queue = getAttribute("queue")) {
		case "cola":
			break;
		default:
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Cola no registrada");
		}
		super.doInit();
	}

	/**
	 * Método para actualizar el valor de la cola que está controlando el Servicio
	 * de Monitorización y Escalado
	 * 
	 * @param rp JSON que tiene que contener el valor de la cola y un sello temporal
	 *           del instante en el que se tomó ese valor
	 */
	@Put("json")
	public void setLenQueue(JsonRepresentation rp) {
		try {
			JSONObject datos = rp.getJsonObject();
			if (datos.has("value") && datos.has("timestamp") && queue.equals("cola")) {
				((ScaleApp) getApplication()).getScale().setNumberTasks(datos.get("timestamp").toString(),
						datos.get("value").toString());
			}
		} catch (Exception e) {
			logger.error("| setLenQueue | " + e.toString() + "Mensaje: " + e.getMessage());
		}
	}

	@Options("txt")
	public String description() {
		return "OPTIONS, PUT";
	}

}
