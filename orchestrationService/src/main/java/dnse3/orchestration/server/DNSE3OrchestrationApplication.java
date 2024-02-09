package dnse3.orchestration.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import dnse3.orchestration.controller.DataController;
import dnse3.orchestration.controller.ExecutionController;
import dnse3.orchestration.jpa.controller.JpaController;
import dnse3.orchestration.resource.OutputFileResource;
import dnse3.orchestration.resource.OutputFileStructureResource;
import dnse3.orchestration.resource.OutputFileStructuresResource;
import dnse3.orchestration.resource.OutputFilesResource;
import dnse3.orchestration.resource.ParameterDescriptionResource;
import dnse3.orchestration.resource.ParameterDescriptionsResource;
import dnse3.orchestration.resource.ParameterResource;
import dnse3.orchestration.resource.ParameterSweepSimulationResource;
import dnse3.orchestration.resource.ParameterSweepSimulationsResource;
import dnse3.orchestration.resource.ParametersResource;
import dnse3.orchestration.resource.ProjectModelResource;
import dnse3.orchestration.resource.ProjectResource;
import dnse3.orchestration.resource.ProjectsResource;
import dnse3.orchestration.resource.ResultsResource;
import dnse3.orchestration.resource.SingleSimulationResource;
import dnse3.orchestration.resource.SingleSimulationsResource;
import dnse3.orchestration.resource.UserResource;
import dnse3.orchestration.resource.UsersResource;

public class DNSE3OrchestrationApplication extends Application {
	
	//private String queueAddress = null;
	private JpaController jpa;
	private DataController dataController;
	private ExecutionController executionController;
	private static Logger logger;
	
	//TODO Queue Address
	public DNSE3OrchestrationApplication(String queueAddress){
		iniciaLogger();
		this.jpa = new JpaController();
		this.dataController =  new DataController(jpa);
		this.executionController = new ExecutionController(this.jpa, this.dataController, queueAddress);
		jpa.restartSystem();
	}
	
	private static void iniciaLogger() {
		logger = LogManager.getLogger("csvDNSE3");
		logger.atInfo();
	}
	
	public static Logger dameLogger() {
		synchronized (logger) {
			if(logger == null) 
				iniciaLogger();
			return logger;
		} 
	}
	
	public JpaController getJpaController(){
		return this.jpa;
	}
	
	public DataController getDataController(){
		return this.dataController;
	}
	
	public ExecutionController getExecutionController(){
		return this.executionController;
	}

	@Override
	public Restlet createInboundRoot(){
		Router router = new Router(getContext());
		
		//We use the version in the URIs for retro-compatibility
		router.attach("/v0.2/users/", UsersResource.class);
		router.attach("/v0.2/users/{username}/", UserResource.class);
		router.attach("/v0.2/users/{username}/projects/", ProjectsResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/", ProjectResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/model", ProjectModelResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/parameters/", ParameterDescriptionsResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/parameters/{parameterName}", ParameterDescriptionResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/outputfilestructures/", OutputFileStructuresResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/outputfilestructures/{outputFileStructureName}", OutputFileStructureResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/outputfiles/", OutputFilesResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/outputfiles/{outputFileName}", OutputFileResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/singlesimulations/", SingleSimulationsResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/singlesimulations/{simulationId}/", SingleSimulationResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/parametersweepsimulations/", ParameterSweepSimulationsResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/parametersweepsimulations/{simulationId}/", ParameterSweepSimulationResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/{typeSimulation}/{simulationId}/parameters/", ParametersResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/{typeSimulation}/{simulationId}/parameters/{parameterName}", ParameterResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/{typeSimulation}/{simulationId}/outputfiles/", OutputFilesResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/{typeSimulation}/{simulationId}/outputfiles/{outputFileName}", OutputFileResource.class);
		router.attach("/v0.2/users/{username}/projects/{projectId}/{typeSimulation}/{simulationId}/results", ResultsResource.class);
		
		return router;
	}
}
