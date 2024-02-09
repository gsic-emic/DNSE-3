package dnse3.queue.resources;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import dnse3.common.TaskException;
import dnse3.common.tasks.OutputFileSummary;
import dnse3.common.tasks.Task;
import dnse3.queue.data.TaskQueue;
import dnse3.queue.server.DNSE3QueueApplication;

/**
 * Clase utilizada para listar, crear o actualizar alguno de las colas del sistema
 * 
 * @author GSIC gsic.uva.es
 * @version 20191113
 */
public class TaskQueueResource extends ServerResource {
    /** Instancia de la cola */
    private TaskQueue queue;
    /** Tipo de cola que se está utilizando */
    private String typeQueue;
    
    /**
     * Método que se ejecuta cada vez que se realiza una llamada a esta clase. Es el 
     * encargado de definir el tipo de cola.
     */
    @Override
    public void doInit(){
        typeQueue = getAttribute("typeQueue");
        switch(typeQueue){
        case "simulationqueue":
        case "queue":
            this.queue = ((DNSE3QueueApplication) getApplication()).getSimulationQueue();
            break;
        case "reportqueue":
            this.queue = ((DNSE3QueueApplication) getApplication()).getReportQueue();
            break;
        default:
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
    }
    
    /**
     * Método para recuperar la lista de tareas en el Servicio de Colas
     * @return JSON con la lista de tareas. Null si la lista de tareas está vacía
     */
    @Get("json")
    public Representation getJson(){
        List<Task> tasksQueue = queue.getTasks();
        if(tasksQueue.isEmpty()){
            setStatus(Status.SUCCESS_NO_CONTENT); //If there are no tasks in the queue
            return null;
        }
        try{
            JSONArray array = new JSONArray();
            for(Task task:queue.getTasks()){ //For each task, a new entry is set in the JSON Array
                JSONObject obj = new JSONObject();
                obj.put("taskID", task.getId());
                obj.put("status", task.getStatus().toString());
                obj.put("uri", getReference().getTargetRef().toString() + task.getId());
                array.put(obj);
            }
            JsonRepresentation rep = new JsonRepresentation(array);
            rep.setIndenting(true);
            setStatus(Status.SUCCESS_OK);
            return rep;
        } catch (JSONException e){
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
    }
    
    /**
     * Método para crear una nueva tarea en el Sistema de Colas
     * @param request JSON donde están las propiedades de la nueva tarea
     */
    @Post("json")
    public void postJson(JsonRepresentation request){
        try {
            JSONObject newTask = request.getJsonObject(); // JSON con las propiedades de la nueva tarea
            //System.out.println(newTask.toString()); //Puede no tener parámetros
            if(!newTask.has("username") || !newTask.has("src") || !newTask.has("parameters") || !newTask.has("outputFiles") || !newTask.has("listener"))
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
            //System.out.println("Valid Task");
            JSONArray parametersJSON = newTask.getJSONArray("parameters");
            JSONArray outputFilesJSON = newTask.getJSONArray("outputFiles");
            
            HashMap<String,String> parameters = new HashMap<>();
            for(int i=0; i<parametersJSON.length(); i++){
                //System.out.println("Reading Parameter");
                JSONObject parameter = parametersJSON.getJSONObject(i);
                parameters.put(parameter.getString("name"), parameter.getString("value"));
            }
            //System.out.println("Valid Parameters");
            
            List<OutputFileSummary> outputFiles = new ArrayList<>(); 
            for(int i=0; i<outputFilesJSON.length(); i++){
                JSONObject outputObj = outputFilesJSON.getJSONObject(i);
                //System.out.println("Reading OutputFile");
                ArrayList<String> variables = new ArrayList<>();
                if(outputObj.has("outputVariables")){
                    JSONArray varArray = outputObj.getJSONArray("outputVariables");
                    for(int j=0; j< varArray.length(); j++)
                        variables.add(varArray.getString(j));
                }
                outputFiles.add(new OutputFileSummary(outputObj.getString("name"),outputObj.getBoolean("multiLine"),variables));
            }
            //System.out.println("Valid Files");
            String id = null;
            switch(typeQueue){
            case "simulationqueue":
            case "queue":
                id = ((DNSE3QueueApplication) getApplication()).generateNewSimulationId();
                break;
            case "reportqueue":
                id = ((DNSE3QueueApplication) getApplication()).generateNewReportId();
                break;
            default:
                throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
            }            
            Task task;
            System.out.println("Creating Task");
            if(newTask.has("renewalTime")){
                if(newTask.has("priority")){
                    task = new Task(newTask.getInt("priority"), id, newTask.getString("username"), newTask.getString("src"), parameters, outputFiles, newTask.getString("listener"), newTask.getString("outputPath"), newTask.getInt("renewalTime"));
                }
                else{
                    task = new Task(id, newTask.getString("username"), newTask.getString("src"), parameters, outputFiles, newTask.getString("listener"), newTask.getString("outputPath"), newTask.getInt("renewalTime"));
                }
            }
            else{
                if(newTask.has("priority")){
                    task = new Task(newTask.getInt("priority"), id, newTask.getString("username"), newTask.getString("src"), parameters, outputFiles, newTask.getString("listener"), newTask.getString("outputPath"));
                }
                else{
                    task = new Task(id, newTask.getString("username"), newTask.getString("src"), parameters, outputFiles, newTask.getString("listener"), newTask.getString("outputPath"));
                }
            }
            //System.out.println("Adding Task");
            queue.addNewTask(task);
            //System.out.println("Tarea agregada a la cola " + task.getUsername() + " " + task.getId());
            setLocationRef(getReference().getTargetRef().toString()+task.getId()); //Add the reference to the new task
            System.out.println("Task created");
            setStatus(Status.SUCCESS_CREATED);
        } catch (TaskException | JSONException e) {
            System.err.println(e.getMessage());
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST); //The document sent was not valid
        }
    }

    /**
     * Método para actualizar el valor de la prioridad de un trabajo que ya esté en ejecución
     * @param representation JSON con lo información necesaria
     */
    @Put("json")
    public void putJson(JsonRepresentation representation){
        switch(typeQueue){
        case "simulationqueue":
        case "queue":
            try{
                JSONObject updateTask = representation.getJsonObject();
                //idJob es simulation.getProject().getId()+"/"+simulation.getId()
                if(!updateTask.has("username") || !updateTask.has("idJob") || !updateTask.has("priority")){
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
                }
                queue.updatePriority(updateTask.getString("username"), updateTask.getString("idJob"), updateTask.getInt("priority"));
                setStatus(Status.SUCCESS_OK);
            } catch(Exception e){
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage()); 
            }
            break;
        default:
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
        }
    }
}
