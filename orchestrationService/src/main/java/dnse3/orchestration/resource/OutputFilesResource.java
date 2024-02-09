package dnse3.orchestration.resource;

import java.util.List;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.jpa.model.project.OutputFile;
import dnse3.orchestration.jpa.model.project.OutputFileSerializer;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class OutputFilesResource extends ServerResource {
	
	private int projectId;
	private long simulationId;
	private SimulationTypeEnum type;
	
	/**
	 * Método de inicialización del recurso. Comprueba si los datos introducidos en la URI son correctos.
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
			String simulationId = getAttribute("simulationId");
			if(simulationId!=null){
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
				if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasSimulation(Long.valueOf(simulationId), projectId, type)){
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				}
				//Falta comprobar que la simulación no se esté eliminando
				this.simulationId = Long.valueOf(simulationId);
			}
			this.projectId = projectId;

		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Error al convertir a número "+e.toString());
		}
	}
	
	/**
	 * Método para recuperar los ficheros de salida de un proyecto o de una simulación
	 * @return JSON con la información de los ficheros de salida del proyecto o de la simulación
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			List<OutputFile> outputFiles = null;
			if(type!=null)
				outputFiles = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getOutputFileController().getOutputFilesFromSimulation(simulationId);
			else
				outputFiles = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getOutputFileController().getOutputFilesFromProject(projectId);
			
			if(outputFiles.isEmpty()) {
				return null;
			}
			
			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(OutputFile.class, new OutputFileSerializer(getOriginalRef(),true)).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(outputFiles));
			response.setIndenting(true);
			return response;
		}
		catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

}
