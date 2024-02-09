package dnse3.queue.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
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
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Tag;

import dnse3.common.TaskException;
import dnse3.common.TaskExceptionEnum;
import dnse3.common.TaskStatusEnum;
import dnse3.common.tasks.Task;
import dnse3.queue.server.DNSE3QueueApplication;

/** Clase crada para gestinar las tareas pendientes de realizar
 * 
 * @author GSIC gsic.uva.es
 * @version 20191215
 */
public class TaskQueue {

    /** Mapa con la relación tarea y la instancia que la representa */
    private HashMap<String, Task> queue;
    /** Mapa con la realización usuario y la instancia que representa su cola */
    private HashMap<String,UserQueue> users;
    /** Lista con las colas de los distintos usuario que tienen tareas pendientes 
     * de completarse */
    private LinkedList<String> usersQueue;
    /** Objeto utilizado para notificar a Orquestación el estado de la tarea */
    private ExecutorService notificationPool;
    /** Objeto utilizado para notificar a Orquestación*/
    private ScheduledExecutorService lostPool;
    /** Objeto para bloquar las tareas que no se han completado */
    private BlockingQueue<Task> lostTasks;
    /** Log donde se anotarán los sucesos que gestione esta clase */
    private static Logger inputLogger = Logger.getLogger("DNSE3InputLogger");
    /**  */
    private ScheduledExecutorService inputPool = Executors.newScheduledThreadPool(1);
    /** */
    private Integer income;

    /**
     * Constructro de la clase. Crea instancias de los objetos que se tengan que iniciar.
     */
    public TaskQueue(){
        this.queue = new HashMap<>();
        this.users = new HashMap<>();
        this.usersQueue = new LinkedList<>();
        this.notificationPool= Executors.newCachedThreadPool();
        this.lostPool=Executors.newScheduledThreadPool(1);
        this.lostTasks=new LinkedBlockingQueue<>();
        
        this.income=0;
        
        this.inputPool.scheduleAtFixedRate(new Runnable() {
            
            @Override
            public void run() {
                int lastIncome;
                synchronized(income){
                    lastIncome = income;
                    income=0;
                }
                inputLogger.info(String.valueOf(lastIncome));
            }
        },0, 10, TimeUnit.SECONDS);
        
        this.lostPool.scheduleAtFixedRate(new Runnable() {
            
            @Override
            public void run() {
                Task task=null;
                while((task=lostTasks.poll())!=null){
                    if(queue.containsValue(task)){
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        CloseableHttpResponse response = null;
                        try{
                            //for(int i=0; i<5; i++){
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
                                switch (statusCode) {
                                case 204: //Success
                                    EntityUtils.consume(response.getEntity());
                                    break;
                                case 400: //Bad request
                                    System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 400 - Bad Request");
                                    EntityUtils.consume(response.getEntity());
                                    break;
                                case 404:
                                    System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 404 - Not Found (run)");
                                    EntityUtils.consume(response.getEntity());
                                    break;
                                case 500: //Error in queue
                                    System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 500 - Internal Server Error\n");
                                    System.err.println(response.getEntity().toString());
                                    EntityUtils.consume(response.getEntity());
                                    lostTasks.offer(task);
                                    break;
                                default: //Unknown error
                                    System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to "+statusCode+" - "+response.getStatusLine().getReasonPhrase());
                                    EntityUtils.consume(response.getEntity());
                                    //if(i==4)
                                    lostTasks.offer(task);
                                    break;
                                }
                            //}
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
    }

    /**
     * Método creado para agregar una tarea a la cola.
     * @param task Instancia de la tarea.
     * @throws TaskException Lanzará una excpeción cuando se intente agregar una 
     * tarea que ya exista en el sistema.
     */
    public void addNewTask(Task task) throws TaskException{
        String taskId = task.getId();
        synchronized(queue){
            if(queue.containsKey(taskId))
                throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
            queue.put(taskId, task);
        }
        String username = task.getUsername();
        UserQueue userQueue = users.get(username);
        if(userQueue == null){//Si la cola del usuario no existe se crea
            userQueue = new UserQueue(this, username);
            synchronized(users){
                users.put(username, userQueue);
            }
            synchronized(usersQueue){
                usersQueue.add(username);
            }
        }
        userQueue.addNewTask(idJob(task.getOutputPath()), taskId, task.getPriority());
        synchronized(income){
            income++;
        }
    }

    /**
     * Recupera la lista de tareas de la cola
     * @return Lista de instancias de tareas que forman la cola
     */
    public List<Task> getTasks(){
        return new ArrayList<>(queue.values());
    }

    /**
     * Método con el que se puede obtener una tarea a partir de su
     * identificador.
     * @param id Identificador de la tarea.
     * @return Tarea asociada al identificador
     */
    public Task getTask(String id){
        return queue.get(id);
    }

    /**
     * Método utilizado para preguntar a Colas cuál es la próxima tarea
     * que se podría realizar
     * @return Instancia de la tarea que se podría realizar
     */
    public Task getNextTask(){
    	String u = null;
        boolean queueWithUsers = false;
        queueWithUsers = (usersQueue.size()>0)?true:false;
        if(queueWithUsers) {
            synchronized(usersQueue){
                u = usersQueue.poll();
                usersQueue.add(u);
            }
        }
        //System.out.println(u);
        String id = null;
        if(queueWithUsers){
            //synchronized(users){
                id = users.get(u).getNextTask();
            //}
        }
        if(id!=null){
            //System.out.println("Valid task: "+id);
            //synchronized(queue){
            return queue.get(id);   
            //}
        }
        return null;
    }
    
    /**
     * Método con el que se puede eliminar una tarea a través de su 
     * identificador
     * @param id Identificador de la tarea
     * @throws TaskException Lanza una excepción cuando la tarea no 
     * exista en el sistema
     */
    public void removeTask(String id) throws TaskException{
        Task task = null;
        
        if(!queue.containsKey(id))
            throw new TaskException(TaskExceptionEnum.NOT_FOUND);
        task = queue.get(id);
        synchronized(queue){
            queue.remove(id);
        }
        //synchronized (users) {
        String username = task.getUsername();
        UserQueue u = users.get(username);
        if(u!=null){
            String idjob = idJob(task.getOutputPath());
            u.removeTask(idjob, id);
            if(u.isEmpty()){
                synchronized (usersQueue) {
                    usersQueue.remove(username);
                }
                synchronized(users){
                    users.remove(username);
                }
            }
        }
        //}
        //Según está ahora el servicio de Simulación (contenedores) no puedo eliminar la tarea del worker
        /*if(task.getWorkerURI()!=null && !task.getWorkerURI().isEmpty()){
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try{
                for(int i=0; i<5; i++){
                    System.out.println("Intento eliminar la tarea del worker");
                    HttpDelete deleteRequest = new HttpDelete(task.getWorkerURI());
                    response = httpClient.execute(deleteRequest);
                    EntityUtils.consume(response.getEntity());
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
            }*/
            
//			ClientResource server = new ClientResource(task.getWorkerURI());
//			server.setRetryAttempts(0);
//			try{
//				server.delete();
//			}catch(ResourceException e){
//				System.err.println("Worker not found");
//			}
//			discardRepresentation(server.getResponseEntity());
//			server.release();
//			Client c = (Client) server.getNext();
//			try{
//				c.stop();
//			}catch(Exception e){
//				
//			}
//      }
    }
    
    /**
     * Método con el que se puede cambiar el estado de una tarea
     * @param id Identificador de la tarea
     * @param status Estado que se pretende establecer a la tarea
     * @throws TaskException Se puede lanzar una excepción cuando la tarea no exista 
     * en el sistema o el estado no sea válido
     */
    public void updateTaskStatus(String id, TaskStatusEnum status) throws TaskException{
    	Task task;
    	//synchronized(queue) {
        if(!queue.containsKey(id))
            throw new TaskException(TaskExceptionEnum.NOT_FOUND);
        task = queue.get(id);
    	//}
    	if(task!=null) {
	        if(task.getStatus().equals(TaskStatusEnum.WAITING) && status!=null && status.equals(TaskStatusEnum.PROCESSING)){
	            users.get(task.getUsername()).setProcessing(idJob(task.getOutputPath()), id);
	        }
	        else {
	        	if(task.getStatus().equals(TaskStatusEnum.PROCESSING) && status!=null && !status.equals(TaskStatusEnum.WAITING)){
	        		if(!status.equals(TaskStatusEnum.PROCESSING))
	        			users.get(task.getUsername()).setFinished(idJob(task.getOutputPath()), id);
	        	}
	        	else {
                    if(!(status.equals(TaskStatusEnum.FINISHED))){
                        System.out.println("Se lanza la excepción de mal formada");
                        System.out.println("La tarea "+id+" está en el estado "+status);
                        throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
                    }
	        	}
	        }
        }
    	else{
            throw new TaskException(TaskExceptionEnum.NOT_FOUND);
        }
    }

    /**
     * Método para recuperar el etag de una tarea
     * @param id Identificador de la tarea
     * @return Tag de la tarea
     * @throws TaskException Lanza una excepción cuando la tarea no exista en el 
     * sistema
     */
    public Tag getEtag(String id) throws TaskException{
    	Tag tag = null;
    	//synchronized(queue) {
        if(!queue.containsKey(id))
            throw new TaskException(TaskExceptionEnum.NOT_FOUND);
        Task task = queue.get(id);
        tag = task.geteTag();
    	//}
        return tag;
    }

    /**
     * Método utilizado para recupara el número de tareas totales pendientes de 
     * completarse
     * @return Número de tareas pendientes de completarse
     */
    public int getSize(){
        int sum=0;
        //synchronized (users) {
        for(UserQueue u: users.values())
            sum += u.getSize();
		//}
        return sum;
    }

    /**
     * Método diseñado para conocer si un identificador existe en la cola
     * @param id Identificador a comprobar
     * @return True si el identificador existe, falso si no existe.
     */
    public boolean containsId(String id){
        return queue.containsKey(id);
    }

    /**
     * Método utilizado para actualizar una tarea que exista en el sistema
     * @param id Identificador de la tarea
     * @param array Nuevos valores a tomar
     * @param address Dirección donde se está procesando la tarea
     * @return etag de la tarea
     * @throws TaskException La tarea no existe en el sistema.
     */
    public Tag updateTask(String id, JSONArray array, String address) throws TaskException{
    	Task task;
    	//synchronized (queue) {
        if (!queue.containsKey(id))
            throw new TaskException(TaskExceptionEnum.NOT_FOUND); //No task with that ID
        task = new Task(queue.get(id)); //Temp copy of the simulation, to avoid overwriting if the request is not valid
		//}
        boolean statusChange = false;
        TaskStatusEnum status = null;
        try{
            for (int i = 0, length = array.length(); i < length; i++) { //Iterate through the JSON Array from the request
                JSONObject obj = array.getJSONObject(i);
                
                if(obj.getString("op").equals("replace")){ //we just allow the replace operation
                    switch (obj.getString("path")) {
                    case "/status":
                        if((task.getStatus().equals(TaskStatusEnum.WAITING) && obj.getString("value")!=null )
                                ||(task.getStatus().equals(TaskStatusEnum.PROCESSING) && obj.getString("value")!=null && !TaskStatusEnum.valueOf(obj.getString("value")).equals(TaskStatusEnum.WAITING))){
                            task.setStatus(TaskStatusEnum.valueOf(obj.getString("value")));
                            statusChange=true;
                            status = TaskStatusEnum.valueOf(obj.getString("value"));
                        }
                        else
                            throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
                        break;
                    case "/expirationDate":
                        task.renewTask(); // As its change automatically, the value field is not needed
                        break;
                    case "/uriWorker":
                        new URL("http://"+address+obj.getString("value")); //First, check the URI is wellformed
                        task.setWorkerURI("http://"+address+obj.getString("value"));
                    }
                }
                else{
                    throw new TaskException(TaskExceptionEnum.BAD_REQUEST); //Field not valid for the request
                }
            }
            task.changeETag(); //set a new ETag
            if(statusChange)
                updateTaskStatus(id, status);
            synchronized (queue) {
            	queue.replace(id, task); // The new simulation, completely updated, replaces the old value
            }
            if(task.getStatus().equals(TaskStatusEnum.FINISHED)||task.getStatus().equals(TaskStatusEnum.ERROR)){
                task.setWorkerURI("");
                
                notificationPool.execute(new Runnable() {
                    
                    @Override
                    public void run() {
                        CloseableHttpClient httpClient = HttpClients.createDefault();
                        CloseableHttpResponse response = null;
                        try{
                            //for(int i=0; i<5; i++){
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
                            switch (statusCode) {
                            case 204://Success
                                EntityUtils.consume(response.getEntity());
                                break;
                            case 400:
                                System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 400 - Bad Request");
                                EntityUtils.consume(response.getEntity());
                                break;
                            case 404:
                                System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 404 - Not Found (updateTask)");
                                EntityUtils.consume(response.getEntity());
                                break;
                            case 500:
                                System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to 500 - Internal Server Error\n");
                                System.err.println(response.getEntity().toString());
                                EntityUtils.consume(response.getEntity());
                                lostTasks.offer(task);
                                break;
                            default:
                                System.err.println("OrchestrationNotfier: Failed to notify Orchestration due to "+statusCode+" - "+response.getStatusLine().getReasonPhrase());
                                EntityUtils.consume(response.getEntity());
                                lostTasks.offer(task);
                                break;
                            }
                            //}
                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                            lostTasks.offer(task);
                        } catch (IOException e) {
                            e.printStackTrace();
                            lostTasks.offer(task);
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
            
                
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						ClientResource server = new ClientResource("http://"+DNSE3QueueApplication.getOrchestrationAddress()+"/"+task.getListener()+"?method=patch");
//						
//						for(int i=0; i<2; i++){
//							try {
//								JSONObject patch = new JSONObject();
//								patch.put("op", "replace");
//								patch.put("path", "/status");
//								patch.put("value", task.getStatus().toString());
//								
//								JsonRepresentation request = new JsonRepresentation(patch);
//								server.post(request);
//							} catch (JSONException |ResourceException e) {
//								e.printStackTrace();
//							}
//						}
//						discardRepresentation(server.getResponseEntity());
//						server.release();
//						Client c = (Client) server.getNext();
//						try{
//							c.stop();
//						}catch(Exception e){
//							
//						}
//					}
//				}).start();
            }
            return task.geteTag();
        } catch (IllegalArgumentException | NullPointerException | MalformedURLException | JSONException e){
            e.printStackTrace();
            throw new TaskException(TaskExceptionEnum.BAD_REQUEST); //Malformed request
        }
    }
    
    /**
     * Método para comprobar si el tiempo de una tarea está agotado
     * @param id Identificador de la tarea
     * @return Verdadero si ha finalizado, falso si no lo ha hecho
     * @throws TaskException
     */
    public boolean isExpired(String id) throws TaskException{
    	boolean expired;
    	//synchronized(queue) {
        if(!queue.containsKey(id))
            throw new TaskException(TaskExceptionEnum.NOT_FOUND);
        expired = queue.get(id).isExpired();
    	//}
    	return expired;
    }
    
    /**
     * Método para descartar una representación que llega de fuera del 
     * Servicio de Colas
     * @param rep Representación
     */
    /*public void discardRepresentation(Representation rep){
        if(rep!=null){
            try{
                rep.exhaust();
            }
            catch (IOException e){
                //Notificación 2º error producido de forma conjunta
            }
            rep.release();
        }
    }*/

    /**
     * Método para actualizar la prioridad de un trabajo
     * @param username Nombre del usuario al que pertenece el trabajo
     * @param idJob Identidad del trabajo en el sistema
     * @param priority Nuevo valor de la prioridad
     * @throws Exception Lanza excepciones cuando no exista el usuario o el trabajo
     */
    public void updatePriority(String username, String idJob, int priority) throws Exception{
    	UserQueue uq;
    	//synchronized(users) {
        if(users.containsKey(username)) {
            uq = users.get(username);
            uq.updatePriority(idJob, priority);   
        }
        else
            throw new Exception("El usuario "+ username +" no existe en el sistema de colas por lo que no se puede actualizar la prioridad del trabajo " +idJob);
    	//}
    	
    }
    
    /**
     * Método para obtener el identificador del trabajo a partir del path donde se van a almacenar los resultados
     * @param path Ruta del servicio de almacenamiento donde se van a almacenar los resultados
     * @return Identificador del trabajo
     */
    private String idJob(String path){
        String s[] = path.split("/");
        //Sigue la estructura: users/<username>/<project>/<simulation>/outputs..
        return s[2]+"/"+s[3];
    }
}
