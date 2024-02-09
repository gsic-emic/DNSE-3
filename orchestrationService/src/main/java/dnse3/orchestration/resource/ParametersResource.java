package dnse3.orchestration.resource;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import dnse3.orchestration.jpa.model.simulation.Parameter;
import dnse3.orchestration.jpa.model.simulation.ParameterSerializer;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class ParametersResource extends ServerResource {

	private long simulationId;
	private SimulationTypeEnum type;
	
	/**
	 * Método lanzado al alcanzar el recurso. Comprueba que el proyecto pertenezca al usuario, 
	 * asigna el valor del identificador de la simulación y fija el tipo (barrido o individual)
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));
			
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
			//Falta el caso de que se esté eliminando el proyecto
//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
//				throw new ResourceException(status)
			Long simulationId = Long.valueOf(getAttribute("simulationId"));
			switch (getAttribute("typeSimulation")) {
			case "singlesimulations":
				type = SimulationTypeEnum.SINGLE_SIMULATION;
				break;
			case "parametersweepsimulations":
				type = SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION;
				break;
			default:
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Tipo de simulación no registrada");
			}
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasSimulation(simulationId,projectId,type)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
			//Falta comprobar que la simulación no se esté eliminando
			this.simulationId = simulationId;
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}
	
	/**
	 * Método para obtener la lista de parámetros de una simulación en formato JSON
	 * @return JSON con la lista de parámetros de la simulación
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			List<Parameter> parameters = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getParameterController().getParameters(simulationId);
			if(parameters.isEmpty())
				return null;
			
			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(Parameter.class, new ParameterSerializer(getOriginalRef(),type,true)).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(parameters));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
