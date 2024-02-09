package dnse3.simulation.client;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import dnse3.common.tasks.Task;

public class TaskPool {
    
    private int taskLimit;
    private CopyOnWriteArrayList<Task> tasks;
    private CopyOnWriteArrayList<Task> processingTasks;
    private boolean pickTask;
    
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();
    private final Condition free = lock.newCondition();
    
    /**
     * Constructor de la clase TaskPool.
     * @param taskLimit l�mite de tareas
     */
    public TaskPool(int taskLimit){
        this.taskLimit=taskLimit;
        this.tasks=new CopyOnWriteArrayList<>();
        this.processingTasks=new CopyOnWriteArrayList<>();
    }
    
    /**
     * M�todo para agregar tareas al final de la lista. Si est� superando el l�mite se espera hasta que se liberen recursos.
     * @param task tarea a agregar a la lista.
     * @throws InterruptedException lanzar� esta excepci�n si la tarea que est� ejecutando se ve interrumpida.
     */
    public void putTask(Task task) throws InterruptedException{
        lock.lock();
        try{
            tasks.add(task);
            pickTask=false;
            notEmpty.signal();
            System.out.println("Tama�o lista tras publicar: "+tasks.size());
            while(!pickTask)
                free.await();
            while(tasks.size()>=taskLimit)
                notFull.await(); //Espera a que se haya liberado
            System.out.println("Tama�o lista tras espera: "+tasks.size());
        }finally {
            lock.unlock();
        }
    }
    
    /**
     * M�todo usado para recoger una de las tareas de la lista. Si no hay tareas en la lista de tareas espera a que le lleguen.
     * @throws InterruptedException la ejecuci�n de la tarea se ve interrumptida
     * @return La tarea que ha sacado de la lista
     */
    public Task getTask() throws InterruptedException{
        lock.lock();
        try{
            System.out.println("Tama�o lista antes de pedir: "+tasks.size());
            System.out.println("Tareas cogidas antes de pedir: "+processingTasks.size());
            while(tasks.size()==0 || tasks.size()==processingTasks.size()) //Si est� vac�a la cola o todas las tareas est�n cogidas
                notEmpty.await(); //Espera a que haya tareas
            for(Task t: tasks){
                if(!processingTasks.contains(t)){
                    processingTasks.add(t);
                    //if(tasks.size()<taskLimit) //Si no se ha llegado al l�mite
                    //	notFull.signal();
                    System.out.println("Tama�o lista despu�s de pedir: "+tasks.size());
                    System.out.println("Tareas cogidas despu�s de pedir: "+processingTasks.size());
                    return t;
                }
            }
            return null;
        }finally{
            lock.unlock();
        }
    }
    
    /**
     * M�todo para liberar una tarea de la lista de tareas.
     * @param task tarea a eliminar de la lista
     */
    public void freeTask(Task task){
        lock.lock();
        try{
            if(processingTasks.contains(task)){
                tasks.remove(task);
                if(tasks.size()<taskLimit) //Si no se ha llegado al l�mite
                    notFull.signal();
                processingTasks.remove(task);
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * M�todo para comprobar si la tarea est� en la lista de tareas que se est�n ejecutando.
     * @param task tarea a comprobar si existe.
     */
    public void validTask(Task task){
        lock.lock();
        try{
            if(processingTasks.contains(task)){
                pickTask=true;
                free.signal();
            }
        }finally {
            lock.unlock();
        }
    }
}
