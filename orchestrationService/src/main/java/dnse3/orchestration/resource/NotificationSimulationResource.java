package dnse3.orchestration.resource;

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Patch;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import dnse3.common.TaskStatusEnum;
import dnse3.orchestration.server.DNSE3ListenerApplication;

public class NotificationSimulationResource extends ServerResource{

	private long simulationId;
	private int repetitionId;
	
	/**
	 * Método que recupera el identificador de la repetición y la simulación 
	 * sobre la que se va a trabajar
	 */
	@Override
	public void doInit(){		
		try {
			repetitionId = Integer.valueOf(getAttribute("repetitionId"));
			simulationId = Long.valueOf(getAttribute("simulationId"));
		} catch (NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}
	
	/**
	 * Método que se va a utilizar para actualizar el valor de cada repetición 
	 * de cada una de las simulaciones
	 * @param request Objeto JSON con el estado de la repetición
	 */
	@Patch("json-patch")
	public void patchJson(JsonRepresentation request){
		JSONObject obj = request.getJsonObject();
		//Se comprueba que el JSON tenga un formato válido
		if(!obj.has("op")||!obj.has("path")||!obj.has("value")){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		if(!obj.getString("op").equals("replace")||!obj.getString("path").equals("/status")){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		TaskStatusEnum status = TaskStatusEnum.valueOf(obj.getString("value"));
		
		try {
			switch(status){
			case FINISHED:
				((DNSE3ListenerApplication) getApplication()).getExecutionController().notifySimulationCompleted(simulationId, repetitionId);
				break;
			case ERROR:
				((DNSE3ListenerApplication) getApplication()).getExecutionController().notifySimulationError(simulationId, repetitionId);
				break;
			default:
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
		
		setStatus(Status.SUCCESS_NO_CONTENT);
	}
}
