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
 * Clase que mantiene la gesti�n de las colas.
 * 
 * @author GSIC gsic.uva.es
 * @version 20191113
 */
public class DNSE3QueueApplication extends Application{

    /** Instancia de la cola de tareas pendientes de realizar por el Servicio de Simulaci�n */
    private TaskQueue simulationQueue;
    /** Instancia de la cola de tareas pendientes de realizar por el Servicio de Informes */
    private TaskQueue reportQueue;
    /** Direcci�n del Servicio de Orquestaci�n. Incluye IP:port */
    private static String orchestrationAddress;
    /** Objeto con el que se asignara un identificador diferente a cada tarea */
    private SecureRandom idGenerator = new SecureRandom();

    /**
     * Constructor de la clase. Solicita una instancia de la cola de Simulaci�n, una instancia 
     * de la cola de Informes y la direcci�n del Servicio de Orquestaci�n.
     * @param simulationQueue Instancia de la cola de Simulaci�n
     * @param reportQueue Instancia de la cola de Informes
     * @param orchestrationAddress Direcci�n del Servicio de Orquestaci�n
     */
    public DNSE3QueueApplication (TaskQueue simulationQueue,TaskQueue reportQueue, String orchestrationAddress){
        this.simulationQueue = simulationQueue;
        this.reportQueue = reportQueue;
        setOrchestrationAddress(orchestrationAddress);
    }

    /**
     * M�todo para establecer la direcci�n del Servicio de Orquestaci�n
     * @param orchestrationAddress Direcci�n del Servicio de Orquestaci�n
     */
    public static void setOrchestrationAddress(String orchestrationAddress) {
        DNSE3QueueApplication.orchestrationAddress = orchestrationAddress;
    }

    /**
     * M�todo para recuperar la direcci�n del Servicio de Orquestaci�n
     * @return Direcci�n del Servicio de Orquestaci�n (IP:port)
     */
    public static String getOrchestrationAddress() {
        return orchestrationAddress;
    }

    /**
     * M�todo para recupar la instancia de la cola de simulaci�n
     * @return Instancia de la cola de simulaciones pendientes de realizar
     */
    public TaskQueue getSimulationQueue() {
        return simulationQueue;
    }

    /**
     * M�todo para recuperar la instancia con la cola de informes
     * @return Instancia con las tareas pendientes de completar por el 
     * Servicio de Informes
     */
    public TaskQueue getReportQueue() {
        return reportQueue;
    }

    /**
     * M�todo de recepci�n de todas las peticiones que le lleguen al Servicio 
     * de Colas. Se especifica qu� petici�n tiene que resolver cada clase.
     * @return Lugar al que derivar la petici�n.
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
     * M�todo utilizado para generar identificadores pseudoaleatorios para las 
     * tareas de cada simulaci�n.
     * @return Identificador �nico de la tarea en el sistema
     */
    public String generateNewSimulationId(){
        String id;
        do{
            id = new BigInteger(65, idGenerator).toString(32);
        }while(containsSimulationId(id));
        
        return id;
    }

    /**
     * M�todo usado para generar identificadores pseudoaleatorios para cada 
     * informe
     * @return Identificador �nico del informe en el sistema
     */
    public String generateNewReportId(){
        String id;
        do{
            id = new BigInteger(65, idGenerator).toString(32);
        }while(containsReportId(id));
        
        return id;
    }

    /**
     * M�todo que comprueba si el identificador est� en uso
     * @param id Identificador a comprobar
     * @return Verdadero si el identificador existe en la cola, falso si no
     * existe en la cola.
     */
    public boolean containsSimulationId(String id){
        return simulationQueue.containsId(id);
    }

    /**
     * M�todo que comprueba si el identificador est� en uso
     * @param id Identificador a comprobar
     * @return Verdadero si el identificador existe en la cola, falso si no
     * existe en la cola.
     */
    public boolean containsReportId(String id){
        return reportQueue.containsId(id);
    }

}
