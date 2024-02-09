package dnse3.simulation.resources;

import org.json.JSONException;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.common.tasks.Task;
import dnse3.common.tasks.TaskSerializer;
import dnse3.simulation.client.WorkerException;
import dnse3.simulation.server.DNSE3SimulationApplication;

public class SimulationResource extends ServerResource {
	
	private Task simulation;
	private String simulationID;
	
	@Override
	public void doInit(){
		simulationID = getAttribute("simulationID");
		simulation= ((DNSE3SimulationApplication) getApplication()).getSimulation(simulationID);
		if(simulation==null)
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	}
	
	@Get("json")
	public JsonRepresentation getJSON(){
		try{
			//GSON parser
			//Generate the response document
			Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, new TaskSerializer()).setPrettyPrinting().create();			
			JsonRepresentation rep = new JsonRepresentation(gson.toJson(simulation));
			rep.setIndenting(true);
			setStatus(Status.SUCCESS_OK);
			return rep;
		} catch (JSONException e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
	
	@Delete
	public synchronized void remove(){
		try{
			((DNSE3SimulationApplication) getApplication()).notifyWorker(simulationID);
			setStatus(Status.SUCCESS_OK);
		}
		catch(WorkerException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
	}

}