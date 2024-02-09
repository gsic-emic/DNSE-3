package dnse3.orchestration.resource;

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.jpa.model.project.OutputFile;
import dnse3.orchestration.jpa.model.project.OutputFileSerializer;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class OutputFileResource extends ServerResource {
	
	private int projectId;
	private long simulationId;
	private SimulationTypeEnum type;
	private String outputFileName;
	
	/**
	 * M�todo inicial del recurso. Establece los valores b�sicos a partir de la URI del recurso
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));
			
			
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
			//Falta el caso de que se est� eliminando el proyecto
//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
//				throw new ResourceException(status)
			String outputFileName = getAttribute("outputFileName");
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasOutputFile(outputFileName, projectId)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
			String simulationId = getAttribute("simulationId");
			if(simulationId!=null){
				this.simulationId=Long.valueOf(simulationId);
				switch (getAttribute("typeSimulation")) {
				case "singlesimulations":
					type=SimulationTypeEnum.SINGLE_SIMULATION;
					break;
				case "parametersweepsimulations":
					type=SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION;
					break;
				default:
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				}
				if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasSimulation(this.simulationId,this.projectId,this.type)){
					throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				}
				//Falta comprobar que la simulaci�n no se est� eliminando
			}
			this.projectId = projectId;
			this.outputFileName = outputFileName;
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}
	
	/**
	 * M�todo para obtener la informaci�n de un fichero de salida
	 * @return JSON del fichero de salida
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			if(type!=null && !((DNSE3OrchestrationApplication) getApplication()).getDataController().hasOutputFile(outputFileName, simulationId, projectId, type)){ //Cuidao, s�lo para GET
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
			
			OutputFile outputFile = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getOutputFileController().getOutputFile(outputFileName, projectId);
			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(OutputFile.class, new OutputFileSerializer(getOriginalRef())).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(outputFile));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	/**
	 * M�todo para crear un fichero de salida adignado a una simulaci�n
	 * @param request JSON con la informaci�n para la creaci�n del fichero
	 */
	//Solo para recursos dependientes de las simulaciones
	@Put("json")
	public void putJson(JsonRepresentation request){ //�Necesito la petici�n? Con poner el nombre e el recurso bastar�a, pero de momento lo mantengo as�
		if(type==null){
			throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		}
		
		//Comprobar la petici�n
		JSONObject outputFileObj = request.getJsonObject();
		if(!outputFileObj.has("name") || !outputFileObj.getString("name").equals(outputFileName)){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Formato de JSON no v�lido");
		}
		
		try{
			boolean created = ((DNSE3OrchestrationApplication) getApplication()).getDataController().addOutputFile(outputFileName,simulationId,projectId,type); //Esto es si no la tiene
			if(created)
				setStatus(Status.SUCCESS_CREATED);
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	/**
	 * M�todo para eliminar un fichero de salida
	 */
	@Delete
	public void remove(){
		if(type==null){
			throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
		}
		
		try{
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().removeOutputFile(outputFileName,simulationId,projectId,type))
				setStatus(Status.CLIENT_ERROR_CONFLICT);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

}
