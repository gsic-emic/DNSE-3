package dnse3.queue.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import dnse3.common.TaskException;
import dnse3.common.TaskExceptionEnum;

/**
 * Clase para gestionar la cola de cada usuario
 * 
 * @author GSIC gsic.uva.es
 * @version 20191215
 */
public class UserQueue {

    /** Lista de tareas que se están realizando del usuario */
    private LinkedList<String> processingTasks;
    /** Cola de tareas del usuario */
    private TaskQueue queue;
    /** Usuario propietario de la cola */
    private String username;
    /**Entidad que almacena el identificador del trabajo y al propio trabajo*/
    private HashMap<String, UserJob> userJob;
    /** Log del servicio de colas */
    private static final Logger workerLogger = Logger.getLogger("DNSE3WorkerLogger");
    /** Instacia de la clase Random utilizada para generar valores pseudoaleatorios */
    private Random random;

    /**
     * Constructor de la clase
     * @param queue Cola de tareas
     * @param username Identificador del usuario
     */
    public UserQueue(TaskQueue queue, String username) {
        processingTasks = new LinkedList<>();
        this.queue = queue;
        this.username = username;
        userJob = new HashMap<>();
        random = new Random();
    }

    /**
     * Método para comprobar si un trabajo existe en la cola del usuario
     * @param idJob Identificador del trabajo
     * @return Verdadero si existe, falso si no existe
     */
    public boolean jobInUser(String idJob){
        for (UserJob uj : userJob.values()) {
            if(idJob.equals(uj.getIdJob()))
                return true;
        }
        return false;
    }

    /**
     * Método para añadir una nueva tarea a un trabajo
     * @param idJob Identificador del trabajo
     * @param idTask Identificador de la tarea
     * @param priority Prioridad con la que se va a realizar el trabajo. Sólo se utiliza cuando se crea el trabajo
     */
    public void addNewTask(String idJob, String idTask, int priority){
        UserJob uj;
        if(!userJob.containsKey(idJob)){
            uj = new UserJob(idJob, priority);
            synchronized(userJob){
                userJob.put(idJob, uj);//Se crea el trabajo si no existe
            }
        }
        else {
            uj = userJob.get(idJob);
        }
        uj.addTask(idTask);
    }

    /**
     * Método para obtener la siguiente tarea a procesar
     * @return El identificador de la tarea que se puede procesar
     */
    public String getNextTask(){
        //Tareas que se están procesando
        synchronized(processingTasks){
            ArrayList<String> notUpdated = new ArrayList<>();
            for (int i = 0; i < processingTasks.size(); i++) {
                String id = processingTasks.poll();
                processingTasks.add(id);
                try {
                    if (queue.isExpired(id)) {
                        workerLogger.warn("Task "+id+" expired. Worker: "+queue.getTask(id).getWorkerURI());
                        return id;
                    }
                } catch (TaskException e) {
                    notUpdated.add(id);
                }
            }
            for (String s : notUpdated)
                processingTasks.remove(s);
            notUpdated = null;
        }

        //Se comprueba de qué trabajo se va a devolver la tarea a partir de los pesos de cada trabajo
        UserJob uj;
        synchronized(userJob){
            if(userJob.isEmpty()) {
                return null;
            }
            else{
                //Rotación por pesos
                HashMap<String, Integer> trabajoPrioridad = priorityJob(); //dupla trabajo prioridad
                TreeMap<Integer, String> treeMap = new TreeMap<>();
                int totalWeight = 0;

                if(trabajoPrioridad!=null){
                    for(String i : trabajoPrioridad.keySet()){
                        totalWeight += trabajoPrioridad.get(i);
                        //Se asigna el espacio muestral de cada trabajo dependiendo de su prioridad
                        treeMap.put(totalWeight, i);
                    }
    
                    trabajoPrioridad = null;
                }
                
                //System.out.println(treeMap.toString());
                
                try{
                    //Se escoge uno de los trabajos. Es más probable escoger trabajos con prioridades altos debido a que
                    // su espacio muestral es más grande.
                    if(treeMap.size()>0){//Compruebo que el map tenga algún valor para evitar que salte la excepción
                        String idJob = treeMap.ceilingEntry(random.nextInt(totalWeight)).getValue();
                        //System.out.println(idJob);
                        uj = userJob.get(idJob);
                    }
                    else{
                        return null;
                    }
                }catch(ClassCastException | NullPointerException | IllegalArgumentException | IllegalStateException e){
                    return null;
                }
            }
        }
        return uj.getNextTask();
    }

    /**
     * Método para obtener todos los trabajos del usuario con la prioridad que le ha dado
     * @return HashMap con las duplas idJob - priority. Devuelve null si la lista de trabajos está vacía
     */
    private HashMap<String, Integer> priorityJob(){
        HashMap<String, Integer> salida;
        if(userJob.size()>0){
            salida = new HashMap<>();
            for(UserJob uj : userJob.values())
                salida.put(uj.getIdJob(), uj.getPriority());
            return salida;
        }
        else{
            return null;
        }
    }

    /**
     * Método para eliminar una tarea de la cola del usuario
     * @param idJob Trabajo donde está la tarea
     * @param idTask Identificador de la tarea
     */
    public void removeTask(String idJob, String idTask){
        synchronized(processingTasks){
            if(processingTasks!=null && processingTasks.contains(idTask))
                processingTasks.remove(idTask);
        }
        //synchronized(userJob){ 
        if(userJob.containsKey(idJob)){
            UserJob uj = userJob.get(idJob);
            try{
                uj.removeTask(idTask);
            }catch(TaskException e){
            }
            if(uj.getSize() == 0){
                synchronized(userJob){
                    userJob.remove(idJob);
                }
            }
        }
        //}
    }

    /**
     * Método para comprobar si la cola del usuario está vacía
     * @return Verdadero si está vacía, false en caso contrario
     */
    public boolean isEmpty(){
        //synchronized(processingTasks){ synchronized(userJob){ 
        if(userJob != null){
            if(processingTasks != null)
                return (userJob.isEmpty() && processingTasks.isEmpty());
            else
                return userJob.isEmpty();
        }
        else{
            if(processingTasks != null)
                return processingTasks.isEmpty();
            else
                return true;
        }
        //} }
    }

    /**
     * Método para obtener el tamaño de la cola del usuario
     * @return Tamaño de la cola del usuario
     */
    public int getSize(){
        int tama = 0;
        //synchronized(processingTasks){ synchronized(userJob){ 
        if(userJob != null && processingTasks != null){
            for(UserJob uj : userJob.values())
                tama += uj.getSize();
            tama += processingTasks.size();
        }
        //} }
        return tama;
    }

    /**
     * Método para marcar una tarea concreta para que se procese
     * @param idJob Identificador del trabajo
     * @param idTask Identificador de la tarea
     * @throws TaskException El trabajo no existe o la tarea no existe
     */
    public void setProcessing(String idJob, String idTask) throws TaskException{
        //synchronized(userJob){ 
        if(userJob != null & userJob.containsKey(idJob)){
            UserJob uj = userJob.get(idJob);
            uj.removeTask(idTask);
            //if(uj.getSize() == 0){
                //userJob.remove(idJob);
            //}
            synchronized(processingTasks){
                processingTasks.add(idTask);
            }
        }
        else {
            throw new TaskException(TaskExceptionEnum.NOT_FOUND);
        }
        //}
    }

    /**
     * Método para marcar la tarea como finalizada
     * @param idJob Identificador del trabajo donde se encuentra la tarea
     * @param idTask Identificador de la tarea finalizada
     */
    public void setFinished(String idJob, String idTask) {
        removeTask(idJob, idTask);
    }

    /**
     * Método para obtener el nombre del usuario al que pertenece la cola
     * @return Nombre de usuario
     */
    public String getUsername() {
        return username;
    }

    /**
     * Método para averiguar si una tarea existe en la cola del usuario
     * @param idTask Tarea a comprobar
     * @return True si existe en el sistema, false si no existe
     */
    public boolean containsId(String idTask){
        boolean trabajos = false;
        for (UserJob uj : userJob.values()) {
            if(uj.getListTask().contains(idTask)){
                trabajos = true;
                break;
            }
        }
        return trabajos || processingTasks.contains(idTask);
    }

    /**
     * Método para actualizar la prioridad de un trabajo
     * @param idJob Identificador del trabajo a actualizar
     * @param priority Nuevo valor de la prioridad
     * @throws Exception En la cola del usuario no existe el trabajo
     */
    public void updatePriority(String idJob, int priority) throws Exception{
        //synchronized(userJob){
        if(userJob.containsKey(idJob)){
            UserJob uj = userJob.get(idJob);
            uj.setPriority(priority);
        }
        else
            throw new Exception("La cola del usuario no contiene el trabajo "+idJob);
        //}
    }
    
    /**
     * Método para obtener las tareas en ejecución del usuario
     * @return Tareas que se están ejecutando
     */
    public LinkedList<String> getProcessingTasks() {
    	return processingTasks;
    }
}
