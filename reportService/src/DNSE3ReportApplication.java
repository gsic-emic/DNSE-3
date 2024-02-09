import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import dnse3.common.CloudManager;
import dnse3.queue.data.TaskQueue;

public class DNSE3ReportApplication extends Application {
	
	private TaskQueue taskQueue; //Puede ser directamente la implementación y no la clase abstracta
	private CloudManager cloudManager;
	
	public DNSE3ReportApplication(TaskQueue taskQueue, CloudManager cloudManager){
		this.taskQueue=taskQueue;
		this.cloudManager=cloudManager;
	}
	
	@Override
	public Restlet createInboundRoot(){
		Router router = new Router(getContext());
		
		//We use the version in the URIs for retro-compatibility
		router.attach("/v0.1/reports/",TaskQueueResource.class); //Podría usar los mismos que en Queue, y me evito tener que implementarlos
		router.attach("/v0.1/reports/{reportID}", TaskResource.class);
		
		return router;
	}

	public TaskQueue getTaskQueue(){
		return taskQueue;
	}
}
