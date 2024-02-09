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
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulationSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class ParameterSweepSimulationsResource extends ServerResource {

	private int projectId;
	private String username;
	//private static final Logger usersLogger = Logger.getLogger("DNSE3UsersLogger");

	/**
	 * Inicializador del reurso. Comprueba que los par�metros introducidos por la URI sean v�lidos
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			}
			this.projectId = projectId;
			this.username = username;
			//Falta el caso de que se est� eliminando el proyecto
			//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
			//				throw new ResourceException(status)
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Formato de proyecto no v�lido");
		}
	}

	/**
	 * M�todo para obtener la lista de simulaciones de barrido de par�metro.
	 * @return JSON con la lista de simulaciones de barrido de par�metro
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			List<ParameterSweepSimulation> parameterSweepSimultions = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getParameterSweepSimulationController().getParameterSweepSimulations(projectId);
			if(parameterSweepSimultions.isEmpty()) {
				//System.out.println("La lista de simulaciones est� vac�a");
				return null;
			}

			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ParameterSweepSimulation.class, new ParameterSweepSimulationSerializer(getOriginalRef(),((DNSE3OrchestrationApplication) getApplication()).getDataController(),true)).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(parameterSweepSimultions));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}

	/**
	 * M�todo para agegar una simulaci�n de barrido
	 * @param request JSON con la informaci�n necesaria para agregar el par�metro a la simulaci�n
	 */
	@Post("json")
	public void postJson(JsonRepresentation request){
		try{
			//Revisi�n del contenido de la petici�n
			JSONObject obj = request.getJsonObject();
			if(!obj.has("name")||!obj.has("outputFiles")){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Formato de JSON no v�lido");
			}

			JSONArray outputFilesArray = obj.getJSONArray("outputFiles");
			if(outputFilesArray.length()==0){
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "La cantidad de outFiles tiene que ser mayor a uno");
			}

			//Extracci�n de los atributos para crear la simulaci�n
			String simulationName = obj.getString("name");
			int numRepetitions = 1;
			int priority = 50;
			ArrayList<String> outputFiles = new ArrayList<>();
			HashMap<String, ParameterMapper> parameters = new HashMap<>();

			for(int i=0; i<outputFilesArray.length(); i++){
				String outputFileName = outputFilesArray.getString(i);
				System.out.println("outputFileName: "+outputFileName);
				if(outputFiles.contains(outputFileName)){//Fichero repetido
					throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON no v�lido. Fichero "+outputFileName+" repetido");
				}
				outputFiles.add(outputFileName);
			}

			if(obj.has("numRepetitions"))
				numRepetitions = obj.getInt("numRepetitions");

			if(obj.has("priority"))
				priority = obj.getInt("priority");

			JSONArray parametersArray = new JSONArray();
			if(obj.has("parameters")){
				parametersArray = obj.getJSONArray("parameters");
				if(parametersArray.length()>0){
					for(int i=0; i<parametersArray.length(); i++){
						JSONObject p = parametersArray.getJSONObject(i);
						if(!p.has("name")){
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "El par�metro no tiene nombre");
						}
						String parameterName = p.getString("name");
						if(parameters.containsKey(parameterName)){ //Parametro repetido
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "El par�metro est� repetido");
						}
						try{
							parameters.put(parameterName, new ParameterMapper(p, true));
						}
						catch(Exception e){
							throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
						}
					}
				}
			}

			long simulationId = ((DNSE3OrchestrationApplication) getApplication()).getDataController().createParameterSweepSimulation(simulationName, numRepetitions, priority, outputFiles, parameters, projectId);
			setLocationRef(getOriginalRef().toString()+simulationId+"/");
			
			String extra = "{Type:pss,Repetitions:"+numRepetitions+",Parameters:"+parametersArray.toString()+",Outputfiles:"+outputFiles.toString()+"}";
			try {
				if(obj.has("tiempPrep"))
					RegistraLog.registra(RegistraLog.nueSimula, username, projectId, simulationId, obj.getLong("tiempoPrep"), extra);
				else
					RegistraLog.registra(RegistraLog.nueSimula, username, projectId, simulationId, extra);
			} catch (Exception e) {
				RegistraLog.registra(RegistraLog.nueSimula, username, projectId, simulationId, extra);
			}
			//usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - NUEVA SIMULACI�N DE BARRIDO - Repetitons: " + numRepetitions + " - " + parametersArray.toString() + " - " + outputFilesArray.toString());
			setStatus(Status.SUCCESS_CREATED);
		}
		catch(Exception e){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
	}
}
