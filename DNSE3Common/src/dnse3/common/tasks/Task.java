package dnse3.common.tasks;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.data.Tag;

import dnse3.common.TaskStatusEnum;

public class Task {

	private String id;
	private String src; //Puede ser URI, Swift
	private Map<String, String> parameters;
	private List<OutputFileSummary> outputFiles;
	private TaskStatusEnum status;
	private int renewalTime;
	private Instant expirationDate;
	private Tag eTag;
	private String workerURI;
	private String username;
	private String listener;
	private String outputPath;
	private Map<String,String> errors;
	private int priority;
	
	private static int defaultRenewalTime = 15; //Default renovation time
	private static SecureRandom etagGenerator = new SecureRandom();
	
	public Task(String id,String username, String src, Map<String,String> parameters, List<OutputFileSummary> outputFiles, String listener, String outputPath){
		this.id = id;
		this.username=username;
		this.src=src;
		this.parameters=parameters;
		this.outputFiles=outputFiles;
		this.listener=listener;
		this.outputPath=outputPath;
		this.priority = 50;
		
		this.status=TaskStatusEnum.WAITING;
		this.renewalTime=defaultRenewalTime;
		this.expirationDate=Instant.now();
		this.errors=new HashMap<>();
		
		this.eTag = new Tag(getNewRandomString());
	}

	public Task(int priority, String id,String username, String src, Map<String,String> parameters, List<OutputFileSummary> outputFiles, String listener, String outputPath){
		this.id = id;
		this.username=username;
		this.src=src;
		this.parameters=parameters;
		this.outputFiles=outputFiles;
		this.listener=listener;
		this.outputPath=outputPath;
		this.priority = priority;
		
		this.status=TaskStatusEnum.WAITING;
		this.renewalTime=defaultRenewalTime;
		this.expirationDate=Instant.now();
		this.errors=new HashMap<>();
		
		this.eTag = new Tag(getNewRandomString());
	}
	
	public Task(String id, String username, String src, Map<String,String> parameters, List<OutputFileSummary> outputFiles, String listener,String outputPath, int renewalTime){
		this.id = id;
		this.username=username;
		this.src=src;
		this.parameters=parameters;
		this.outputFiles=outputFiles;
		this.listener=listener;
		this.outputPath=outputPath;
		this.priority = 50;
		
		this.status=TaskStatusEnum.WAITING;
		this.renewalTime=renewalTime;
		this.expirationDate=Instant.now();
		this.errors=new HashMap<>();
		
		this.eTag = new Tag(getNewRandomString());
	}

	public Task(int priority, String id, String username, String src, Map<String,String> parameters, List<OutputFileSummary> outputFiles, String listener,String outputPath, int renewalTime){
		this.id = id;
		this.username=username;
		this.src=src;
		this.parameters=parameters;
		this.outputFiles=outputFiles;
		this.listener=listener;
		this.outputPath=outputPath;
		this.priority = priority;
		
		this.status=TaskStatusEnum.WAITING;
		this.renewalTime=renewalTime;
		this.expirationDate=Instant.now();
		this.errors=new HashMap<>();
		
		this.eTag = new Tag(getNewRandomString());
	}
	
	public Task(String username, String src, Map<String,String> parameters, List<OutputFileSummary> outputFiles, String listener, String outputPath){
		this.username=username;
		this.src=src;
		this.parameters=parameters;
		this.outputFiles=outputFiles;
		this.listener=listener;
		this.outputPath=outputPath;
		this.priority = 50;
		
		this.status=TaskStatusEnum.WAITING;
		this.renewalTime=defaultRenewalTime;
		this.expirationDate=Instant.now();
		this.errors=new HashMap<>();
		
		this.eTag = new Tag(getNewRandomString());
	}
	
	public Task(String username, String src, Map<String,String> parameters, List<OutputFileSummary> outputFiles, String listener, String outputPath, int renewalTime){
		this.username=username;
		this.src=src;
		this.parameters=parameters;
		this.outputFiles=outputFiles;
		this.listener=listener;
		this.outputPath=outputPath;
		this.priority = 50;
		
		this.status=TaskStatusEnum.WAITING;
		this.renewalTime=renewalTime;
		this.expirationDate=Instant.now();
		this.errors=new HashMap<>();
		
		this.eTag = new Tag(getNewRandomString());
	}
	
	public Task(Task task){
		this.id= task.id;
		this.src = task.src;
		this.parameters = task.parameters;
		this.outputFiles = task.outputFiles;
		this.listener = task.listener;
		this.status = task.status;
		this.renewalTime = task.renewalTime;
		this.expirationDate = task.expirationDate;
		this.errors = task.errors;
		this.eTag = task.eTag;
		this.workerURI = task.workerURI;
		this.username = task.username;
		this.outputPath = task.outputPath;
		this.priority = task.priority;
	}
	
	public int getPriority(){
		return priority;
	}

	public void setPriority(int priority){
		this.priority = priority;
	}

	public String getId(){
		return id;
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

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public List<OutputFileSummary> getOutputFiles() {
		return outputFiles;
	}

	public void setOutputFiles(List<OutputFileSummary> outputFiles) {
		this.outputFiles = outputFiles;
	}

	public TaskStatusEnum getStatus() {
		return status;
	}

	public void setStatus(TaskStatusEnum status) {
		this.status = status;
	}

	public int getRenewalTime() {
		return renewalTime;
	}

	public void setRenewalTime(int renewalTime) {
		this.renewalTime = renewalTime;
	}

	public String getWorkerURI() {
		return workerURI;
	}

	public void setWorkerURI(String workerURI) {
		this.workerURI = workerURI;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getListener() {
		return listener;
	}

	public void setListener(String listener) {
		this.listener = listener;
	}

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public Instant getExpirationDate() {
		return expirationDate;
	}

	public Tag geteTag() {
		return eTag;
	}
	
	public boolean isExpired(){
		return this.expirationDate.isBefore(Instant.now());
	}

	public void renewTask() {
		this.expirationDate = Instant.now().plusSeconds(renewalTime*2);		
	}

	public void changeETag(){ 
		boolean changed=false;
		do{
			Tag newTag = new Tag(getNewRandomString()); //Based on random Strings
			if(!newTag.equals(eTag)){
				eTag = newTag;
				changed=true;
			}
		}while(!changed);
		
	}
	
	public void seteTag(Tag eTag){
		this.eTag=eTag;
	}
	
	public void setId(String id){
		this.id=id;
	}

	public void clean() {
		this.id="";
		
	}
	
	//TODO: debo incluir otras funciones para recuperar y establecer datos
}
