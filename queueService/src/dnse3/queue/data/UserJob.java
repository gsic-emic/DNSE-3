package dnse3.queue.data;

import java.util.LinkedList;

import dnse3.common.TaskException;
import dnse3.common.TaskExceptionEnum;

/**
 * Clase diseña para representar a un trabajo de simulación.
 * Este tipo de trabajos puede estar compuesto de un número variable de tareas.
 * 
 * @author GSIC gsic.uva.es
 * @version 20191215
 */
public class UserJob {
    /** Valor con el que se va a evaluar el orden de procesado de las simulaciones */
    private int priority;
    /** Identificador del trabajo en el sistema */
    private String idJob;
    /** Lista de tareas que componen al trabajo */
    private LinkedList<String> listTask;

    /**
     * Constructor de la clase. La prioridad asignada a este proyecto es la de por defecto
     * @param idJob Identificador del trabajo en el sistema
     */
    public UserJob(String idJob){
        this.idJob = idJob;
        priority = 50; //Valor por defecto
        listTask = new LinkedList<>();
    }

    /**
     * Constructor de la clase. Se indica la prioridad del trabajo
     * @param idJob Identificador del trabajo en el sistema
     * @param priority Valor con el que se va a indicar la prioridad del trabajo
     */
    public UserJob(String idJob, int priority){
        this.idJob = idJob;
        this.priority = priority;
        listTask = new LinkedList<>();
    }

    /**
     * Constructor de la clase. Se indican todos los parámetros
     * @param idJob Identificador del trabajo en el sistema
     * @param priority Valor con el que se va a indicar la prioridad del trabajo
     * @param listTask Lista de identificadores de tareas del trabajo
     */
    public UserJob(String idJob, int priority, LinkedList<String> listTask){
        this.idJob = idJob;
        this.priority = priority;
        this.listTask = listTask;
    }

    /**
     * Método para actualizar el valor de la prioridad en el sistema
     * @param priority Valor de la nueva prioridad
     */
    public void setPriority(int priority){
        this.priority = priority;
    }

    /**
     * Método para obtener la prioridad del trabajo
     * @return Valor de la prioridad del trabajo
     */
    public int getPriority(){
        return priority;
    }

    /**
     * Método para obtener el idientificador del trabajo
     * @return Identificador del trabajo
     */
    public String getIdJob(){
        return idJob;
    }

    /**
     * Método para agregar una lista de tareas al trabajo
     * @param listTask Lista de tareas a agregar. Sustuye a la que 
     * tenía asociada el trabajo
     */
    public void setListTask(LinkedList<String> listTask){
        this.listTask = listTask;
    }

    /**
     * Método para obtener la lista de tareas del trabajo
     * @return Lista de tareas del trabajo
     */
    public LinkedList<String> getListTask(){
        return listTask;
    }

    /**
     * Método para agregar una tarea al trabajo
     * @param idTask Identificador de la tarea
     */
    public void addTask(String idTask){
        synchronized(listTask){
            if(listTask.contains(idTask))
                System.err.println("Se está intentando agregar una tarea en el trabajo "+idJob+" que ya existía.");
            else
                listTask.add(idTask);
        }
    }

    /**
     * Método para obtener la siguiente tarea del trabajo
     * @return Identificador de la tarea a realizar. Puede devolver null si la lista está vacía
     */
    public String getNextTask(){
        String task = null;
        synchronized(listTask){
            if(!listTask.isEmpty()){
                task = listTask.pop();
                listTask.add(task);
            }
        }
        return task;
    }

    /**
     * Método para eliminar una tarea del trabajo
     * 
     * @param task Identificador de la tarea a eliminar
     * @throws TaskException La tarea no existía en el trabajo
     */
    public void removeTask(String task) throws TaskException {
        synchronized(listTask){
            if(!listTask.remove(task)){
                throw new TaskException(TaskExceptionEnum.NOT_FOUND);
            }
        }
    }

    /**
     * Método para obtener el número de tareas del trabajo que no se están procesando
     * @return Número de tareas en el trabajo que no se están procesando
     */
    public int getSize(){
        return listTask.size();
    }

}