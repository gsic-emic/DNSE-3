
import java.io.IOException;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import dnse3.common.MetricType;
import dnse3.common.Task;
import dnse3.common.TaskException;
import dnse3.queue.data.TaskQueue;
import dnse3.queue.server.DNSE3QueueApplication;

public class TaskQueueResource extends ServerResource {
	private TaskQueue queue;
	
	@Override
	public void doInit(){
		this.queue=((DNSE3ReportApplication) getApplication()).getTaskQueue();
	}
	
	@Get("json")
	public Representation getJson(){
		Collection<Task> tasksQueue = queue.getTasks();
		if(tasksQueue.isEmpty()){
			setStatus(Status.SUCCESS_NO_CONTENT); //If there are no tasks in the queue
			return null;
		}
		try{
			JSONArray array = new JSONArray();
			for(Task task:queue.getTasks()){ //For each task, a new entry is set in the JSON Array
				JSONObject obj = new JSONObject();
				obj.put("taskID", task.getTaskID());
				obj.put("status", task.getStatus().toString());
				obj.put("uri", getReference().getTargetRef().toString()+task.getTaskID());
				array.put(obj);
			}
			JsonRepresentation rep=new JsonRepresentation(array);
			rep.setIndenting(true);
			setStatus(Status.SUCCESS_OK);
			return rep;
		} catch (JSONException e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Get("xml")
	public Representation getXml(){
		Collection<Task> tasksQueue = queue.getTasks();
		if(tasksQueue.isEmpty()){
			setStatus(Status.SUCCESS_NO_CONTENT); //If there are no tasks in the queue
			return null;
		}
		try{
			DomRepresentation rep = new DomRepresentation();
			rep.setIndenting(true);
			Document doc = rep.getDocument();
			
			Node taskArray = doc.createElement("tasks");
			doc.appendChild(taskArray);
			
			for(Task task:queue.getTasks()){ //For each task, a new entry is set in the JSON Array
				Node taskElt = doc.createElement("task");
				taskArray.appendChild(taskElt);
				
				Node taskID = doc.createElement("taskId");
				taskID.setTextContent(task.getTaskID());
				taskElt.appendChild(taskID);
				
				Node status = doc.createElement("status");
				status.setTextContent(task.getStatus().toString());
				taskElt.appendChild(status);
				
				Node uri = doc.createElement("uri");
				uri.setTextContent(getReference().getTargetRef().toString()+task.getTaskID());
				taskElt.appendChild(uri);
			}
			setStatus(Status.SUCCESS_OK);
			return rep;
		} catch (IOException e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Post("json")
	public void postJson(JsonRepresentation request){
		try {
			System.out.println("Hola");
			JSONObject newTask = request.getJsonObject();
			System.out.println("Cojo el documento");
			String taskID = queue.addNewTask(newTask);
			System.out.println("Creo la tarea");
			setLocationRef(getReference().getTargetRef().toString()+taskID); //Add the reference to the new task
			setStatus(Status.SUCCESS_CREATED);
		} catch (TaskException | JSONException e) {
			System.out.println("Error antes de crear");
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST); //The document sent was not valid
		}
	}


//	@Post("xml")
//	public void postJson(DomRepresentation request){
//		try {
//			Document doc = request.getDocument();
//			String idTask = queue.addNewTask(doc);
//			setLocationRef(getReference().getTargetRef().toString()+idTask); //Add the reference to the new task
//			setStatus(Status.SUCCESS_CREATED);
//		} catch (TaskException | IOException e) {
//			setStatus(Status.CLIENT_ERROR_BAD_REQUEST); //The document sent was not valid
//		}
//	}
}
