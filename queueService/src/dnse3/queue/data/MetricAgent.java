package dnse3.queue.data;

import org.apache.log4j.Logger;

import dnse3.common.CloudManager;
import dnse3.common.MetricType;

/**
 * Clase utilizada para notificar a los servicios de escalado el n�mero de tareas
 * pendientes de completar de cada cola. Tambi�n escribe en un log la cantidad de 
 * tareas pendientes de completar en cada instante.
 * 
 * @author GSIC gsic.uva.es
 * @version 20191113
 */
public class MetricAgent extends Thread{
    /** Instancia de la cola de simulaci�n */
    private TaskQueue simulationQueue;
    /** Instancia de la cola de informes */
    private TaskQueue reportQueue;
    /** Instancia que identifica el log donde almacenar los cambios en el n�mero
     * de tareas pendientes de completarse */
    private static Logger logger = Logger.getLogger("DNSE3QueueLogger");
    /** Objeto que almacena el valor de la �ltima m�trica publicada con las simulaciones
     * pendientes */
    
     /**
      * Constructor de la clase. Realiza el almacenamiento de las instancias de 
      * las colas de simulaci�n e informes
      * @param simulationQueue Instancia de la cola de Simulaci�n
      * @param reportQueue Instancia de la cola de Informes
      */
    public MetricAgent(TaskQueue simulationQueue, TaskQueue reportQueue){
        this.simulationQueue = simulationQueue;
        this.reportQueue = reportQueue;
    }

    /**
     * M�todo que se ejecuta en bucle desde que se inicia el Servicio de Colas. 
     * Realiza una publicaci�n de m�trica cuando detecte un cambio. Publica en 
     * el log cada segundo
     */
    public void run(){
        int simulationSize = 0, previousSimulationSize = 0;
        int reportSize = 0 , previousReportSize = 0;
        boolean publica = false;
        CloudManager.publishMetric("simulation", MetricType.GAUGE.toString(), (double) simulationSize, "");
        CloudManager.publishMetric("report", MetricType.GAUGE.toString(), (double) reportSize, "");
        while(true){
            //Falta tasks/instance
            simulationSize = simulationQueue.getSize();
            reportSize = reportQueue.getSize();
            
            //Env�a siempre
            //CloudManager.publishMetric("simulation", MetricType.GAUGE.toString(), (double) simulationSize, "");
            //CloudManager.publishMetric("report", MetricType.GAUGE.toString(), (double) reportSize, "");
            
            //Solo se env�a una actualizaci�n a Scale cuando se detecte un cambio
            if(simulationSize != previousSimulationSize){
                previousSimulationSize = simulationSize;
                CloudManager.publishMetric("simulation", MetricType.GAUGE.toString(), (double) simulationSize, "");
            }
            if(reportSize != previousReportSize){
                previousReportSize = reportSize;
                CloudManager.publishMetric("report", MetricType.GAUGE.toString(), (double) reportSize, "");
            }
            if(publica){
                logger.info("dnse3.queue.simulation.size | " + simulationSize);
                logger.info("dnse3.queue.report.size | " + reportSize);
            }
            publica = !publica;
            synchronized(this){
                try {
                    wait(2000);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

}
