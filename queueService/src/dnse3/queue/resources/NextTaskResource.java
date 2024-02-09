package dnse3.queue.resources;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.common.tasks.Task;
import dnse3.common.tasks.TaskSerializer;
import dnse3.queue.data.TaskQueue;
import dnse3.queue.server.DNSE3QueueApplication;

/**
 * Clase genera da atender a las peticiones de qué tarea se debería procesar
 * 
 * @author GSIC gsic.uva.es
 * @version 20191113
 */
public class NextTaskResource extends ServerResource {

    /** Instancia del objeto de la cola de tareas */
    private TaskQueue queue;
    /** Nombre del tipo de cola sobre la que se va a trabajar */
    private String typeQueue;
    
    /**
     * Método de inicialización. Dependiendo del recurso al que se apunte establece el 
     * tipo de cola sobre la que se va a trabajar
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
     * Método utilizado para obtener la próxima tarea que se podría procesar de la cola 
     * sobre la que se esté trabajando
     * @return Información de la próxima tarea a procesar en formato JSON
     */
    @Get("json")
    public Representation getJson(){
    	Task task = null;
        task = queue.getNextTask();
        if(task!=null){
            try{
                //Adds the location of the task it points to
                setLocationRef("./"+task.getId());
                setStatus(Status.SUCCESS_OK);
                
                //Generate the response document
                JSONObject obj = new JSONObject();
                obj.put("taskID",task.getId());
                obj.put("src", task.getSrc());
                obj.put("outputFiles", task.getOutputFiles());
                obj.put("status", task.getStatus().toString());
                obj.put("renewalTime", task.getRenewalTime());
                obj.put("expirationDate", task.getExpirationDate());
                
                JSONArray param = new JSONArray();
                for(Entry<String,String> e:task.getParameters().entrySet()){
                    JSONObject o = new JSONObject();
                    o.put("name", e.getKey());
                    o.put("value", e.getValue());
                    param.put(o);
                }
                
                Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskSerializer()).setPrettyPrinting().create();
                JsonRepresentation rep = new JsonRepresentation(gson.toJson(task));
                
                rep.setIndenting(true);
                rep.setTag(task.geteTag());
                
                return rep;
            } catch (JSONException e){
                throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
            }
        }
        else{
            setStatus(Status.SUCCESS_NO_CONTENT); //There's no task to be processed, it's notified to the client
            return null;
        }
    }
}
