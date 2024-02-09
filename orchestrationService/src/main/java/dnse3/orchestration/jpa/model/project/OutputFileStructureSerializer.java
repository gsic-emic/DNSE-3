package dnse3.orchestration.jpa.model.project;

import java.lang.reflect.Type;

import org.restlet.data.Reference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class OutputFileStructureSerializer implements JsonSerializer<OutputFileStructure> {
	
	//toca recuperar las uris
	
	private boolean resume = false;
	private Reference locationRef;
	
	public OutputFileStructureSerializer(Reference locationRef){
		// TODO Auto-generated constructor stub
		this.locationRef=locationRef;
	}
	
	public OutputFileStructureSerializer(Reference loactionRef,boolean resume){
		this.locationRef=loactionRef;
		this.resume=resume;
	}
	
	@Override
	public JsonElement serialize(OutputFileStructure outputFile, Type type, JsonSerializationContext context) {
		// TODO Auto-generated method stub
		final JsonObject outputObj = new JsonObject();
		outputObj.addProperty("name", outputFile.getName());
		outputObj.addProperty("multiLine", outputFile.isMultiLine());
		final JsonArray outputVarsArray = new JsonArray();
		for(String s: outputFile.getOutputVars())
			outputVarsArray.add(s);
		
		if(outputVarsArray.size()>0)
			outputObj.add("outputVariables", outputVarsArray);
		
		if(resume)
			outputObj.addProperty("uri", locationRef.toString()+Reference.encode(outputFile.getName()));
		else{
			final JsonObject projectObj = new JsonObject();
			projectObj.addProperty("projectId", outputFile.getProject().getId());
			projectObj.addProperty("uri", new Reference(locationRef).getParentRef().toString());
			outputObj.add("project", projectObj);
		}	
		
		return outputObj;
	}
}
