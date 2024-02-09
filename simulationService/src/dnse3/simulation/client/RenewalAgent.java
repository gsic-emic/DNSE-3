package dnse3.simulation.client;
import java.io.IOException;

//import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.restlet.Client;
//import org.restlet.data.MediaType;
import org.restlet.data.Tag;
//import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
//import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import dnse3.common.tasks.Task;
import dnse3.common.TaskStatusEnum;

public class RenewalAgent implements Runnable {

	private Task task;
	private boolean running;
	private boolean error;
	private boolean aborted;
	private String queueAddress;
	private Worker worker;
	
	/**
	 * Constructor de la clase RenewalAgent.
	 * @param task tarea a almacenar en la variable privada.
	 * @param queueAddress dirección a almacenar en la variable privada.
	 * @param worker trabajador a almacenar en la variable privada.
	 */
	public RenewalAgent (Task task, String queueAddress, Worker worker){
		this.task=task;
		this.queueAddress=queueAddress;
		this.worker=worker;
		this.running=true;
		this.error=false;
		this.aborted=false;
	}
	
	public void run(){
		System.out.println("http://"+queueAddress+"/v0.1/queue/"+task.getId());
		// ClientResource renewService = new ClientResource("http://"+queueAddress+"/v0.1/simulationqueue/"+task.getId()+"?method=patch"); //Need to make this tunnel as it sends a POST instead of PATCH
		JSONArray patch;
		boolean first=true;
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		try{
			while(running){	
				System.out.println("ID: "+task.getId());
				System.out.println("Listener: "+task.getListener());
				System.out.println("SRC: "+task.getSrc());
				System.out.println("Status: "+task.getStatus());
				
				patch = new JSONArray();
				JSONObject renewObject = new JSONObject();
				renewObject.put("op","replace");
				renewObject.put("path", "/expirationDate");
				patch.put(renewObject);

				if(task.getStatus().equals(TaskStatusEnum.WAITING)){ //Leave the op replace as it's also hat operation
					JSONObject renewStatus = new JSONObject();
					renewStatus.put("op", "replace");
					renewStatus.put("path", "/status");
					renewStatus.put("value", TaskStatusEnum.PROCESSING);
					patch.put(renewStatus);
					task.setStatus(TaskStatusEnum.PROCESSING);
				}
				
				System.out.println("Sending PATCH");
				if(first){
					JSONObject renewWorker = new JSONObject();
					renewWorker.put("op", "replace");
					renewWorker.put("path", "/uriWorker");
					renewWorker.put("value", ":8082/v0.1/simulationClient/"+task.getId());
					patch.put(renewWorker);
					first=false;
				}
				
				//falta añadir uriWorker, esperar a tener el servidor
				System.err.println("Path donde hace la renovación: " + "http://"+queueAddress+"/v0.1/simulationqueue/"+task.getId());
				HttpPatch patchRequest = new HttpPatch("http://"+queueAddress+"/v0.1/simulationqueue/"+task.getId());
				
				StringEntity body = new StringEntity(patch.toString(), "UTF-8");
				body.setContentType("application/json-patch");
				patchRequest.setEntity(body);
				patchRequest.addHeader("If-Match", task.geteTag().getName());
				
				response=httpClient.execute(patchRequest);
				
				if(response.getStatusLine().getStatusCode()==204){
					task.seteTag(new Tag(response.getFirstHeader("Etag").getValue()));
					synchronized(this){
						wait(task.getRenewalTime()*1000);
					}
				}
				else if(response.getStatusLine().getStatusCode()==404){
					System.err.println("Simulation does not exist anymore in Queue: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
					worker.stopSimulation();
					break;
				}
				else if(response.getStatusLine().getStatusCode()==412){
					System.err.println("Simulation no longer reserved for this instance: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
					worker.stopSimulation();
					break;
				}
				else{
					System.err.println("Error in update: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
					worker.stopSimulation();
					break;
				}
				
				// JsonRepresentation request = new JsonRepresentation(patch);
				// renewService.getConditions().getMatch().clear();
				// renewService.getConditions().getMatch().add(task.geteTag());
				// renewService.accept(MediaType.APPLICATION_JSON_PATCH);
				
				// Representation rep = renewService.post(request); //With service.patch(patch), it sends a POST :S
				
				// System.out.println("PATCH Sent");
				// task.seteTag(rep.getTag());
				// renewService.getConditions().getMatch().clear();
				
				// System.out.println(task.geteTag().toString());
				
				// synchronized(this){
				// 	wait(task.getRenewalTime()*1000);
				// }
			}
		} catch (InterruptedException e){
			e.printStackTrace();
			System.out.println("Stopped");
		} catch(ResourceException e){ //Failed the request, notify the worker to stop the execution
			e.printStackTrace();
			worker.stopSimulation();		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!aborted) {
			if(!error) {
				try {
					patch = new JSONArray(); //Prepare the JSON document to notify of the success in the simulation
					JSONObject errorObject = new JSONObject();
					errorObject.put("op", "replace");
					errorObject.put("path", "/status");
					errorObject.put("value", TaskStatusEnum.FINISHED);
					patch.put(errorObject);
					
					HttpPatch patchRequest = new HttpPatch("http://"+queueAddress+"/v0.1/simulationqueue/"+task.getId());
					
					StringEntity body = new StringEntity(patch.toString(), "UTF-8");
					body.setContentType("application/json-patch");
					patchRequest.setEntity(body);
					patchRequest.addHeader("If-Match", task.geteTag().getName());
					
					response=httpClient.execute(patchRequest);
					
					if(response.getStatusLine().getStatusCode()==204){
						System.err.println("Task completed");
					}
					else if(response.getStatusLine().getStatusCode()==404){
						System.err.println("Simulation does not exist anymore in Queue: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
					}
					else if(response.getStatusLine().getStatusCode()==412){
						System.err.println("Simulation no longer reserved for this instance: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
					}
					else{
						System.err.println("Error in update: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
					}

				// 	JsonRepresentation request = new JsonRepresentation(patch);
				// 	renewService.getConditions().getMatch().clear();
				// 	renewService.getConditions().getMatch().add(task.geteTag());
				// 	renewService.accept(MediaType.APPLICATION_JSON_PATCH);

				// 	renewService.post(request);
				// 	renewService.release();
				// } catch (ResourceException e) {
				// 	System.out.println("Simulation removed before notifying");
				// 	e.printStackTrace();
				} catch (JSONException | IOException e){
					System.out.println("Error preparing request");
					e.printStackTrace();
				}
			} 
		}
		
		// discardRepresentation(renewService.getResponseEntity());
		// service.release();

		// Client c = (Client) renewService.getNext();
		// try{
		// 	c.stop();
		// }catch(Exception e){
			
		// }
	}
	
	public void stopRunning(){
		this.running=false;
		synchronized (this) {
			this.notify();
		}
	}
	
	public void notifyError(){
		this.error=true;
		stopRunning();
		
		// ClientResource service = new ClientResource("http://"+queueAddress+"/v0.1/simulationqueue/"+task.getId()+"?method=patch"); //Need to make this tunnel as it sends a POST instead of PATCH
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		try{
			JSONArray patch = new JSONArray();
			JSONObject errorObject = new JSONObject(); //Prepare the JSON document to notify of an error in the simulation
			errorObject.put("op", "replace");
			errorObject.put("path", "/status");
			errorObject.put("value", TaskStatusEnum.ERROR);
			patch.put(errorObject);
			
			HttpPatch patchRequest = new HttpPatch("http://"+queueAddress+"/v0.1/simulationqueue/"+task.getId());
			
			StringEntity body = new StringEntity(patch.toString(), "UTF-8");
			body.setContentType("application/json-patch");
			patchRequest.setEntity(body);
			patchRequest.addHeader("If-Match", task.geteTag().getName());
			
			response=httpClient.execute(patchRequest);
			
			if(response.getStatusLine().getStatusCode()==204){
				System.err.println("Error notified");
			}
			else if(response.getStatusLine().getStatusCode()==404){
				System.err.println("Simulation does not exist anymore in Queue: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
			}
			else if(response.getStatusLine().getStatusCode()==412){
				System.err.println("Simulation no longer reserved for this instance: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
			}
			else{
				System.err.println("Error in update: "+response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
			}
	
			// JsonRepresentation request = new JsonRepresentation(patch);
			// service.getConditions().getMatch().add(task.geteTag());
			// service.accept(MediaType.APPLICATION_JSON_PATCH);
	
			// service.post(request);
		}
		catch(JSONException | IOException e){
			System.out.println("Error notification not achieved. Aborted!");
		}
		// discardRepresentation(service.getResponseEntity());
		// service.release();
		// Client c = (Client) service.getNext();
		// try{
		// 	c.stop();
		// }catch(Exception e){
			
		// }
	}
	
	public void setAborted(boolean aborted){
		this.aborted=aborted;
		stopRunning();
	}
	
	public void discardRepresentation(Representation rep){
		if(rep!=null){
			try{
				rep.exhaust();
			}
			catch (IOException e){
				//Notificación 2º error producido de forma conjunta
			}
			rep.release();
		}
	}
	
	public void setTask(Task task){
		this.task=task;
		this.running=true;
		this.error=false;
		this.aborted=false;
	}
}
