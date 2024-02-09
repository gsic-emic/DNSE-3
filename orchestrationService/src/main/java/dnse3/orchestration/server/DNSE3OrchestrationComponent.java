package dnse3.orchestration.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.routing.VirtualHost;
import org.restlet.ext.jetty.HttpServerHelper;

import dnse3.common.CloudManager;

public class DNSE3OrchestrationComponent extends Component {
	
	/**
	 * Clase principal del Servicio de Orquestación. Desde este punto se incian las 
	 * escuchas de los distintos puertos que se van a utilizar. Inicia el resto de
	 * tareas que realiza el Servicio de Orquestación.
	 * @param args Este servicio no necesita ningún parámetro en la entrada.
	 */
	public static void main(String[] args){
		String formato = "";
		boolean err = false;
		if(args.length != 0) {
			formato = "Orquestación no necesita ningún parámetro de entrada.\r\n"
					+ "Si se necesita realizar algún cambio hay que modificar el fichero"
					+ "orchestration.properties";
			err = true;
		}
		File f = new File("almacen.properties");
		if(!f.exists()){
			formato = "El Servicio necesita el finchero \"almacen.properties\" "
					+ "en el directorio donde se esté ejecutando.\n";
			err = true;
		}
		f = new File("Makefile");
		if(!f.exists()){
			formato = "El Servicio necesita el finchero \"Makefile\" "
					+ "en el directorio donde se esté ejecutando.\n";
			err = true;
		}
		f = new File("orchestra.properties");
		if(!f.exists()){
			formato = "El Servicio necesita el finchero \"orchestra.properties\" "
					+ "en el directorio donde se esté ejecutando.\n";
			err = true;
		}
		f = null;
		if(err){
			System.err.println(formato);
			System.exit(-1);
		} else{
			try {
				CloudManager.initialize();
				new DNSE3OrchestrationComponent().start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Construcctor de la clase. Inicia la escucha en los distintos puertos.
	 * Crea el directorio temporal donde se compilaran los distintos scripts que 
	 * utilicen los usuarios.
	 */
	public DNSE3OrchestrationComponent(){
		try {
			Properties properties = new Properties();
			
			File propFile = new File("orchestra.properties");
			File jarPath = new File(CloudManager.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			File homePath = jarPath.getParentFile();
			if(!propFile.exists())
				propFile=new File(homePath,"orchestra.properties");
			
			File tmp = new File(homePath,"tmp");
			if (!tmp.exists()) {
				if (tmp.mkdirs()) {
					System.out.println("Directory is created!");
				} else {
					System.out.println("Failed to create directory!");
				}
			}
			
			System.out.println("El servicio tiene todos los ficheros iniciales y comienza la configuración");
			InputStream input = new FileInputStream(propFile);
			properties.load(input);
					
			Server server = getServers().add(Protocol.HTTP, 8080);
			server.getContext().getParameters().set("tracing", "true");
			Server notification = getServers().add(Protocol.HTTP,8082);
			notification.getContext().getParameters().set("tracing", "true");
			
			//Need to create the clients as well
			getClients().add(Protocol.HTTP);
			getClients().add(Protocol.HTTPS);
			getClients().add(Protocol.CLAP);
			
			
			System.out.println("Dirección del Servicio de Colas: " + properties.getProperty("queueAddress"));
			//DNSE3OrchestrationApplication.setQueueAddress(queueAddress);
			DNSE3OrchestrationApplication orchestrationApplication = new DNSE3OrchestrationApplication(properties.getProperty("queueAddress"));
			getDefaultHost().setServerPort("8080");
			getDefaultHost().attachDefault(orchestrationApplication);
			
			VirtualHost host = new VirtualHost();
			host.setServerPort("8082");
			host.attachDefault(new DNSE3ListenerApplication(orchestrationApplication.getExecutionController()));
			getHosts().add(host);
			new HttpServerHelper(server);
			new HttpServerHelper(notification);
			
//			Intentando poner el log de RESTlet
//			System.setProperty("java.util.logging.config.file", "log.properties");
//			getLogService().setLoggerName("DNSE3.AccessLog");
//			String direction=LocalReference.createClapReference(getClass().getProtectionDomain().getCodeSource().getLocation().getPath())+"/log.properties";
//			String direction="clap:///log.properties";
//			System.out.println(direction);
//			getLogService().setLogPropertiesRef(direction);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
}
