package dnse3.orchestration.jpa.model.project;

import java.lang.reflect.Type;

import org.restlet.data.Reference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ParameterDescriptionSerializer implements JsonSerializer<ParameterDescription> {
	
	private boolean resume = false;
	private Reference locationRef;
	
	public ParameterDescriptionSerializer(Reference locationRef) {
		this.locationRef=locationRef;
	}
	
	public ParameterDescriptionSerializer(Reference locationRef,boolean resume){
		this.locationRef=locationRef;
		this.resume=resume;
	}
	
	@Override
	public JsonElement serialize(ParameterDescription parameter, Type type, JsonSerializationContext context) {
		// TODO Auto-generated method stub
		final JsonObject parameterObj = new JsonObject();
		parameterObj.addProperty("name", parameter.getName());
		parameterObj.addProperty("type", parameter.getType().toString());
		
		switch(parameter.getType()){
		case STRING_VALUE:
			final JsonArray values= new JsonArray();
			for(String s: parameter.getValues())
				values.add(s);
			if(values.size()>0)
				parameterObj.add("possibleValues", values);
			parameterObj.addProperty("defaultValue",parameter.getDefaultValue());
			break;
		case INTEGER_VALUE:
			if(parameter.getGreater()!=null)
				parameterObj.addProperty("greaterThan", Integer.valueOf(parameter.getGreater()));
			if(parameter.getGreaterEqual()!=null)
				parameterObj.addProperty("greaterThanOrEqualTo", Integer.valueOf(parameter.getGreaterEqual()));
			if(parameter.getLess()!=null)
				parameterObj.addProperty("lessThan", Integer.valueOf(parameter.getLess()));
			if(parameter.getLessEqual()!=null)
				parameterObj.addProperty("lessThanOrEqualTo", Integer.valueOf(parameter.getLessEqual()));
			parameterObj.addProperty("defaultValue",parameter.getDefaultValue());
			break;
		case RATIONAL_VALUE:
			if(parameter.getGreater()!=null)
				parameterObj.addProperty("greaterThan", Double.valueOf(parameter.getGreater()));
			if(parameter.getGreaterEqual()!=null)
				parameterObj.addProperty("greaterThanOrEqualTo", Double.valueOf(parameter.getGreaterEqual()));
			if(parameter.getLess()!=null)
				parameterObj.addProperty("lessThan", Double.valueOf(parameter.getLess()));
			if(parameter.getLessEqual()!=null)
				parameterObj.addProperty("lessThanOrEqualTo", Double.valueOf(parameter.getLessEqual()));
			parameterObj.addProperty("defaultValue",parameter.getDefaultValue());
			break;
		default: break;
		}
		
		if(resume)
			parameterObj.addProperty("uri", locationRef.toString()+Reference.encode(parameter.getName()));
		else{
			final JsonObject projectObj = new JsonObject();
			projectObj.addProperty("projectId", parameter.getProject().getId());
			projectObj.addProperty("uri", locationRef.getParentRef().getParentRef().toString());
			parameterObj.add("project", projectObj);
		}	
		
		return parameterObj;
	}
}
