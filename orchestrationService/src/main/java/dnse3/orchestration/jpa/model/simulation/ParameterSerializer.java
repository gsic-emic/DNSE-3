package dnse3.orchestration.jpa.model.simulation;

import java.lang.reflect.Type;

import org.restlet.data.Reference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ParameterSerializer implements JsonSerializer<Parameter> {
	
	private boolean resume = false;
	private Reference locationRef;
	private SimulationTypeEnum simType;
	
	public ParameterSerializer(Reference locationRef, SimulationTypeEnum simType) {
		// TODO Auto-generated constructor stub
		this.locationRef=locationRef;
		this.simType=simType;
	}
	
	public ParameterSerializer(Reference locationRef, SimulationTypeEnum simType, boolean resume){
		this.locationRef=locationRef;
		this.simType=simType;
		this.resume=resume;
	}
	
	@Override
	public JsonElement serialize(Parameter parameter, Type type, JsonSerializationContext context) {
		// TODO Auto-generated method stub
		final JsonObject parameterObj = new JsonObject();
		parameterObj.addProperty("name", parameter.getParameterDescription().getName());
		
		if(parameter.getValues()!=null && !parameter.getValues().isEmpty()){
			switch(simType){
			case SINGLE_SIMULATION:
				parameterObj.addProperty("value", parameter.getSingleValue());
				break;
			case PARAMETER_SWEEP_SIMULATION:
				JsonArray values = new JsonArray();
				for(String v: parameter.getValues())
					values.add(v);
				parameterObj.add("values", values);
			}
		}
		
		parameterObj.addProperty("minValue", parameter.getMinValue());
		parameterObj.addProperty("maxValue", parameter.getMaxValue());
		parameterObj.addProperty("step", parameter.getStep());
		//parameterObj.addProperty("units", parameter.getUnits());
		
		if(parameter.isRandom())
			parameterObj.addProperty("random", parameter.isRandom());
		
		if(resume)
			parameterObj.addProperty("uri", locationRef.toString()+Reference.encode(parameter.getParameterDescription().getName()));
		else{
			parameterObj.addProperty("parameterDescriptionUri", parameter.getParameterDescription().getName());
			final JsonObject simulationObj = new JsonObject();
			simulationObj.addProperty("simulationId", parameter.getSimulation().getId());
			simulationObj.addProperty("uri", locationRef.getParentRef().getParentRef().toString());
			parameterObj.add("simulation", simulationObj);
		}	
		
		return parameterObj;
	}
}
