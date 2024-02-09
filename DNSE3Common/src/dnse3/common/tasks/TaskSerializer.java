package dnse3.common.tasks;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dnse3.common.TaskStatusEnum;

public class TaskSerializer implements JsonSerializer<Task>{
	
	@Override
	public JsonElement serialize(Task task, Type type, JsonSerializationContext context) {
		final JsonObject taskObj = new JsonObject();
		taskObj.addProperty("id", task.getId());
		taskObj.addProperty("src", task.getSrc());
		taskObj.addProperty("status", task.getStatus().toString());
		taskObj.addProperty("priority", task.getPriority());
		taskObj.addProperty("renewalTime", task.getRenewalTime());
		taskObj.addProperty("username", task.getUsername());
		taskObj.addProperty("listener", task.getListener());
		taskObj.addProperty("outputPath", task.getOutputPath());
		
		if(task.getStatus().equals(TaskStatusEnum.PROCESSING)){
			taskObj.addProperty("expirationDate", task.getExpirationDate().toString());
			taskObj.addProperty("workerURI", task.getWorkerURI());
		}
		
		final JsonArray parameterArray = new JsonArray();
		for(Entry<String,String> e: task.getParameters().entrySet()){
			JsonObject parameterObj = new JsonObject();
			parameterObj.addProperty("name", e.getKey());
			parameterObj.addProperty("value", e.getValue());
			parameterArray.add(parameterObj);
		}
		taskObj.add("parameters", parameterArray);
		
		final JsonArray outputFileArray = new JsonArray();
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		for(OutputFileSummary o: task.getOutputFiles()){
			outputFileArray.add(parser.parse(gson.toJson(o)));
		}
		taskObj.add("outputFiles", outputFileArray);
		
		return taskObj;
	}
	
	

}
