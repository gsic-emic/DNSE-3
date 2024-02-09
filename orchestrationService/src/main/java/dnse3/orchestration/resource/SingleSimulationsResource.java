package dnse3.orchestration.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.auxiliar.ParameterMapper;
import dnse3.orchestration.auxiliar.RegistraLog;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;
import dnse3.orchestration.jpa.model.simulation.SingleSimulationSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class SingleSimulationsResource extends ServerResource {

	private int projectId;
	private String username;
	//private static final Logger usersLogger = Logger.getLogger("DNSE3UsersLogger");
	/**
	 * Inicializador del recurso. Comprueba que los datos introducidos por la URI sean válidos.
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no pertecene al usuario");
			}
			this.projectId = projectId;
			this.username = username;
			//Falta el caso de que se esté eliminando el proyecto
			//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
			//				throw new ResourceException(status)
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Error en el identificador del proyecto "+e.toString());
		}
	}

	/**
	 * Método para obtener el listado de simulaciones individuales de un proyecto
	 * @return JSON con la lista de simulaciones individuales del proyecto
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			List<SingleSimulation> singleSimultions = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getSingleSimulationController().getSingleSimulations(projectId);
			if(singleSimultions.isEmpty())
				return null;

			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(SingleSimulation.class, new SingleSimulationSerializer(getOriginalRef(),((DNSE3OrchestrationApplication) getApplication()).getDataController(),true)).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(singleSimultions));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}

	/**
	 * Método para crear una nueva simulación individual
	 * @param request JSON con la actualización de la simulación
	 */
	@Post("json")
	public void postJson(JsonRepresentation request){
		try{
			//Revisión del contenido de la petición
			JSONObject obj = request.getJsonObject();

			if(!obj.has("name")||!obj.has("outputFiles")){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "El JSON está mal formado");
			}

			JSONArray outputFilesArray = obj.getJSONArray("outputFiles");
			if(outputFilesArray.length()==0){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "El número de ficheros de salida tiene que ser superior a 0");
			}

			//Extracción de los atributos para crear la simulación
			String simulationName = obj.getString("name");
			int numRepetitions = 1;
			int priority = 50;
			ArrayList<String> outputFiles = new ArrayList<>();
			HashMap<String, ParameterMapper> parameters = new HashMap<>();

			for(int i=0; i<outputFilesArray.length(); i++){
				String outputFileName = outputFilesArray.getString(i);
				if(outputFiles.contains(outputFileName)){//Fichero repetido
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Fichero repetido");
				}
				outputFiles.add(outputFileName);
			}

			if(obj.has("numRepetitions")) {
				numRepetitions = obj.getInt("numRepetitions");
			}

			JSONArray parametersArray = new JSONArray();
			if(obj.has("parameters")){
				parametersArray = obj.getJSONArray("parameters");
				if(parametersArray.length()>0){
					for(int i=0; i<parametersArray.length(); i++){
						JSONObject p = parametersArray.getJSONObject(i);
						if(!p.has("name")){
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Un parámetro no tiene nombre");
						}
						String parameterName = p.getString("name");
						if(parameters.containsKey(parameterName)){ //Parametro repetido
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Un parámetro está repetido");
						}
						try{
							parameters.put(parameterName, new ParameterMapper(p, false));
						}
						catch(Exception e){
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
						}
					}
				}
			}
			if(obj.has("priority"))
				priority = obj.getInt("priority");

			long simulationId = ((DNSE3OrchestrationApplication) getApplication()).getDataController().createSingleSimulation(simulationName, numRepetitions, priority, outputFiles, parameters, projectId);
			String extra = "{Type:ss,Repetitions:"+numRepetitions+",Parameters:"+parametersArray.toString()+",Outputfiles:"+outputFiles.toString()+"}";
			try {
				if(obj.has("tiempPrep"))
					RegistraLog.registra(RegistraLog.nueSimula, username, projectId, simulationId, obj.getLong("tiempoPrep"), extra);
				else
					RegistraLog.registra(RegistraLog.nueSimula, username, projectId, simulationId, extra);
			} catch (Exception e) {
				RegistraLog.registra(RegistraLog.nueSimula, username, projectId, simulationId, extra);
			}
			//usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - NUEVA SIMULACIÓN INDIVIDUAL - Repetitions: " + numRepetitions + " - " + parametersArray.toString() + " - " + outputFilesArray.toString());
			setLocationRef(getOriginalRef().toString()+simulationId+"/");
			setStatus(Status.SUCCESS_CREATED);
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}
}
