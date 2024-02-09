package dnse3.orchestration.server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import dnse3.orchestration.controller.ExecutionController;
import dnse3.orchestration.resource.NotificationSimulationResource;
import dnse3.orchestration.resource.NotificationReportResource;

public class DNSE3ListenerApplication extends Application {
	
	private ExecutionController executionController;
	
	public DNSE3ListenerApplication (ExecutionController executionController){
		this.executionController=executionController;
	}
	
	@Override
	public Restlet createInboundRoot(){
		Router router = new Router(getContext());
		
		//We use the version in the URIs for retro-compatibility
		router.attach("/v0.2/simulations/{simulationId}/report", NotificationReportResource.class);
		router.attach("/v0.2/simulations/{simulationId}/{repetitionId}", NotificationSimulationResource.class);
		
		return router;
	}
	
	public ExecutionController getExecutionController() {
		return this.executionController;
	}
}
