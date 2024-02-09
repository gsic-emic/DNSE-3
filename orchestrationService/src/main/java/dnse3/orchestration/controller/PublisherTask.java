package dnse3.orchestration.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.common.tasks.OutputFileSummary;
import dnse3.common.tasks.Task;
import dnse3.common.tasks.TaskSerializer;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;
import dnse3.orchestration.jpa.model.project.OutputFile;
import dnse3.orchestration.jpa.model.project.OutputFileStructure;
import dnse3.orchestration.jpa.model.simulation.Parameter;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;

public class PublisherTask implements Runnable{
    
    private String queueAddress;
    private String username;
    private SingleSimulation singleSimulation;
    private ParameterSweepSimulation parameterSweepSimulation;
    private ExecutionController executionController;
    private Gson gson;
    private CloseableHttpClient httpClient;
    
    /**
     * Constructor de la clase. Se utiliza para simulaciones de tipo individual.
     * @param username Identificador del usuario en el sistema
     * @param singleSimulation Simulaci�n a realizar
     * @param executionController 
     * @param queueAddress Direcci�n IP y puerto del Servicio de Colas
     */
    public PublisherTask(String username, SingleSimulation singleSimulation, ExecutionController executionController, String queueAddress){
        this.username = username;
        this.singleSimulation = singleSimulation;
        this.executionController = executionController;
        this.gson =  new GsonBuilder().registerTypeAdapter(Task.class, new TaskSerializer()).setPrettyPrinting().create();
        this.httpClient = HttpClients.createDefault();
        this.queueAddress = queueAddress;
    }
    
    /**
     * Constructro de la clase. Se utiliza para simulaciones de barrido de par�metro.
     * @param username Identificador del usuario en el sistema
     * @param parameterSweepSimulation Simulaci�n a realizar
     * @param executionController
     * @param queueAddress Direcci�n IP y puerto del Servicio de Colas
     */
    public PublisherTask(String username, ParameterSweepSimulation parameterSweepSimulation, ExecutionController executionController, String queueAddress){
        this.username = username;
        this.parameterSweepSimulation = parameterSweepSimulation;
        this.executionController = executionController;
        this.gson =  new GsonBuilder().registerTypeAdapter(Task.class, new TaskSerializer()).setPrettyPrinting().create();
        this.httpClient = HttpClients.createDefault();
        this.queueAddress = queueAddress;
    }

    @Override
    public void run() {
        if(singleSimulation!=null){ //Ejecuci�n de las simulaciones individuales
            int remainingSimulations = singleSimulation.getRemainingSimulations();
            List<Task> tasks = generateTasks(singleSimulation);
            String tid = "";
            try{
                long inicio = System.currentTimeMillis();
                System.out.println("Comienza la publicaci�n de " + singleSimulation.getId());
                for(Task t : tasks){
                    tid = t.getId();
                    String[] ids = t.getId().split("-");
                    String simulationAddress = publishTask(t);
                    remainingSimulations--;
                    //System.out.println(remainingSimulations);
                    executionController.addSimulationAddress(singleSimulation.getId(), Integer.parseInt(ids[1]), simulationAddress);
                }
                System.out.println("Finaliza la simulaci�n de " + singleSimulation.getId() + ". Tiempo en ms: " + (System.currentTimeMillis() - inicio));
                executionController.notifySimulationPublished(singleSimulation.getId(),SimulationTypeEnum.SINGLE_SIMULATION);
            }
            catch(Exception e){
                //Notificar del error en la simulaci�n con ID e indicar el n�mero de simulaciones pendientes
                String error = "No se ha podido publicar la simulaci�n "+tid+" de la simulaci�n "+singleSimulation.getId()+". Simulaciones pendientes: "+remainingSimulations;
                System.out.println(new Date().toString() + error + "\n" + e.toString());
                e.printStackTrace();
            }
            finally{
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{ //Ejecuci�n de las de barrido de par�metros
            int remainingSimulations = parameterSweepSimulation.getRemainingSimulations();
            String tid = "";
            try{
                long inicio = System.currentTimeMillis();
                System.out.println("Comienza la publicaci�n de " + parameterSweepSimulation.getId());
                for(SingleSimulation singleSimulation : parameterSweepSimulation.getSingleSimulations()){
                    List<Task> tasks = generateTasks(singleSimulation);
                    //System.out.println(tasks.size());
                    for(Task t : tasks){
                        tid = t.getId();
                        String[] ids = t.getId().split("-");
                        String simulationAddress = publishTask(t);
                        remainingSimulations--;
                        //System.out.println("Quedan por publicar: " + remainingSimulations);
                        executionController.addSimulationAddress(singleSimulation.getId(), Integer.parseInt(ids[1]), simulationAddress);
                    }
                }
                System.out.println("Finaliza la simulaci�n de " + parameterSweepSimulation.getId() + ". Tiempo en ms: " + (System.currentTimeMillis() - inicio));
                executionController.notifySimulationPublished(parameterSweepSimulation.getId(),SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
            }
            catch(Exception e){
                String error = "No se ha podido publicar la simulaci�n "+tid+" de la simulaci�n "+parameterSweepSimulation.getId()+". Simulaciones pendientes: "+remainingSimulations;
                System.out.println(new Date().toString() + error + "\n" + e.toString());
                e.printStackTrace();
            }
            finally{
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Genera la lista de las tareas a realizar para completar una simulaci�n.
     * @param simulation Simulaci�n a realizar
     * @return Lista de tareas que se tienen que llevar a cabo para completar una simulaci�n.
     */
    public List<Task> generateTasks(SingleSimulation simulation){
        List<Task> tasks = new ArrayList<>();
        
        List<OutputFileSummary> outputFiles = new ArrayList<>();
        
        OutputFileStructure ofs;
        for(OutputFile of : simulation.getOutputFiles()){
            ofs = of.getOutputFileStructure();
            if(ofs != null){
                outputFiles.add(new OutputFileSummary(of.getName(), ofs.isMultiLine()));
            }
            else{
                outputFiles.add(new OutputFileSummary(of.getName(), false));
            }
        }

        for(int i=0; i < simulation.getNumRepetitions(); i++){
            if(simulation.getCompletedRepetitions().contains(Integer.valueOf(i))){
                continue;
            }
            Map<String, String> parameters = new HashMap<>();
            for(Parameter p : simulation.getParameters()) {
                //System.err.println(p.getSingleValue());
                parameters.put(p.getParameterDescription().getName(), p.getSingleValue());
            }
            String path;
            if(simulation.getParameterSweepSimulation()!=null) {
                path="users/"+simulation.getProject().getUser().getUsername()+"/"+simulation.getProject().getId()+"/"+simulation.getParameterSweepSimulation().getId()+"/outputs/"+(Integer.valueOf(simulation.getName())+i)+"/";
            }else{
                path="users/"+simulation.getProject().getUser().getUsername()+"/"+simulation.getProject().getId()+"/"+simulation.getId()+"/outputs/"+(simulation.getNumRepetitions()!=1?i+"/":"");
            }
            tasks.add(new Task(simulation.getPriority(), simulation.getId()+"-"+i, username, simulation.getProject().getSourceURI(), parameters, outputFiles, "v0.2/simulations/"+simulation.getId()+"/"+i,path));
        }
        return tasks;
    }
    
    /**
     * Publicaci�n de una tarea en el Servicio de Colas
     * @param task Tarea a publicar
     * @return 
     * @throws Exception
     */
    public String publishTask(Task task) throws Exception{
        try{
            for(int i=0; i<5; i++){
                HttpPost postRequest = new HttpPost("http://"+queueAddress+"/v0.1/simulationqueue/");
                
                StringEntity body = new StringEntity(gson.toJson(task), "UTF-8");
                body.setContentType("application/json");
                postRequest.setEntity(body);
                
                CloseableHttpResponse response = httpClient.execute(postRequest);
                postRequest = null;

                int codigo = response.getStatusLine().getStatusCode();
                switch(codigo){
                case 201: //Creada
                    String location = response.getFirstHeader("Location").getValue();
                    EntityUtils.consume(response.getEntity());
                    response.close();
                    System.out.println("Devuelvo "+location);
                    return location;
                case 400: //BAD REQUEST
                    EntityUtils.consume(response.getEntity());
                    response.close();
                    throw new Exception("Cuando se est� publicando la tarea se recibe el c�digo 400");
                case 500: //INTERNAL SERVER ERROR
                    EntityUtils.consume(response.getEntity());
                    response.close();
                    throw new Exception("Cuando se est� intentando publicar una tarea se est� recogido el c�digo de estado 500");
                default:
                    EntityUtils.consume(response.getEntity());
                    if(i<4){
                        try{
                            Thread.sleep(5000);
                            continue;
                        }catch (InterruptedException e){
                            continue;
                        }
                    }
                    response.close();
                    throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED, "C�digo " + codigo);
                }
            }
        }
        catch(NumberFormatException | IOException e){
            throw new Exception(e.getMessage());
        }
        throw new Exception("No se ha podido publicar la tarea "+task.getSrc());
    }

}
