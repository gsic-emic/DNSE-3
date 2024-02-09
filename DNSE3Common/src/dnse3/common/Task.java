package dnse3.common;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.restlet.data.Tag;

public abstract class Task {
	//Abstract class that defines the tasks used in the queue service. For its use we need to define the "real" task that will be managed
	
	protected URI input; //URI where the file used in the task is found
	protected String message;
	protected HashMap<String,String> environmentVariables; //Environment variables used in the realization of the task
	protected HashMap<String,String> parameters;
	protected String outputPattern; //Pattern used in the output files generated
	//protected URI callback; //URI a la que llamar cuando se notifique de error o de fin de proceso
	protected TaskStatusEnum status;
	protected int renewalTime; //Period of time to perform the booking of the task
	protected Instant expirationDate; //Date of expiration of the booking
	protected String taskID;
	protected Tag etag; //tag used for the conditional http calls
	protected static int defaultRenewalTime = 60; //Default renovation time
	private static SecureRandom etagGenerator = new SecureRandom();

	//Default construction
	public Task(String taskID, URI input, HashMap<String,String> environmentVariables, HashMap<String,String> parameters, String outputPattern){
		this.taskID = taskID;
		this.input=input;
		this.environmentVariables=environmentVariables;
		this.parameters=parameters;
		this.outputPattern=outputPattern;
		this.status = TaskStatusEnum.WAITING;
		this.renewalTime = getDefaultRenewalTime();
		this.expirationDate = Instant.now(); //Cambiar más adelante por Joda-Time
		this.etag = new Tag(getNewRandomString());
	}
	
	public Task(String taskID, URI input, HashMap<String,String> environmentVariables, HashMap<String,String> parameters, String outputPattern, int renewalTime){
		this.taskID = taskID;
		this.input=input;
		this.environmentVariables=environmentVariables;
		this.parameters=parameters;
		this.outputPattern=outputPattern;
		this.status = TaskStatusEnum.WAITING;
		this.renewalTime = renewalTime;
		this.expirationDate = Instant.now(); //Cambiar más adelante por Joda-Time
		this.etag = new Tag(getNewRandomString());
	}
	
	protected Task(String taskID, URI input, HashMap<String,String> environmentVariables, HashMap<String,String> parameters, String outputPattern, int renewalTime, Instant expirationDate, Tag etag, TaskStatusEnum status){
		this.taskID=taskID;
		this.input=input;
		this.environmentVariables=environmentVariables;
		this.parameters=parameters;
		this.outputPattern=outputPattern;
		this.renewalTime=renewalTime;
		this.expirationDate=expirationDate;
		this.etag=etag;
		this.status=status;
	}
	
	public Task() {
		taskID=null;
		message=null;
		status=null;
		renewalTime=getDefaultRenewalTime();
		expirationDate=null;
		etag=null;
	}

	public static int getDefaultRenewalTime(){
		return defaultRenewalTime;
	}
	
	protected static String getNewRandomString(){ //Generates a new String for the ETag, could be change for generating a new Tag
		return new BigInteger(35, etagGenerator).toString(32);
	}
	
	public static void setDefaultRenewalTime(int time){
		defaultRenewalTime=time;
	}

	public int getRenewalTime(){
		return this.renewalTime;
	}
	
	public String getExpirationTime(){
		return expirationDate.toString();
	}
	
	public TaskStatusEnum getStatus(){
		return this.status;
	}
	
	public String getTaskID(){
		return taskID;
	}
	
	public Tag getETag(){
		return etag;
	}
	
	public URI getInput(){
		return input;
	}
	
	public HashMap<String,String> getEnvironmentVariables(){
		return environmentVariables;
	}
	
	public HashMap<String,String> getParameters(){
		return parameters;
	}
	
	public String getOutputPattern(){
		return outputPattern;
	}
	
	//Abstarct methods
	public abstract void changeETag();
	
	public abstract Map<String,String> getAditionalFields();
	
	
	public void setStatus(TaskStatusEnum status){
		this.status=status;
	}
	
	public void setRenewalTime(int renewalTime){
		this.renewalTime=renewalTime;
	}
	
	public void setEtag(Tag etag){
		this.etag=etag;
	}
	
	public void setEtag(String tag){
		this.etag= new Tag(tag);
	}
	
	public void setIdTask(String idTask){
		this.taskID=idTask;
	}
	
	public void setInput(URI input){
		this.input=input;
	}
	
	public void setEnvironmentVariables(HashMap<String,String> environment){
		if(environment!=null)
			this.environmentVariables=environment;
	}
	
	public void setParameters(HashMap<String,String> parameters){
		if(parameters!=null)
			this.parameters=parameters;
	}
	
	public void setOutputPattern(String outputPattern){
		this.outputPattern=outputPattern;
	}
	
	//Checks if the task has expired
	public boolean isExpired(){
		return this.expirationDate.isBefore(Instant.now()); //Si la fecha es anterior, ha caducado
	}
	
	public void renewTask(){ //Renews the expiration date
		this.expirationDate = Instant.now().plusSeconds(renewalTime*2); //It's twice the renewal time, to allow variations in the time used in the rrequest
	}

	//Cleans the task, leaving an empty one
	public void clean() {
		this.environmentVariables= new HashMap<String,String>();
		this.input= null;
		this.outputPattern="";
		this.parameters= new HashMap<String,String>();
		this.taskID="";
		this.status=TaskStatusEnum.WAITING;
		
	}

}
