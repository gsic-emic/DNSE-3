package dnse3.simulation.server;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

//import dnse3.common.Simulation;
import dnse3.common.tasks.Task;
import dnse3.simulation.client.TaskCollector;
//import dnse3.simulation.client.Worker;
import dnse3.simulation.client.WorkerException;
import dnse3.simulation.client.WorkerGenerator;
import dnse3.simulation.resources.SimulationResource;

public class DNSE3SimulationApplication extends Application{
	
	private TaskCollector tc;
	private WorkerGenerator wg;
	
	public DNSE3SimulationApplication(TaskCollector tc, WorkerGenerator wg){
		this.tc=tc;
		this.wg=wg;		
	}
	
	@Override
	public Restlet createInboundRoot(){
		Router router = new Router(getContext());
		
		//We use the version in the URIs for retro-compatibility
		router.attach("/v0.1/simulationClient/{simulationID}",SimulationResource.class);
		
		return router;
	}
	
	public Task getSimulation(String simulationId){
		return this.wg.getSimulation(simulationId);
	}
	

	public void notifyWorker(String simulationID) throws WorkerException{
		this.wg.stopSimulation(simulationID);
	}
}
