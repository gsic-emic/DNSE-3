package dnse3.queue.server;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import dnse3.queue.data.TaskQueue;
import dnse3.queue.resources.NextTaskResource;
import dnse3.queue.resources.TaskQueueResource;
import dnse3.queue.resources.TaskResource;

/**
 * Clase que mantiene la gestión de las colas.
 * 
 * @author GSIC gsic.uva.es
 * @version 20191113
 */
public class DNSE3QueueApplication extends Application{

    /** Instancia de la cola de tareas pendientes de realizar por el Servicio de Simulación */
    private TaskQueue simulationQueue;
    /** Instancia de la cola de tareas pendientes de realizar por el Servicio de Informes */
    private TaskQueue reportQueue;
    /** Dirección del Servicio de Orquestación. Incluye IP:port */
    private static String orchestrationAddress;
    /** Objeto con el que se asignara un identificador diferente a cada tarea */
    private SecureRandom idGenerator = new SecureRandom();

    /**
     * Constructor de la clase. Solicita una instancia de la cola de Simulación, una instancia 
     * de la cola de Informes y la dirección del Servicio de Orquestación.
     * @param simulationQueue Instancia de la cola de Simulación
     * @param reportQueue Instancia de la cola de Informes
     * @param orchestrationAddress Dirección del Servicio de Orquestación
     */
    public DNSE3QueueApplication (TaskQueue simulationQueue,TaskQueue reportQueue, String orchestrationAddress){
        this.simulationQueue = simulationQueue;
        this.reportQueue = reportQueue;
        setOrchestrationAddress(orchestrationAddress);
    }

    /**
     * Método para establecer la dirección del Servicio de Orquestación
     * @param orchestrationAddress Dirección del Servicio de Orquestación
     */
    public static void setOrchestrationAddress(String orchestrationAddress) {
        DNSE3QueueApplication.orchestrationAddress = orchestrationAddress;
    }

    /**
     * Método para recuperar la dirección del Servicio de Orquestación
     * @return Dirección del Servicio de Orquestación (IP:port)
     */
    public static String getOrchestrationAddress() {
        return orchestrationAddress;
    }

    /**
     * Método para recupar la instancia de la cola de simulación
     * @return Instancia de la cola de simulaciones pendientes de realizar
     */
    public TaskQueue getSimulationQueue() {
        return simulationQueue;
    }

    /**
     * Método para recuperar la instancia con la cola de informes
     * @return Instancia con las tareas pendientes de completar por el 
     * Servicio de Informes
     */
    public TaskQueue getReportQueue() {
        return reportQueue;
    }

    /**
     * Método de recepción de todas las peticiones que le lleguen al Servicio 
     * de Colas. Se especifica qué petición tiene que resolver cada clase.
     * @return Lugar al que derivar la petición.
     */
    @Override
    public Restlet createInboundRoot(){
        Router router = new Router(getContext());
        
        //We use the version in the URIs for retro-compatibility
        router.attach("/v0.1/{typeQueue}/",TaskQueueResource.class);
        router.attach("/v0.1/{typeQueue}/nextTask",NextTaskResource.class);
        router.attach("/v0.1/{typeQueue}/{taskID}",TaskResource.class);
        
        return router;
    }

    /**
     * Método utilizado para generar identificadores pseudoaleatorios para las 
     * tareas de cada simulación.
     * @return Identificador único de la tarea en el sistema
     */
    public String generateNewSimulationId(){
        String id;
        do{
            id = new BigInteger(65, idGenerator).toString(32);
        }while(containsSimulationId(id));
        
        return id;
    }

    /**
     * Método usado para generar identificadores pseudoaleatorios para cada 
     * informe
     * @return Identificador único del informe en el sistema
     */
    public String generateNewReportId(){
        String id;
        do{
            id = new BigInteger(65, idGenerator).toString(32);
        }while(containsReportId(id));
        
        return id;
    }

    /**
     * Método que comprueba si el identificador está en uso
     * @param id Identificador a comprobar
     * @return Verdadero si el identificador existe en la cola, falso si no
     * existe en la cola.
     */
    public boolean containsSimulationId(String id){
        return simulationQueue.containsId(id);
    }

    /**
     * Método que comprueba si el identificador está en uso
     * @param id Identificador a comprobar
     * @return Verdadero si el identificador existe en la cola, falso si no
     * existe en la cola.
     */
    public boolean containsReportId(String id){
        return reportQueue.containsId(id);
    }

}
