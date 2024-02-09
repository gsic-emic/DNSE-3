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
     * @param taskLimit límite de tareas
     */
    public TaskPool(int taskLimit){
        this.taskLimit=taskLimit;
        this.tasks=new CopyOnWriteArrayList<>();
        this.processingTasks=new CopyOnWriteArrayList<>();
    }
    
    /**
     * Método para agregar tareas al final de la lista. Si está superando el límite se espera hasta que se liberen recursos.
     * @param task tarea a agregar a la lista.
     * @throws InterruptedException lanzará esta excepción si la tarea que está ejecutando se ve interrumpida.
     */
    public void putTask(Task task) throws InterruptedException{
        lock.lock();
        try{
            tasks.add(task);
            pickTask=false;
            notEmpty.signal();
            System.out.println("Tamaño lista tras publicar: "+tasks.size());
            while(!pickTask)
                free.await();
            while(tasks.size()>=taskLimit)
                notFull.await(); //Espera a que se haya liberado
            System.out.println("Tamaño lista tras espera: "+tasks.size());
        }finally {
            lock.unlock();
        }
    }
    
    /**
     * Método usado para recoger una de las tareas de la lista. Si no hay tareas en la lista de tareas espera a que le lleguen.
     * @throws InterruptedException la ejecución de la tarea se ve interrumptida
     * @return La tarea que ha sacado de la lista
     */
    public Task getTask() throws InterruptedException{
        lock.lock();
        try{
            System.out.println("Tamaño lista antes de pedir: "+tasks.size());
            System.out.println("Tareas cogidas antes de pedir: "+processingTasks.size());
            while(tasks.size()==0 || tasks.size()==processingTasks.size()) //Si está vacía la cola o todas las tareas están cogidas
                notEmpty.await(); //Espera a que haya tareas
            for(Task t: tasks){
                if(!processingTasks.contains(t)){
                    processingTasks.add(t);
                    //if(tasks.size()<taskLimit) //Si no se ha llegado al límite
                    //	notFull.signal();
                    System.out.println("Tamaño lista después de pedir: "+tasks.size());
                    System.out.println("Tareas cogidas después de pedir: "+processingTasks.size());
                    return t;
                }
            }
            return null;
        }finally{
            lock.unlock();
        }
    }
    
    /**
     * Método para liberar una tarea de la lista de tareas.
     * @param task tarea a eliminar de la lista
     */
    public void freeTask(Task task){
        lock.lock();
        try{
            if(processingTasks.contains(task)){
                tasks.remove(task);
                if(tasks.size()<taskLimit) //Si no se ha llegado al límite
                    notFull.signal();
                processingTasks.remove(task);
            }
        }finally {
            lock.unlock();
        }
    }

    /**
     * Método para comprobar si la tarea está en la lista de tareas que se están ejecutando.
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
