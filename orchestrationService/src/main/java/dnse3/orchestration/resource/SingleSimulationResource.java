package dnse3.orchestration.resource;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.auxiliar.ParameterMapper;
import dnse3.orchestration.auxiliar.RegistraLog;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;
import dnse3.orchestration.jpa.model.simulation.SingleSimulationSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class SingleSimulationResource extends ServerResource {

	private long simulationId;
	private String username;
	private int projectId;

	//private static final Logger usersLogger = Logger.getLogger("DNSE3UsersLogger");

	/**
	 * Inicializador. Si no encuentra ningún problema establece el nombre del usuario, identificador de proyecto e identificador de la simulación
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));


			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no pertenece al usuario");
			}
			Long simulationId = Long.valueOf(getAttribute("simulationId"));
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasSimulation(simulationId,projectId,SimulationTypeEnum.SINGLE_SIMULATION)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "La simulación no pertenece al proyecto");
			}
			//Falta comprobar que la simulación no se esté eliminando

			this.simulationId = simulationId;
			this.username = username;
			this.projectId = projectId;
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
		}
	}

	/**
	 * Recuperar la información de la simulación
	 * @return JSON con la información de la simulación
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			SingleSimulation simulation = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getSingleSimulationController().getSingleSimulation(simulationId);

			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(SingleSimulation.class, new SingleSimulationSerializer(getOriginalRef(),((DNSE3OrchestrationApplication) getApplication()).getDataController())).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(simulation));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}

	/**
	 * Método para actualizar la simulación
	 * @param request JSON con los parámetros que necesita la simulación para actulalizarse
	 */
	@Put("json")
	public void putJson(JsonRepresentation request){
		try{
			JSONObject simulationObj = request.getJsonObject();
			System.out.println(simulationObj);
			if(!simulationObj.has("name")){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. No tiene name");
			}

			String simulationName = simulationObj.getString("name");
			int numRepetitions = 0;
			int priority = -1;
			ArrayList<String> outputFiles = null;
			HashMap<String, ParameterMapper> parameters = null;

			if(simulationObj.has("numRepetitions"))
				try {//Por si viene con null
					numRepetitions = simulationObj.getInt("numRepetitions");
				}catch (JSONException e) {
					numRepetitions = 0;
				}

			if(simulationObj.has("priority"))
				try {
					priority = simulationObj.getInt("priority");
				}catch (JSONException e) {
					priority = -1;
				}

			if(simulationObj.has("outputFiles")){
				JSONArray outputFilesArray = simulationObj.getJSONArray("outputFiles");
				if(outputFilesArray.length()==0){
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. La longitud de la lista de ficheros de salida no puede ser 0");
				}

				outputFiles =  new ArrayList<>();
				for(int i=0; i<outputFilesArray.length(); i++){
					String outputFileName = outputFilesArray.getString(i);
					if(outputFiles.contains(outputFileName)){//Fichero repetido
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. OutFile "+outputFileName+" repetido");
					}
					outputFiles.add(outputFileName);
				}
			}

			JSONArray parametersArray = new JSONArray();
			if(simulationObj.has("parameters")){
				parametersArray = simulationObj.getJSONArray("parameters");
				parameters = new HashMap<>();
				if(parametersArray.length()>0){
					for(int i=0; i<parametersArray.length(); i++){
						JSONObject p = parametersArray.getJSONObject(i);
						if(!p.has("name")){
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Un parámetro no tiene name");
						}
						String parameterName = p.getString("name");
						if(parameters.containsKey(parameterName)){ //Parametro repetido
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Parámetro "+parameterName+" repetido");
						}
						try{
							parameters.put(parameterName, new ParameterMapper(p, false));
						}
						catch(Exception e){
							System.err.println("Excepción "+e.getCause());
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
						}
					}
				}
			}
			((DNSE3OrchestrationApplication) getApplication()).getDataController().updateSingleSimulation(simulationId,simulationName,numRepetitions,priority,outputFiles,parameters);
			String extra = "{Type:ss,Repetitions:"+numRepetitions+",Parameters:"+parametersArray.toString()+",OutputFiles"+outputFiles.toString()+"}";
			try {
				if(simulationObj.has("tiempPrep"))
					RegistraLog.registra(RegistraLog.modSimula, username, projectId, simulationId, simulationObj.getLong("tiempoPrep"), extra);
				else
					RegistraLog.registra(RegistraLog.modSimula, username, projectId, simulationId, extra);
			} catch (Exception e) {
				RegistraLog.registra(RegistraLog.modSimula, username, projectId, simulationId, extra);
			}
			//usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - EDICIÓN SIMULACIÓN INDIVIDUAL - " + parametersArray.toString());
		}
		catch(Exception e){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
		}
	}

	/**
	 * Método para cambiar el estado de la simulación
	 * @param request JSON con la operación a realizar a la simulación
	 */
	@Post("json")
	public void postJson(JsonRepresentation request){
		try{

			JSONObject obj = request.getJsonObject();

			if(!obj.has("operation")){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			}

			switch(obj.getString("operation").toLowerCase()){
			case "start":
				((DNSE3OrchestrationApplication) getApplication()).getExecutionController().startSimulation(simulationId,SimulationTypeEnum.SINGLE_SIMULATION);
				break;
			case "pause":
				((DNSE3OrchestrationApplication) getApplication()).getExecutionController().pauseSimulation(simulationId,SimulationTypeEnum.SINGLE_SIMULATION);
				RegistraLog.registra(RegistraLog.pauSimula, username, projectId, simulationId);
				//usersLogger.info("Username:"+username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - PAUSA SIMULACIÓN INDIVIDUAL");
				break;
			case "stop":
				((DNSE3OrchestrationApplication) getApplication()).getExecutionController().stopSimulation(simulationId,SimulationTypeEnum.SINGLE_SIMULATION);
				RegistraLog.registra(RegistraLog.stoSimula, username, projectId, simulationId);
				//usersLogger.info("Username:"+username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - STOP SIMULACIÓN INDIVIDUAL");
				break;
			default:
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Operación no soportada. Operación " + obj.getString("operation"));
			}
			setStatus(Status.SUCCESS_ACCEPTED);
		}
		catch(JSONException e){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Error en el JSON");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Borrado de la simulación
	 */
	@Delete
	public void remove(){
		try{
			((DNSE3OrchestrationApplication) getApplication()).getDataController().removeSingleSimulation(simulationId);
			//usersLogger.info("Username:"+username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId+" - DELETE SIMULACIÓN INDIVIDUAL");
			RegistraLog.registra(RegistraLog.borSimula, username, projectId, simulationId);
			setStatus(Status.SUCCESS_ACCEPTED);

		}
		catch(Exception e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}
}
