package dnse3.queue.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jetty.HttpServerHelper;

import dnse3.common.CloudManager;
import dnse3.queue.data.MetricAgent;
import dnse3.queue.data.TaskQueue;

/**
 * Clase iniciadora del Servicio de Colas. En ella podemos encontrar el m�todo de inicio y 
 * las llamadas al resto de servicios que necesita el Servicio de Colas para operar.
 * 
 * @author GSIC gsic.uva.es
 * @version 20191113
 */
public class DNSE3QueueComponent extends Component {
    
    /**
     * M�todo de inicio del Servicio de Colas. Comprueba que los ficheros de propiedades existan y
     *  extrae la direcci�n del Servicio de Orquestaci�n de uno de ellos. Comprueba que el n�mero 
     * de par�metros por la l�nea de comandos sea el adecuado.
     * @param args Par�metros de entrada para el Servicio de Colas.
     */
    public static void main(String[] args){
        if(args.length==0){
            try {
                File almacen = new File("almacen.properties");
                if(!almacen.exists())
                    throw new Exception("El Servicio de colas necesita el fichero almacen.properties");
                Properties p = new Properties();
                p.load(new FileInputStream("queue.properties"));
                new DNSE3QueueComponent(p.getProperty("dirOrchestra")).start();
            } catch (IOException e) {
                String formato = "Se necesita el fichero queue.properties con la direcci�n IP y puerto de Orchestra" +
                                    "\r\nEjemplo: dirOrchestra=192.168.50.11:8082\r\n";
                System.err.println(formato + e.getMessage());
            } catch(Exception e){
                System.err.println("El Servicio de Colas ha lanzado una excepci�n que llega a la funci�n principal");
                e.printStackTrace();
            }
        }
        else {
            String formato = "El Servicio de Colas no necesita ning�n argumento de entrada";
            System.err.println(formato);
            System.exit(-1);
        }
    }

    /**
     * M�todo que inicializa el proceso por el cual el Servicio de Colas escucha peticiones en el puerto 8081.
     * Crea las instancias de las colas de simulaciones e informes, la inicializaci�n de los m�todos est�ticos
     * con los que se comunica con los servicios de OpenStack y el Servicio de Almacenamiento. Adem�s, inicia 
     * el servicio de m�tricas para la publicaci�n de las tareas pendientes, tanto para la cola de simulaci�n 
     * como para la de informes.
     * @param orchestrationAddress Direcci�n IP y puerto del Servicio de Orquestaci�n
     */
    public DNSE3QueueComponent(String orchestrationAddress) {
        Server server = getServers().add(Protocol.HTTP, 8081);
    	getClients().add(Protocol.HTTP);
        getClients().add(Protocol.HTTPS);
        server.getContext().getParameters().set("tracing", "true");
        //server.getContext().getParameters().add("useForwardedForHeader", "true");
        //server.getContext().getParameters().add("maxQueued", "-1");
        //server.getContext().getParameters().add("maxThreads", "120");

        TaskQueue simulationQueue = new TaskQueue();
        TaskQueue reportQueue = new TaskQueue();
        getDefaultHost().attachDefault(new DNSE3QueueApplication(simulationQueue, reportQueue, orchestrationAddress));
        
        CloudManager.initialize();
        new HttpServerHelper(server);
        new MetricAgent(simulationQueue,reportQueue).start();
    }
}
