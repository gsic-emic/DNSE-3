package dnse3.queue.data;

import java.io.IOException;
//import java.util.LinkedList;
//import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
import org.json.JSONObject;
//import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
//import org.restlet.resource.ClientResource;
//import org.restlet.resource.ResourceException;

//import dnse3.common.TaskStatusEnum;
import dnse3.common.tasks.Task;
import dnse3.queue.server.DNSE3QueueApplication;

public class OrchestrationNotifier{ //cambiar a modo executorservice
	
	//////////////////////////////////
	//NOT USED!!!!!!!!!!!!!!!!!!!!!!//
	//////////////////////////////////
	
//	private BlockingQueue<Task> tasks;
//	private ExecutorService pool;
	private ExecutorService notificationPool;
//	private LinkedList<Task> pendingTasks;
	private LinkedBlockingQueue<Task> pendingTasks;
	private ScheduledExecutorService periodicPool;
	private TaskQueue taskQueue;
	
	public OrchestrationNotifier(TaskQueue taskQueue){
//		this.tasks = new LinkedBlockingQueue<>();
//		this.pool = Executors.newFixedThreadPool(5);
		this.notificationPool = Executors.newCachedThreadPool();
		this.periodicPool=Executors.newScheduledThreadPool(1);
//		pendingTasks = new LinkedList<>();
		this.pendingTasks=new LinkedBlockingQueue<>();
		this.taskQueue=taskQueue;
		
		periodicPool.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				Task task = null;
				while((task = pendingTasks.poll()) != null){
					if(taskQueue.containsId(task.getId())){
						CloseableHttpClient httpClient = HttpClients.createDefault();
						CloseableHttpResponse response = null;
						try{
							for(int i=0; i<5; i++){
								HttpPatch patchRequest = new HttpPatch("http://"+DNSE3QueueApplication.getOrchestrationAddress()+"/"+task.getListener());
								
								JSONObject patch = new JSONObject();
								patch.put("op", "replace");
								patch.put("path", "/status");
								patch.put("value", task.getStatus().toString());
								
								StringEntity body = new StringEntity(patch.toString(), "UTF-8");
								body.setContentType("application/json");
								patchRequest.setEntity(body);
								
								response = httpClient.execute(patchRequest);
								int statusCode = response.getStatusLine().getStatusCode();
								if(statusCode == 204){//Success
									EntityUtils.consume(response.getEntity());
									break;
								}
								else{
									if(statusCode == 400){//Bad request
										System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 400 - Bad Request");
										EntityUtils.consume(response.getEntity());
										break;
									}
									else{
										if(statusCode == 500){//Error in queue
											System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 500 - Internal Server Error\n");
											System.err.println(response.getEntity().toString());
											EntityUtils.consume(response.getEntity());
											pendingTasks.offer(task);
											break;
										}
										else{ 
											if(statusCode == 404){//Not Found
												System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 404 - Not Found - run");
												EntityUtils.consume(response.getEntity());
												break;
											}
											else{//Unknown error
												System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to " + statusCode + " - " + response.getStatusLine().getReasonPhrase());
												EntityUtils.consume(response.getEntity());
												if(i==4)
													pendingTasks.offer(task);
											}
										}
									}
								}
							}
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}finally{
							try{
								if(response!=null)
									response.close();
								httpClient.close();
							}catch(IOException e){
								
							}
						}
					}
				}
				
				
			}
		}, 0, 60, TimeUnit.SECONDS);
		
//		for(int i=0; i<5; i++){
//			pool.execute(new Runnable() {
//				
//				@Override
//				public void run() {
//					ClientResource server = new ClientResource("http://"+DNSE3QueueApplication.getOrchestrationAddress());
//					
//					while(true){
//						try{
//						Task task=null;
//						while((task=tasks.take())==null || task.getListener()==null);
//						server.getRequest().setResourceRef("http://"+DNSE3QueueApplication.getOrchestrationAddress()+"/"+task.getListener()+"?method=patch");
//						try {
//							JSONObject patch = new JSONObject();
//							patch.put("op", "replace");
//							patch.put("path", "/status");
//							patch.put("value", task.getStatus().toString());
//							
//							JsonRepresentation request = new JsonRepresentation(patch);
//							server.post(request);
//						} catch (JSONException |ResourceException e) {
//							e.printStackTrace();
//						}
//						//
//						}
//						catch (Exception e) {
//						}
//						discardRepresentation(server.getResponseEntity());
//					}
//					
//					//server.release();
//					
//				}
//			});
//		}
//		pool.shutdown();
	}
	
//	public void addTask(Task task){ //puede haber error al saturarse mucho la cola
//		if(task!=null)
//			try {
//				tasks.put(task);
//				
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//	}
	
	public void notifyTask(Task task){
		if(task != null){
			notificationPool.execute(new Runnable() {
				
				@Override
				public void run() {
					CloseableHttpClient httpClient = HttpClients.createDefault();
					CloseableHttpResponse response = null;
					try{
						for(int i=0; i<5; i++){
							HttpPatch patchRequest = new HttpPatch("http://" + DNSE3QueueApplication.getOrchestrationAddress() + "/" + task.getListener());
							
							JSONObject patch = new JSONObject();
							patch.put("op", "replace");
							patch.put("path", "/status");
							patch.put("value", task.getStatus().toString());
							
							StringEntity body = new StringEntity(patch.toString(), "UTF-8");
							body.setContentType("application/json");
							patchRequest.setEntity(body);
							
							response = httpClient.execute(patchRequest);
							int statusCode = response.getStatusLine().getStatusCode();
							if(statusCode == 204){//Success
								EntityUtils.consume(response.getEntity());
								break;
							}
							else{
								if(statusCode == 400){//Bad request
									System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 400 - Bad Request");
									EntityUtils.consume(response.getEntity());
									break;
								}
								else{
									if(statusCode == 500){//Error in queue
										System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 500 - Internal Server Error\n");
										System.err.println(response.getEntity().toString());
										EntityUtils.consume(response.getEntity());
										pendingTasks.offer(task);
										break;
									}
									else{ 
										if(statusCode == 404){//Not Found
											System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 404 - Not Found - notifyTask");
											EntityUtils.consume(response.getEntity());
											break;
										}
										else{//Unknown error
											System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to " + statusCode + " - " + response.getStatusLine().getReasonPhrase());
											EntityUtils.consume(response.getEntity());
											if(i==4)
												pendingTasks.offer(task);
										}
									}
								}
							}
						}
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						try{
							if(response!=null)
								response.close();
							httpClient.close();
						}catch(IOException e){
							
						}
					}
				}
			});
		}
	}
	
	public void discardRepresentation(Representation rep){
		if(rep!=null){
			try{
				rep.exhaust();
			}catch (IOException e) {
				// notify...
			}
			rep.release();
		}
	}

}
