package dnse3.orchestration.jpa.model.project;

import java.lang.reflect.Type;

import org.restlet.data.Reference;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dnse3.orchestration.controller.DataController;

public class ProjectSerializer implements JsonSerializer<Project> {

	private boolean resume = false;
	private Reference locationRef;
	private DataController dataController;
	
	public ProjectSerializer(Reference locationRef, DataController dataController) {
		// TODO Auto-generated constructor stub
		this.locationRef=locationRef;
		this.dataController = dataController;
	}
	public ProjectSerializer(Reference locationRef, DataController dataController, boolean resume){
		this.locationRef=locationRef;
		this.resume=resume;
		this.dataController = dataController;
	}
	
	@Override
	public JsonElement serialize(Project project, Type type, JsonSerializationContext context) {
		final JsonObject projectObj = new JsonObject();
		projectObj.addProperty("projectId", project.getId());
		projectObj.addProperty("name", project.getName());
		projectObj.addProperty("numSingleSimulations", dataController.getNumSingleSimulations(project.getId()));
		projectObj.addProperty("numParameterSweepSimulations", project.getNumParameterSweepSimulations());
		projectObj.addProperty("creationDate",project.getCreationDate().toInstant().toString());
		projectObj.addProperty("updateDate", project.getUpdateDate().toInstant().toString());
		projectObj.addProperty("removing", project.isRemove());
		
		if(resume)
			projectObj.addProperty("uri", locationRef.toString()+project.getId()+"/");
		else{
			projectObj.addProperty("description", project.getDescription());
			projectObj.addProperty("outputFileStructuresUri", locationRef.toString()+"outputfilestructures/");
			projectObj.addProperty("outputFilesUri", locationRef.toString()+"outputfiles/");
			projectObj.addProperty("parameterDescriptionsUri", locationRef.toString()+"parameters/");
			projectObj.addProperty("singleSimulationsUri", locationRef.toString()+"singlesimulations/");
			projectObj.addProperty("parameterSweepSimulationsUri", locationRef.toString()+"parametersweepsimulations/");
		}
		
		return projectObj;
	}

}
