package dnse3.orchestration.resource;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

import dnse3.orchestration.auxiliar.ParameterMapper;
import dnse3.orchestration.auxiliar.RegistraLog;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulationSerializer;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class ParameterSweepSimulationResource extends ServerResource {

    /** Identificador de la simulaci�n */
    private long simulationId;
    private String username;
    private int projectId;
    //private static final Logger usersLogger = Logger.getLogger("DNSE3UsersLogger");
    
    /**
     * Iniciador. Comprueba que el proyecto pertenezca al usuario y que la simulaci�n perteneza al proyecto
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
            long simulationId = Long.valueOf(getAttribute("simulationId"));
            if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasSimulation(simulationId,projectId,SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION)){
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
            }
            //Falta comprobar que la simulaci�n no se est� eliminando
            this.simulationId = simulationId;
            this.username = username;
            this.projectId = projectId;
        }
        catch(NumberFormatException e){
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
    }
    
    /**
     * M�todo para obtener el JSON con la informaci�n de la simulaci�n de barrido
     * @return JSON con informaci�n de la simulaci�n
     */
    @Get("json")
    public JsonRepresentation getJson(){
        try{
            ParameterSweepSimulation simulation = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
            
            Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ParameterSweepSimulation.class, new ParameterSweepSimulationSerializer(getOriginalRef(),((DNSE3OrchestrationApplication) getApplication()).getDataController())).setPrettyPrinting().create();
            JsonRepresentation response = new JsonRepresentation(gson.toJson(simulation));
            response.setIndenting(true);
            return response;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
    }
    
    /**
     * M�todo para actualizar una simulaci�n de barrido de par�metros
     * @param request JSON con la informaci�n necesaria para editar un barrido de par�metros
     */
    @Put("json")
    public void putJson(JsonRepresentation request){
        try{
            JSONObject simulationObj = request.getJsonObject();
            System.out.println(simulationObj);
            if(!simulationObj.has("name")){
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Falta el campo name");
            }
            
            String simulationName = simulationObj.getString("name");
            int numRepetitions = 0;
            int priority = -1;
            ArrayList<String> outputFiles = null;
            HashMap<String, ParameterMapper> parameters = null;
            
            if(simulationObj.has("numRepetitions")){
                try {
                    numRepetitions = simulationObj.getInt("numRepetitions");
                } catch (JSONException e) {
                    numRepetitions = 0;
                }
            }
            
            if(simulationObj.has("priority")){
                try {
                    priority = simulationObj.getInt("priority");
                } catch (JSONException e) {
                    priority = -1;
                }
            }
            
            if(simulationObj.has("outputFiles")){
                JSONArray outputFilesArray = simulationObj.getJSONArray("outputFiles");
                if(outputFilesArray.length()==0){
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. El n�mero de ficheros de salida tiene que ser mayor a 0");
                }
                
                outputFiles = new ArrayList<>();
                for(int i=0; i<outputFilesArray.length(); i++){
                    String outputFileName = outputFilesArray.getString(i);
                    if(outputFiles.contains(outputFileName)){//Fichero repetido
                        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. El fichero de salida "+outputFileName+" est� repetido");
                    }
                    outputFiles.add(outputFileName);
                }
            }

            JSONArray parametersArray = new JSONArray();
            if(simulationObj.has("parameters")){
                parametersArray = simulationObj.getJSONArray("parameters");
                System.out.println(parametersArray);
                parameters = new HashMap<>();
                if(parametersArray.length()>0){
                    for(int i=0; i<parametersArray.length(); i++){
                        JSONObject p = parametersArray.getJSONObject(i);
                        System.out.println(p);
                        if(!p.has("name")){
                            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Par�metro sin name");
                        }
                        String parameterName = p.getString("name");
                        if(parameters.containsKey(parameterName)){ //Parametro repetido
                            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. El par�metro "+parameterName+" est� repetido");
                        }
                        try{
                            parameters.put(parameterName, new ParameterMapper(p, true));
                        }
                        catch(Exception e){
                            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
                        }
                    }
                }
            }
            ((DNSE3OrchestrationApplication) getApplication()).getDataController().updateParameterSweepSimulation(simulationId,simulationName,numRepetitions,priority,outputFiles,parameters);
            String extra = "{Type:pss,Repetitions:"+numRepetitions+",Parameters:"+parametersArray.toString()+",Outputfiles:"+outputFiles.toString()+"}";
			try {
				if(simulationObj.has("tiempPrep"))
					RegistraLog.registra(RegistraLog.modSimula, username, projectId, simulationId, simulationObj.getLong("tiempoPrep"), extra);
				else
					RegistraLog.registra(RegistraLog.modSimula, username, projectId, simulationId, extra);
			} catch (Exception e) {
				RegistraLog.registra(RegistraLog.modSimula, username, projectId, simulationId, extra);
			}
            //usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - EDICI�N SIMULACI�N DE BARRIDO - " + parametersArray.toString());
        }
        catch(Exception e){
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }
    
    /**
     * M�todo para cambiar el estado de una simulaci�n
     * @param request JSON con la operaci�n a ejecutar
     */
    @Post("json")
    public void postJson(JsonRepresentation request){
        try{
            JSONObject obj = request.getJsonObject();
            System.out.println(obj);
            if(!obj.has("operation")){
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "JSON mal formado. Falta la operaci�n a realizar");
            }
            
            switch(obj.getString("operation")){
            case "start":
            case "START":
                ((DNSE3OrchestrationApplication) getApplication()).getExecutionController().startSimulation(simulationId,SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
                break;
            case "pause":
            case "PAUSE":
                ((DNSE3OrchestrationApplication) getApplication()).getExecutionController().pauseSimulation(simulationId,SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
                RegistraLog.registra(RegistraLog.pauSimula, username, projectId, simulationId);
                //usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - PAUSA SIMULACI�N DE BARRIDO");
                break;
            case "stop":
            case "STOP":
                ((DNSE3OrchestrationApplication) getApplication()).getExecutionController().stopSimulation(simulationId,SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
                RegistraLog.registra(RegistraLog.stoSimula, username, projectId, simulationId);
                //usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - STOP SIMULACI�N DE BARRIDO");
                break;
            default:
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Operaci�n no soportada");
            }
            setStatus(Status.SUCCESS_ACCEPTED);
        }
        catch(JSONException e){
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    /**
     * M�todo para eliminar una simulaci�n del sistema
     */
    @Delete
    public void remove(){
        try{
            ((DNSE3OrchestrationApplication) getApplication()).getDataController().removeParameterSweepSimulation(simulationId);
            RegistraLog.registra(RegistraLog.borSimula, username, projectId, simulationId);
            //usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - DELETE SIMULACI�N DE BARRIDO");
            setStatus(Status.SUCCESS_ACCEPTED);
        }
        catch(Exception e){
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
    }
}
