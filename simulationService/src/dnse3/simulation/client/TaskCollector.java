package dnse3.simulation.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Tag;
import org.restlet.representation.Representation;

import dnse3.common.TaskStatusEnum;
import dnse3.common.tasks.OutputFileSummary;
import dnse3.common.tasks.Task;

public class TaskCollector extends Thread {

    private String queueAddress;
    private TaskPool taskPool;
    private boolean quit;

    /**
     * Constructor de la clase TaskCollector.
     * @param queueAddress dirección del servicio de colas
     * @param taskPool lugar donde están las tareas
     */
    public TaskCollector(String queueAddress, TaskPool taskPool) {
        this.queueAddress = queueAddress;
        this.taskPool = taskPool;
        this.quit = false;
    }

    /**
     * Método que busca obtener nuevas tareas para el servicio de simulación.
     */
    public void run() {
        try{
            while (!quit) { //Al usar while en vez de recursión, no deberá de dar el StackOverflow
                System.out.println("Ciclo de ejecución disponible, solicitando tarea");
                boolean available = false;
                Task task = null;
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = null;
                try{
                    do{
                        HttpGet getRequest = new HttpGet("http://" + queueAddress + "/v0.1/simulationqueue/nextTask");
                        getRequest.addHeader("Accept", "application/json");
                        response = httpClient.execute(getRequest);
                        
                        if(response.getStatusLine().getStatusCode()==200){
                            HttpEntity entity=response.getEntity();
                            JSONObject taskRep = new JSONObject(EntityUtils.toString(entity));
                            
                            HashMap<String, String> parameters = new HashMap<>();
                            List<OutputFileSummary> outputFiles = new ArrayList<>();

                            if (taskRep.has("parameters")) {
                                JSONArray parameterArray = taskRep.getJSONArray("parameters");
                                for (int i = 0; i < parameterArray.length(); i++) {
                                    JSONObject param = parameterArray.getJSONObject(i);
                                    parameters.put(param.getString("name"), param.getString("value"));
                                }
                            }

                            JSONArray outputFilesArray = taskRep.getJSONArray("outputFiles");
                            for (int i = 0; i < outputFilesArray.length(); i++) {
                                JSONObject outputFile = outputFilesArray.getJSONObject(i);
                                ArrayList<String> variables = new ArrayList<>();
                                if (outputFile.has("outputVariables")) {
                                    JSONArray outputVariables = outputFile.getJSONArray("outputVariables");
                                    for (int j = 0; j < outputVariables.length(); j++)
                                        variables.add(outputVariables.getString(j));
                                }
                                outputFiles.add(new OutputFileSummary(outputFile.getString("name"),
                                        outputFile.getBoolean("multiLine"), variables));
                            }

                            task = new Task(taskRep.getString("id"), taskRep.getString("username"),
                                    taskRep.getString("src"), parameters, outputFiles, taskRep.getString("listener"),
                                    taskRep.getString("outputPath"), taskRep.getInt("renewalTime"));
                            task.setStatus(TaskStatusEnum.valueOf(taskRep.getString("status")));
                            System.out.println("ETag: "+response.getFirstHeader("Etag").getValue());
                            task.seteTag(new Tag(response.getFirstHeader("Etag").getValue()));

                            System.out.println("Tarea verificada");
                            available = true;
                            
                            EntityUtils.consume(entity);
                        }
                        else 
                            if (response.getStatusLine().getStatusCode() == 204) { // If no task is given
                                System.out.println("No simulation available, wait for a new request");
                                Thread.sleep(5000); // For the moment, wait a periodic time
                                EntityUtils.consume(response.getEntity());
                            } 
                            else { // If there's an error
                                System.err.println("Server error, please notify to the administrator");
                                Thread.sleep(5000);
                                EntityUtils.consume(response.getEntity());
                            }
                    }while (!available);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }finally{
                    try{
                        if(response!=null)
                            response.close();
                        httpClient.close();
                    }catch(IOException e){
                        
                    }
                }
                
                // ClientResource service = new ClientResource("http://" + queueAddress + "/v0.1/simulationqueue/nextTask"); // URI of the next task available
                
                // do {
                // 	Representation rep;
                // 	try {
                // 		service.accept(MediaType.APPLICATION_JSON); // Set
                // 													// Accept
                // 													// header
                // 													// to JSON
                // 		rep = service.get(); // Get the next task
                // 		if (service.getStatus().getCode() == 200) { // If
                // 													// there's a
                // 													// task
                // 													// available
                // 			JSONObject taskRep = new JsonRepresentation(rep).getJsonObject();
                // 			HashMap<String, String> parameters = new HashMap<>();
                // 			List<OutputFileSummary> outputFiles = new ArrayList<>(); // Cambiar
                // 																		// a
                // 																		// hashMap,
                // 																		// que
                // 																		// tenga
                // 																		// como
                // 																		// valor
                // 																		// is
                // 																		// es
                // 																		// multilinea
                // 																		// o
                // 																		// no.

                // 			if (taskRep.has("parameters")) {
                // 				JSONArray parameterArray = taskRep.getJSONArray("parameters");
                // 				for (int i = 0; i < parameterArray.length(); i++) {
                // 					JSONObject param = parameterArray.getJSONObject(i);
                // 					parameters.put(param.getString("name"), param.getString("value"));
                // 				}
                // 			}

                // 			JSONArray outputFilesArray = taskRep.getJSONArray("outputFiles");
                // 			for (int i = 0; i < outputFilesArray.length(); i++) {
                // 				JSONObject outputFile = outputFilesArray.getJSONObject(i);
                // 				ArrayList<String> variables = new ArrayList<>();
                // 				if (outputFile.has("outputVariables")) {
                // 					JSONArray outputVariables = outputFile.getJSONArray("outputVariables");
                // 					for (int j = 0; j < outputVariables.length(); j++)
                // 						variables.add(outputVariables.getString(j));
                // 				}
                // 				outputFiles.add(new OutputFileSummary(outputFile.getString("name"),
                // 						outputFile.getBoolean("multiLine"), variables));
                // 			}

                // 			task = new Task(taskRep.getString("id"), taskRep.getString("username"),
                // 					taskRep.getString("src"), parameters, outputFiles, taskRep.getString("listener"),
                // 					taskRep.getString("outputPath"), taskRep.getInt("renewalTime"));
                // 			task.setStatus(TaskStatusEnum.valueOf(taskRep.getString("status")));
                // 			task.seteTag(rep.getTag());

                // 			System.out.println("Tarea verificada");
                // 			available = true; // Now we have an available task
                // 								// to work
                // 								// with
                // 		} else if (service.getStatus().getCode() == 204) { // If
                // 															// no
                // 															// task
                // 															// is
                // 															// given
                // 			System.out.println("No simulation available, wait for a new request");
                // 			Thread.sleep(5000); // For the moment, wait a
                // 								// periodic time
                // 		} else { // If there's an error
                // 			System.err.println("Server error, please notify to the administrator");
                // 			Thread.sleep(5000);
                // 		}
                // 	} catch (NullPointerException e) {
                // 		System.err.println("Revisa los parÃ¡metros utilizados");
                // 	} catch (JSONException e) {
                // 		System.out.println("No se recogen datos en formato JSON");
                // 	} catch (ClassCastException e) {
                // 		System.err.println("Comprueba que estÃ¡s cogiendo el tipo adecuado");
                // 	} catch (InterruptedException e) {
                // 		System.err.println("Problema al esperar para realizar la nueva peticiÃ³n");
                // 	} catch (ResourceException | IOException e) {
                // 		System.err.println("Problema al realizar la peticiÃ³n get");
                // 	}
                // } while (!available);

                // discardRepresentation(service.getResponseEntity());
                // service.release();
                // Client c = (Client) service.getNext();
                // try{
                // 	c.stop();
                // }catch(Exception e){
                    
                // }
                if(task==null){
                    try{
                        Thread.sleep(5000);
                    }catch(InterruptedException e){
                        
                    }
                }
                else{
                    taskPool.putTask(task);
                }
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Método para que hace que el método run() salga del bucle.
     */
    public void doQuit() {
        this.quit = true;
    }
    
    /**
     * 
     */
    public static void discardRepresentation(Representation rep){
        if(rep!=null){
            try{
                rep.exhaust();
            }
            catch (IOException e){
                //Notificació 2º error producido de forma conjunta
            }
            rep.release();
        }
    }
}
