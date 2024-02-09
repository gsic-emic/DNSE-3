package dnse3.common;

public enum TaskStatusEnum {
	WAITING,PROCESSING,FINISHED,ERROR; //Añado MALFORMED para estados inconsistentes, que se borre el script, que no esté correctamente formado el mensaje...
}
