package dnse3.simulation.client;

//import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import dnse3.common.tasks.Task;

public class WorkerGenerator extends Thread{

    private ConcurrentHashMap<String, Worker> workers;
    private TaskPool taskPool;
    private String queueAddress;
    private final ExecutorService workerPool;
    private BlockingQueue<Worker> preparedWorkers;
    
    public WorkerGenerator(TaskPool taskPool, String queueAddress, int workerLimit){
        this.taskPool=taskPool;
        this.queueAddress = queueAddress;
        this.workers=new ConcurrentHashMap<>();
        this.workerPool = Executors.newFixedThreadPool(workerLimit);
        this.preparedWorkers=new LinkedBlockingQueue<>();
        for(int i=0; i<workerLimit; i++)
            preparedWorkers.add(new Worker(null, this, queueAddress));
    }
    
    @Override
    public void run(){
        while(true){
            try{
                Task task = taskPool.getTask();
                if(task!=null){
                    Worker worker = preparedWorkers.take();
                    worker.setTask(task);
                    System.out.println("Empieza el procesado de la tarea " + task.getId());
                    workers.put(task.getId(), worker);
                    workerPool.execute(worker);
                }
            }catch(InterruptedException e){
                e.printStackTrace();
                System.err.println("Se interrumpió la recogida de la tarea. Se intentará de nuevo");
            }
        }
    }
    
    public void removeWorker(Worker worker, String simulationId){
        if(workers.containsKey(simulationId)&&workers.get(simulationId).equals(worker)){
            Task task = worker.getTask();
            workers.remove(simulationId);
            taskPool.freeTask(task);
            preparedWorkers.add(worker);
        }
    }
    
    public Task getSimulation(String simulationId){
        if(!workers.containsKey(simulationId))
            return null;
        else
            return workers.get(simulationId).getTask();
    }
    
    public void stopSimulation(String simulationId){
        if(workers.containsKey(simulationId))
            workers.get(simulationId).stopSimulation();
    }
    
    public void validTask(Worker worker){
        Task task = worker.getTask();
        taskPool.validTask(task);
    }
}
