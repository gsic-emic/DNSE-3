package dnse3.queue.resources;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Patch;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.common.tasks.Task;
import dnse3.common.tasks.TaskSerializer;
import dnse3.common.TaskException;
import dnse3.queue.data.TaskQueue;
import dnse3.queue.server.DNSE3QueueApplication;

/**
 * Clase creada para gestionar una tarea de manera individual
 * 
 * @author GSIC gsic.uva.es
 * @version 20191113
 */
public class TaskResource extends ServerResource {
 
    /** Instancia de la cola */
    private TaskQueue queue;
    /** Tipo de cola */
    private String typeQueue;
    /** Identificador de la tarea */
    private String taskID;
    
    /**
     * Método que se inicia cada vez que la clase es creada. Establece el tipo de 
     * cola, recupera la instacia de la cola y establece el identificador de la tarea.
     */
    @Override
    public void doInit(){
        typeQueue = getAttribute("typeQueue");
        switch(typeQueue){
        case "simulationqueue":
        case "queue":
            queue = ((DNSE3QueueApplication) getApplication()).getSimulationQueue();
            break;
        case "reportqueue":
            queue = ((DNSE3QueueApplication) getApplication()).getReportQueue();
            break;
        default:
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
        taskID = getAttribute("taskID"); //Get the ID of the task from the URI
    }
    
    /**
     * Método que devuelve la información relacionada con la tarea solicitada
     * @return JSON con la información que tenga el Sistema de Colas de la tarea
     * solicitada.
     */
    @Get("json")
    public Representation getJson(){
        try{
            Task task = queue.getTask(taskID); //Recover the task
            if(task == null)
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
            
            //Generate the response document
            Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskSerializer()).setPrettyPrinting().create();
            JsonRepresentation rep = new JsonRepresentation(gson.toJson(task));
            rep.setTag(task.geteTag());
            rep.setIndenting(true);
            setStatus(Status.SUCCESS_OK);
            return rep;
            
        }
        catch (JSONException e){
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
    }
    


    /**
     * Método para modificar el estado en el que se encuentra la tarea
     * @param request JSON con los datos que venga de la petición
     * @return Respuesta con su tag
     */
    @Patch("json-patch")
    public Representation patchJson(JsonRepresentation request){
    //public synchronized Representation patchJson(JsonRepresentation request){
        //Se comprueba mayúscula y minúscula (Jetty y servidor interno Restlet)
        if(getRequest().getHeaders().getNames().contains("If-match")||getRequest().getHeaders().getNames().contains("If-Match")){ //If-Match header used in the request. If so, it was previously check if it was valid
            try {
                //Check the document passed is valid
                JSONArray array = request.getJsonArray();
                for (int i = 0, length = array.length(); i < length; i++) {
                    JSONObject obj = array.getJSONObject(i);
                    switch (obj.getString("op").toLowerCase()) {
                    case "test":
                    case "remove":
                    case "add":
                    case "replace":
                    case "move":
                    case "copy":
                        break;
                    default:
                        System.err.println("Error: " + obj.toString());
                        throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
                    }
                }
                
                Representation rep = new StringRepresentation(""); //Empty Representation, for adding the ETag
                setStatus(Status.SUCCESS_NO_CONTENT);
            	rep.setTag(queue.updateTask(taskID, array, getClientInfo().getAddress()));//Pass the request to the queue
            	return rep;
                
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST); //Not well formed document in the request
            } catch (TaskException e) {
                e.printStackTrace();
                switch(e.getError()){
                case BAD_REQUEST: //Values used in the request were not valid
                    throw new ResourceException(Status.CLIENT_ERROR_UNPROCESSABLE_ENTITY);
                case NOT_FOUND: //Task not found
                    throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
                default:
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
                }
            }
        }
        else
            throw new ResourceException(Status.CLIENT_ERROR_PRECONDITION_FAILED); //No If-Match header, cancelled the request
        
    }
    
    /**
     * Método para eliminar una tarea del sistema
     */
    @Delete
    public void remove() {
    //public syncrhonized void remove() {
        try{
            queue.removeTask(taskID);
            setStatus(Status.SUCCESS_NO_CONTENT);
        }
        catch(TaskException e){
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
    }

}
