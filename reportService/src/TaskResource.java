
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Patch;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import dnse3.common.Task;
import dnse3.common.TaskException;
import dnse3.queue.data.TaskQueue;
import dnse3.queue.server.DNSE3QueueApplication;


public class TaskResource extends ServerResource {
	
	private TaskQueue queue;
	private String taskID;
	
	@Override
	public void doInit(){
		this.queue=((DNSE3ReportApplication) getApplication()).getTaskQueue(); //Get the queue from the application
		this.taskID=getAttribute("taskID"); //Get the ID of the task from the URI
	}
	
	@Get("json")
	public Representation getJson(){
		try{
			Task task=queue.getTask(taskID); //Recover the task
			
			//Generate the response document
			JSONObject obj = new JSONObject();
			obj.put("taskID",taskID);
			obj.put("input", task.getInput().toString());
			obj.put("outputPattern", task.getOutputPattern());
			obj.put("status", task.getStatus().toString());
			obj.put("renewalTime", task.getRenewalTime());
			obj.put("expirationTime", task.getExpirationTime());
			
			JSONArray env = new JSONArray();
			for(Entry<String,String> e:task.getEnvironmentVariables().entrySet()){
				JSONObject o = new JSONObject();
				o.put("name", e.getKey());
				o.put("value", e.getValue());
				env.put(o);				
			}
			obj.put("environmentVariables", env);
			
			JSONArray param = new JSONArray();
			for(Entry<String,String> e:task.getParameters().entrySet()){
				JSONObject o = new JSONObject();
				o.put("name", e.getKey());
				o.put("value", e.getValue());
				param.put(o);				
			}
			obj.put("parameters", param);
			
			Map<String, String> additionalFields = task.getAditionalFields();
			
			if(!additionalFields.isEmpty()){
				for(Entry<String,String> entry : additionalFields.entrySet()){
					obj.put(entry.getKey(), entry.getValue());
				}
			}
			
			JsonRepresentation rep = new JsonRepresentation(obj);
			rep.setTag(task.getETag());
			rep.setIndenting(true);
			setStatus(Status.SUCCESS_OK);
			return rep;
		}
		catch (TaskException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND); //There's no task with that ID, notified to the client
		} catch (JSONException e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
//	@Get("xml")
//	public Representation getXml(){
//		try{
//			Task task=queue.getTask(idTask); //Recover the task
//			
//			//Generate the response document
//			//Prepare the response
//			DomRepresentation rep = new DomRepresentation();
//			rep.setIndenting(true);
//			rep.setTag(task.getETag());
//			
//			//Create de response document
//			Document doc = rep.getDocument();
//			Node taskElt = doc.createElement("simulation");
//			doc.appendChild(taskElt);
//			
//			Node idTask = doc.createElement("idTask");
//			idTask.setTextContent(task.getIdTask());
//			taskElt.appendChild(idTask);
//			
//			Node message = doc.createElement("message");
//			message.setTextContent(task.getMessage());
//			taskElt.appendChild(message);
//			
//			Node status = doc.createElement("status");
//			status.setTextContent(task.getStatus().toString());
//			taskElt.appendChild(status);
//			
//			Node renewalTime = doc.createElement("renewalTime");
//			renewalTime.setTextContent(Integer.toString(task.getRenewalTime()));
//			taskElt.appendChild(renewalTime);
//			
//			Node expirationTime = doc.createElement("expirationTime");
//			expirationTime.setTextContent(task.getExpirationTime());
//			taskElt.appendChild(expirationTime);
//			//Add the aditional fields
//			Map<String, String> additionalFields = task.getAditionalFields();
//			
//			if(!additionalFields.isEmpty()){
//				for(Entry<String,String> entry : additionalFields.entrySet()){
//					Node additional = doc.createElement(entry.getKey());
//					additional.setTextContent(entry.getValue());
//					taskElt.appendChild(additional);
//				}
//			}
//			
//			setStatus(Status.SUCCESS_OK);
//			
//			return rep;
//			
//		}
//		catch (TaskException e){
//			setStatus(Status.CLIENT_ERROR_NOT_FOUND); //There's no task with that ID, notified to the client
//		}
//		catch (IOException e){
//			setStatus(Status.SERVER_ERROR_INTERNAL);
//		}
//		return null;
//	}
	
	@Patch("json-patch")
	public Representation patchJson(JsonRepresentation request){
		if(getRequest().getHeaders().getNames().contains("If-match")){ //If-Match header used in the request. If so, it was previously check if it was valid
			try {
				//Check the document passed is valid
				JSONArray array = request.getJsonArray();
				boolean badrequest = false;
				for (int i = 0, length = array.length(); i < length && !badrequest; i++) {
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
						throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
					}
				}
				
				queue.udateTask(taskID, array, "",""); //Pass the request to the queue
				setStatus(Status.SUCCESS_NO_CONTENT);
				Representation rep = new StringRepresentation(""); //Empty Representation, for adding the ETag
				rep.setTag(queue.getETag(taskID));
				return rep;
			} catch (JSONException | NullPointerException e) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST); //Not well formed document in the request
			} catch (TaskException e) {
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
	
	@Delete
	public synchronized void remove(){
		try{
			queue.deleteTask(taskID);
			setStatus(Status.SUCCESS_NO_CONTENT);
		}
		catch(TaskException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}

}
