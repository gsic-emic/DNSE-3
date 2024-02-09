package dnse3.orchestration.jpa.model.project;

import java.lang.reflect.Type;

import org.restlet.data.Reference;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class OutputFileSerializer implements JsonSerializer<OutputFile> {
	
	private boolean resume = false;
	private Reference locationRef;
	private boolean simulation;
	
	public OutputFileSerializer(Reference locationRef) {
		// TODO Auto-generated constructor stub
		this.locationRef=locationRef;
		this.simulation=false;
	}
	
	public OutputFileSerializer(Reference locationRef, boolean resume){
		this.locationRef=locationRef;
		this.resume=resume;
		this.simulation=false;
	}
	
	public OutputFileSerializer(Reference locationRef, boolean resume, boolean simulation) {
		// TODO Auto-generated constructor stub
		this.locationRef=locationRef;
		this.resume=resume;
		this.simulation=simulation;
	}
	
	@Override
	public JsonElement serialize(OutputFile outputFile, Type type, JsonSerializationContext context) {
		final JsonObject outputObj = new JsonObject();
		outputObj.addProperty("outputFileName", outputFile.getName());
		Reference ofsReference = new Reference(locationRef,"/v0.2/users/"+outputFile.getProject().getUser().getUsername()+"/projects/"+outputFile.getProject().getId()+"/outputfilestructures/");
		switch(outputFile.getType()){
		case RESULT_FILE:
		case TABBED_FILE:
			Gson gson = new GsonBuilder().registerTypeAdapter(OutputFileStructure.class, new OutputFileStructureSerializer(ofsReference,true)).setPrettyPrinting().create();
			JsonParser parser = new JsonParser();
			outputObj.add("outputFileStructure", parser.parse(gson.toJson(outputFile.getOutputFileStructure())));
		case TRACE_FILE:
			outputObj.addProperty("type", outputFile.getType().toString());
		}
		
		if(resume)
			outputObj.addProperty("uri", locationRef.toString()+Reference.encode(outputFile.getName()));
		else{
			
			final JsonObject projectObj = new JsonObject();
			projectObj.addProperty("projectId", outputFile.getProject().getId());
			projectObj.addProperty("uri", (new Reference(locationRef,"/v0.2/users/"+outputFile.getProject().getUser().getUsername()+"/projects/"+outputFile.getProject().getId()+"/")).toString());
			outputObj.add("project", projectObj);
			
			if(simulation){
				Reference simulationRef = locationRef.getParentRef().getParentRef() ;
				final JsonObject simObj = new JsonObject();
				simObj.addProperty("simulationId", Long.valueOf(simulationRef.getLastSegment()));
				simObj.addProperty("uri", simulationRef.toString());
				outputObj.add("simulation", simObj);
				
			}
		}	
		
		return outputObj;
	}
}
