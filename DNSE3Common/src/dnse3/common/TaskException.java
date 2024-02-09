package dnse3.common;

public class TaskException extends Exception {

	private static final long serialVersionUID = 1L;
	private TaskExceptionEnum error;
	
	public TaskException(TaskExceptionEnum error){
		this.error=error;
	}
	
	public TaskExceptionEnum getError(){
		return error;
	}
	
	public String toString(){
		switch(error){
		case BAD_REQUEST:
			return "The request body was empty o malformed";
		case NOT_FOUND:
			return "The task requested was not found";
		default:
			return null;
		}
	}

}
