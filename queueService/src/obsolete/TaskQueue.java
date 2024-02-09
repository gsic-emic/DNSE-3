package obsolete;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Tag;

import dnse3.common.Task;
import dnse3.common.TaskException;

public interface TaskQueue {

	//Method for returning a Task
	public Task getTask(String idTask) throws TaskException;
	
	//Method for returning the next Task
	public Task getNextTask();
	
	//Method for iterating throw the queue
	public Collection<Task> getTasks();
	
	//Method for creating a new Task
	public String addNewTask(JSONObject document) throws TaskException;
	
	//Method for creating a new Task
	//public String addNewTask(Document document) throws TaskException;
	
	//Remove a task
	public void deleteTask(String idTask) throws TaskException;
	
	//Update a task
	public Tag udateTask(String idTask, JSONArray document, String address, String server) throws TaskException;
	
	//Get the ETag of a task
	public Tag getETag(String idTask) throws TaskException;

	public int getSize();
	
}
