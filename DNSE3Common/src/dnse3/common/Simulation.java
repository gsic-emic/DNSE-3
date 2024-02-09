package dnse3.common;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.Tag;

public class Simulation extends Task {
	//Class that defines the tasks used in the simulation client
	//Extends the Task class and adds new attributes
	
	private URI uriWorker; //URI to access the worker processing the simulation
	private String packageID; //ID of the package it belongs to, come from the Orchestation Service
	private String simDescriptionID; //ID of the sim. description it belongs to, come from the Orchestration Service
	private boolean saveGeneratedFiles; //Indicates the simulation client to save or not the files generated by the simulation
	
	public Simulation(){
		super();
		uriWorker=null;
		packageID=null;
		saveGeneratedFiles=false;
	}
	
	public Simulation(String taskID, String packageID, String simDescriptionID, URI input, HashMap<String,String> environmentVariables, HashMap<String,String> parameters, String outputPattern, boolean saveGeneratedFiles){
		super(taskID,input,environmentVariables,parameters,outputPattern);
		this.packageID=packageID;
		this.uriWorker=null;
		this.simDescriptionID=simDescriptionID;
		this.saveGeneratedFiles=saveGeneratedFiles;
	}
	
	public Simulation(String taskID, String packageID, String simDescriptionID, URI input, HashMap<String,String> environmentVariables, HashMap<String,String> parameters, String outputPattern,boolean saveGeneratedFiles, int renewalTime){
		super(taskID,input,environmentVariables,parameters,outputPattern, renewalTime);
		this.packageID=packageID;
		this.simDescriptionID=simDescriptionID;
		this.uriWorker=null;
		this.saveGeneratedFiles=saveGeneratedFiles;
	}
	
	public String getSimDescriptionID() {
		return simDescriptionID;
	}

	public Simulation(Simulation sim){
		super(sim.taskID,sim.input,sim.environmentVariables,sim.parameters,sim.outputPattern, sim.renewalTime, sim.expirationDate, sim.etag, sim.status);
		this.packageID=sim.packageID;
		this.simDescriptionID=sim.simDescriptionID;
		this.uriWorker=sim.uriWorker;
		this.saveGeneratedFiles=sim.saveGeneratedFiles;
	}
	
	public URI getUriWorker(){
		return this.uriWorker;
	}
	
	public String getPackageID(){
		return this.packageID;
	}
	
	public boolean saveFiles(){
		return saveGeneratedFiles;
	}
	
	public void setUriWorker(String uriWorker) throws URISyntaxException{
		//Should check the URI here instead of the SimulationQueue
		this.uriWorker=new URI(uriWorker);
	}
	public void setUriWorker(URI uriWorker){
		//Should check the URI here instead of the SimulationQueue
		this.uriWorker=uriWorker;
	}

	public void stopProcessing() {
		// TODO 
		//First, develop a preliminar version of the workers
		
	}
	
	//Updates the ETag of the resource after an update
	public void changeETag(){ 
		boolean changed=false;
		do{
			Tag newTag = new Tag(getNewRandomString()); //Based on random Strings
			if(!newTag.equals(etag)){
				etag = newTag;
				changed=true;
			}
		}while(!changed);
		
	}
	
	//Returns a Map with the new attributes to be used in the representation
	public Map<String,String> getAditionalFields(){
		HashMap<String, String> fields = new HashMap<String,String>();
		if(this.uriWorker!=null)
			fields.put("uriWorker", this.uriWorker.toString());
		if(this.packageID!=null)
			fields.put("packageID", this.packageID);
		if(this.simDescriptionID!=null)
			fields.put("simDescriptionID", this.simDescriptionID);
		System.out.println(this.saveGeneratedFiles);
		fields.put("saveGeneratedFiles", String.valueOf(this.saveGeneratedFiles));
		
		return fields;
	}

	public void setPackageID(String packageID) {
		this.packageID=packageID;
		
	}
	
	public void setSimDescriptionID(String simDescriptionID) {
		this.simDescriptionID = simDescriptionID;
	}

	public void setSaveGeneratedFiles(boolean saveGeneratedFiles){
		this.saveGeneratedFiles=saveGeneratedFiles;
	}

	public void clean() {
		super.clean();
		this.packageID="";
		this.uriWorker=null;
		
	}

}