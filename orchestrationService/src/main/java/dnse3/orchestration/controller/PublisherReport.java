package dnse3.orchestration.controller;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.common.tasks.Task;
import dnse3.common.tasks.TaskSerializer;
import dnse3.orchestration.jpa.controller.JpaController;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.SimulationStatus;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;

public class PublisherReport /*implements Runnable*/ {
	String username;
	ExecutionController executionController;
	Task task;
	String queueAddress;
	Gson gson;
	long simulationId;
	SimulationTypeEnum type;
	
	public PublisherReport(String username, ExecutionController executionController, Task task, String queueAddress, long simulationId, SimulationTypeEnum type){
	   this.username = username;
	   this.simulationId = simulationId;
	   this.type = type;
       this.executionController = executionController;
       this.task = task;
       this.queueAddress = queueAddress;
       this.gson =  new GsonBuilder().registerTypeAdapter(Task.class, new TaskSerializer()).setPrettyPrinting().create();
       start();
    }

	/*@Override
	public void run() {*/
	public void start() {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		
		try {
			HttpPost httpPost = new HttpPost("http://" + queueAddress + "/v0.1/reportqueue/");
			//System.out.println(gson.toJson(task));

			StringEntity stringEntity = new StringEntity(gson.toJson(task));
			stringEntity.setContentType("application/json");
			httpPost.setEntity(stringEntity);
			response = httpClient.execute(httpPost);
			
			int statusCode = response.getStatusLine().getStatusCode();
			switch (statusCode) {
			case 200:
			case 201:
				EntityUtils.consume(response.getEntity());
				JpaController jpaController = executionController.getJpa();
				switch(type){
					case SINGLE_SIMULATION:{
						SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
						simulation.setStatus(SimulationStatus.REPORTING);
						jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
					}
					break;
					case PARAMETER_SWEEP_SIMULATION:{
						ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
						simulation.setStatus(SimulationStatus.REPORTING);
						jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
					}
					break;
				}
				jpaController = null;
				break;
			default:
				EntityUtils.consume(response.getEntity());
				executionController.notifyReport(Long.parseLong(task.getId()), SimulationStatus.ERROR);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(response != null)
					response.close();
				httpClient.close();
				System.out.println("Salgo del finally");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	

}
