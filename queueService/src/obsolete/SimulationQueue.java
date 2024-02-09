package obsolete;
import org.json.JSONObject;
import org.restlet.data.Tag;

import dnse3.common.Simulation;
import dnse3.common.Task;
import dnse3.common.TaskException;
import dnse3.common.TaskExceptionEnum;
import dnse3.common.TaskStatusEnum;

import org.json.JSONArray;
import org.json.JSONException;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SimulationQueue implements TaskQueue { //Podría tener el gestor de la nube, para no tener que publicar el recurso la métrica, voy a hacerlo para evitar tener que publicar muchas seguidas, ceilometer tarda y bastante

	private LinkedHashMap<String, Simulation> simulationQueue;
	private SecureRandom idGenerator = new SecureRandom();
	private int availableSize;

	public SimulationQueue() {
		this.simulationQueue = new LinkedHashMap<String, Simulation>();
		availableSize=0;
	}

	public Task getTask(String taskID) throws TaskException {
		if (simulationQueue.containsKey(taskID))
			return simulationQueue.get(taskID);
		else
			throw new TaskException(TaskExceptionEnum.NOT_FOUND);
	}

	public Task getNextTask(){
		for (Simulation sim : simulationQueue.values()) {
			switch (sim.getStatus()) { //Check the status of the task
			case WAITING: //If it is waiting, return it
				return sim;
			case PROCESSING: //If it is processing, check if it has expired
				if(sim.isExpired())
					return sim;
				else 
					continue;
			case FINISHED:
			case ERROR:
				continue;
			}
		}
		return null;
	}

	public Collection<Task> getTasks() {
		return new ArrayList<Task>(simulationQueue.values());
	}

	public String addNewTask(JSONObject document) throws TaskException {
		if (document.has("input") && document.has("parameters") && document.has("saveGeneratedFiles") && document.has("environmentVariables") && document.has("outputPattern") && document.has("packageID") && document.has("simDescriptionID")) { //check the presence of those required fields
			try{
				Simulation sim;
				String id;
				
				JSONArray parametersArray = document.getJSONArray("parameters");
				HashMap<String,String> parameters = new HashMap<String,String>();
				for(int i=0; i<parametersArray.length(); i++){
					JSONObject obj = parametersArray.getJSONObject(i);
					parameters.put(obj.getString("name"), obj.getString("value"));
				}
				
				JSONArray envArray = document.getJSONArray("environmentVariables");
				HashMap<String,String> environment = new HashMap<String,String>();
				for(int i=0; i<envArray.length(); i++){
					JSONObject obj = envArray.getJSONObject(i);
					environment.put(obj.getString("name"), obj.getString("value"));
				}
				
				do {
					id = new BigInteger(65, idGenerator).toString(32); //generate a new random id
				} while (simulationQueue.containsKey(id));
				if (document.has("renewalTime")) //optional field
					sim = new Simulation(id, document.getString("packageID"), document.getString("simDescriptionID"), new URI(document.getString("input")),environment,parameters,document.getString("outputPattern"),document.getBoolean("saveGeneratedFiles"),document.getInt("renewalTime"));
				else
					sim = new Simulation(id, document.getString("packageID"), document.getString("simDescriptionID"), new URI(document.getString("input")),environment,parameters,document.getString("outputPattern"),document.getBoolean("saveGeneratedFiles"));
				
				simulationQueue.put(id, sim);
				availableSize++;
				return id;
			}
			catch(JSONException|URISyntaxException e){
				e.printStackTrace();
				throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
			}
		} else
			throw new TaskException(TaskExceptionEnum.BAD_REQUEST); //The request was not valid
	}
	
//	public String addNewTask(Document document) throws TaskException { //Voy a comentarlo de momento para testear, mÃ¡s adelante termino el desarrollo XML
//		Element taskElt = (Element) document.getElementsByTagName("task").item(0);
//		if(taskElt == null)
//			throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
//		else{
//			Element message = (Element) taskElt.getElementsByTagName("message").item(0);
//			Element idPackage = (Element) taskElt.getElementsByTagName("idPackage").item(0);
//			
//			Element parametersArray = (Element) taskElt.getElementsByTagName("parameters").item(0);
//			Element envArray = (Element) taskElt.getElementsByTagName("environmentVariables").item(0);
//			
//			HashMap<String,String> parameters = new HashMap<String,String>();
//			for(int i=0; i<; i++){
//				JSONObject obj = parametersArray.getJSONObject(i);
//				parameters.put(obj.getString("name"), obj.getString("value"));
//			}
//			
//			JSONArray envArray = document.getJSONArray("environmetVariables");
//			HashMap<String,String> environment = new HashMap<String,String>();
//			for(int i=0; i<envArray.length(); i++){
//				JSONObject obj = envArray.getJSONObject(i);
//				environment.put(obj.getString("name"), obj.getString("value"));
//			}
//			
//			if(message == null || idPackage == null)
//				throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
//			else{
//				Simulation sim;
//				String id;
//				
//				do {
//					id = new BigInteger(65, idGenerator).toString(32); //generate a new random id
//				} while (simulationQueue.containsKey(id));
//				
//				Element renewalTime = (Element) taskElt.getElementsByTagName("renewalTime").item(0);
//				
//				if(renewalTime==null)
//					sim = new Simulation(id, idPackage.getTextContent(), message.getTextContent());
//				else
//					sim = new Simulation(id, idPackage.getTextContent(), message.getTextContent(), Integer.parseInt(renewalTime.getTextContent()));
//				simulationQueue.put(id, sim);
//				return id;
//			}
//		}
//	}

	public void deleteTask(String taskID) throws TaskException {
		if (simulationQueue.containsKey(taskID)) {
			if (simulationQueue.get(taskID).getStatus().equals(TaskStatusEnum.PROCESSING))
				simulationQueue.get(taskID).stopProcessing(); //If the task is being processed, stop it first
			if(simulationQueue.get(taskID).getStatus().equals(TaskStatusEnum.WAITING)||simulationQueue.get(taskID).getStatus().equals(TaskStatusEnum.PROCESSING))
				availableSize--;
			simulationQueue.remove(taskID);
		} else
			throw new TaskException(TaskExceptionEnum.NOT_FOUND); //There was no task with that ID
	}

	public Tag udateTask(String taskID, JSONArray array, String address, String orchestrationAddress) throws TaskException {
		if (simulationQueue.containsKey(taskID)) {
			Simulation sim = new Simulation(simulationQueue.get(taskID)); //Temp copy of the simulation, to avoid overwriting if the request is not valid
			try{
				for (int i = 0, length = array.length(); i < length; i++) { //Iterate through the JSON Array from the request
					JSONObject obj = array.getJSONObject(i);
					if(obj.getString("op").equals("replace")){ //we just allow the replace operation
						switch (obj.getString("path")) {
						case "/status":
							sim.setStatus(TaskStatusEnum.valueOf(obj.getString("value")));
							break;
						case "/expirationDate":
							sim.renewTask(); // As its change automatically, the value field is not needed
							break;
						case "/uriWorker":
							System.out.println(address);
							new URL("http://"+address+obj.getString("value")); //First, check the URI is wellformed
							sim.setUriWorker("http://"+address+obj.getString("value"));
						}
					}
					else
						throw new TaskException(TaskExceptionEnum.BAD_REQUEST); //Field not valid for the request
				}
				sim.changeETag(); //set a new ETag
				simulationQueue.replace(taskID, sim); // The new simulation, completely updated, replaces the old value
				if(sim.getStatus().equals(TaskStatusEnum.FINISHED)||sim.getStatus().equals(TaskStatusEnum.ERROR)){
					availableSize--;
					//Notify the orchestration service, create new thread
					//new OrchestrationNotifier(sim, orchestrationAddress).start();
				}
				return sim.getETag();
			} catch (IllegalArgumentException | NullPointerException | MalformedURLException | JSONException | URISyntaxException e){
				throw new TaskException(TaskExceptionEnum.BAD_REQUEST); //Malformed request
			}
		} else {
			throw new TaskException(TaskExceptionEnum.NOT_FOUND); //No task with that ID
		}
	}
	
	public Tag getETag(String taskID) throws TaskException{
		if(simulationQueue.containsKey(taskID)){
			return simulationQueue.get(taskID).getETag();
		}
		else
			throw new TaskException(TaskExceptionEnum.NOT_FOUND); //NOT_FOUND
	}
	
	public int getSize(){
		return availableSize;
	}
}
