package dnse3.orchestration.resource;

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Patch;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import dnse3.common.TaskStatusEnum;
import dnse3.orchestration.jpa.model.simulation.SimulationStatus;
import dnse3.orchestration.server.DNSE3ListenerApplication;

public class NotificationReportResource extends ServerResource{
	
	private Long simulationId;
	
	/**
	 * Método que establece el indentificador de la simulación sobre la que se 
	 * va a actualizar el estado del informe
	 */
	@Override
	protected void doInit() throws ResourceException {
		try {
			simulationId = Long.valueOf(getAttribute("simulationId"));
		} catch (NumberFormatException e) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		super.doInit();
	}
	
	/**
	 * Método que resuelve las actualizaciones de estado de los informes
	 * @param request Objeto JSON con el informe cuyo estado se va a actualizar
	 */
	@Patch("json-patch")
	public void patchJson(JsonRepresentation request){
		JSONObject jsonObj = request.getJsonObject();
		if(!jsonObj.has("op")||!jsonObj.has("path")||!jsonObj.has("value")){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		try {
			switch (TaskStatusEnum.valueOf(jsonObj.getString("value"))) {
			case FINISHED:
				((DNSE3ListenerApplication) getApplication()).getExecutionController().notifyReport(simulationId, SimulationStatus.FINISHED);
				break;
			case ERROR:
				((DNSE3ListenerApplication) getApplication()).getExecutionController().notifyReport(simulationId, SimulationStatus.ERROR);
				break;
			default:
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}
		}catch (Exception e) {
			System.err.println(e.toString() +  " - " + e.getMessage());
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}
}
