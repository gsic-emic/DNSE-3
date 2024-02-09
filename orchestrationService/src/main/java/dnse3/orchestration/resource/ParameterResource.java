package dnse3.orchestration.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import dnse3.orchestration.auxiliar.ParameterMapper;
import dnse3.orchestration.jpa.model.simulation.Parameter;
import dnse3.orchestration.jpa.model.simulation.ParameterSerializer;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class ParameterResource extends ServerResource {

	private int projectId;
	private long simulationId;
	private SimulationTypeEnum type;
	private String parameterName;
	
	/**
	 * Método utilizado para comprobar si existe un parámetro que pertenezca a un proyecto, 
	 * y que a su vez, el proyecto pertenezca al usuario
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));
			
			
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no pertenece al usuario");
			}
			String parameterName = getAttribute("parameterName");
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasParameterDescription(parameterName,projectId)){ //Posible conflicto de respuesta con PUT, ¿cómo no se encuentra algo que estás creando?
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El parámetro no pertenece al proyecto");
			}
			//Falta el caso de que se esté eliminando el proyecto
//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
//				throw new ResourceException(status)

			long simulationId = Long.valueOf(getAttribute("simulationId"));
			switch (getAttribute("typeSimulation")) {
			case "singlesimulations":
				type = SimulationTypeEnum.SINGLE_SIMULATION;
				break;
			case "parametersweepsimulations":
				type = SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION;
				break;
			default:
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Tipo de simulación no encontrada");
			}
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasSimulation(simulationId,projectId,type)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}

			this.projectId = projectId;
			this.parameterName = parameterName;
			this.simulationId = simulationId;
			//Falta comprobar que la simulación no se esté eliminando
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}
	
	/**
	 * Método para obtener un JSON con la información de un parámetro específico de un proyecto concreto
	 * @return JSON con la información del parámetro 
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			Parameter parameter = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getParameterController().getParameter(parameterName, projectId, simulationId);
			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Parameter.class, new ParameterSerializer(getOriginalRef(),type)).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(parameter));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}
	
	/**
	 * Método para añadir un parámetro a la simulación
	 * @param request
	 */
	@Put("json")
	public void putJson(JsonRepresentation request){
		try{
			JSONObject parameterObj = request.getJsonObject();
			if(!parameterObj.has("name")){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Falta el campo name");
			}
			
			boolean updated = ((DNSE3OrchestrationApplication) getApplication()).getDataController().updateParameter(new ParameterMapper(parameterObj, type.equals(SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION)),projectId,simulationId,type);
			if(!updated)
				setStatus(Status.SUCCESS_CREATED);
		}
		catch(Exception e){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
	}
	
	@Delete
	public void remove(){
		try{
			((DNSE3OrchestrationApplication) getApplication()).getDataController().removeParameter(parameterName,projectId,simulationId);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}
}
