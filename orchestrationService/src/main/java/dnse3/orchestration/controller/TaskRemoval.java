package dnse3.orchestration.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class TaskRemoval implements Runnable {

    private ExecutionController executionController;
    private String taskUri;
    private long simulationId;
    
    public TaskRemoval(ExecutionController executionController) {
        this.executionController = executionController;
    }
    
    @Override
    public void run() {
        while(true){
            try{
                taskUri=null;
                simulationId=-1;
                while(true){
                    executionController.checkToRemove();
                    simulationId=executionController.getSimulationToRemove();
                    if(simulationId!=-1)
                        break;
                    taskUri=executionController.getTaskToRemove();
                    if(taskUri!=null)
                        break;
                }
                
                if(simulationId!=-1)
                    removeSimulation();
                else
                    removeTask();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }

    }

    /**
     * Método para eliminar una tarea de la simulación
     */
    private void removeTask(){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try{
            HttpDelete deleteRequest = new HttpDelete(taskUri);
            try{
                response = httpClient.execute(deleteRequest);
            }catch(IOException e){
                e.printStackTrace();
            }
            
            if(response.getStatusLine().getStatusCode()!=204){
                //ERROR no se pudo eliminar la simulación
            }
            EntityUtils.consume(response.getEntity());
        } catch(IOException e){
            e.printStackTrace();
        }
        
        try{
            if(response!=null)
                response.close();
            httpClient.close();
        } catch(IOException e){
            e.printStackTrace();
        }
        
        //Tengo que quitarlo del exec? La cola en principio si se hace un pop se coge y se borra
    }
    
    /**
     * Método para eliminar todas las taréas de la simulación
     */
    private void removeSimulation(){
        ArrayList<String> taskUris = executionController.getTasksUrisToRemove(simulationId);

        if(taskUris!=null){
            for(String uri : taskUris){
                CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = null;
                try{
                    HttpDelete deleteRequest = new HttpDelete(uri);
                    response = httpClient.execute(deleteRequest);

                    int statusCode = response.getStatusLine().getStatusCode();
                    if(statusCode != 204){
                        //¿? qué hago si no ha podido eliminarlo de la cola?
                    }

                    EntityUtils.consume(response.getEntity());
                } catch(Exception e){
                    System.err.println("Excepción lanzada al intentar borrar una simulación de la cola " + e.getMessage());
                }
                finally{
                    try{
                        if(response!=null){
                            response.close();
                        }
                        httpClient.close();
                    } catch(IOException e){
                        System.err.println(e.toString() +" - "+ e.getCause());
                    }
                }
            }
        }

        executionController.notifySimulationRemoved(simulationId); //Aquí siempre lo tengo que hacer
    }
}
