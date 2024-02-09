package dnse3.orchestration.jpa.model.user;

import java.lang.reflect.Type;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dnse3.orchestration.controller.DataController;

public class UserSerializer implements JsonSerializer<User>{

	private boolean resume = false;
	private DataController dataController;
	
	public UserSerializer(DataController dataController) {
		// TODO Auto-generated constructor stub
		this.dataController=dataController;
	}
	
	public UserSerializer(DataController dataController, boolean resume) {
		// TODO Auto-generated constructor stub
		this.resume=resume;
		this.dataController=dataController;
	}
	
	@Override
	public JsonElement serialize(User user, final Type source, JsonSerializationContext context) {
		// TODO Auto-generated method stub
		final JsonObject userObj = new JsonObject();
		userObj.addProperty("username", user.getUsername());
		userObj.addProperty("currentSimulations", dataController.getUserCurrentSimulations(user.getUsername()));
		
		if(resume)
			userObj.addProperty("uri", user.getUsername()+"/");
		else{
			userObj.addProperty("maxSimulations", user.getSimulationLimit());
			userObj.addProperty("projectsUri", "projects/");
		}
		
		return userObj;
	}

}
