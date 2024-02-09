package dnse3.simulation.server;

//import java.net.InetAddress;
//import java.net.URI;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

//import dnse3.common.CloudManager;
import dnse3.common.CloudManager;
import dnse3.common.Simulation;
import dnse3.simulation.client.TaskCollector;
import dnse3.simulation.client.TaskPool;
import dnse3.simulation.client.WorkerGenerator;

public class DNSE3SimulationComponent extends Component {

	/**
	 * Constructor de DNSE3SimulationComponent
	 * @param sim simulaci�n a realizar
	 * @param tc recolector de tareas
	 * @param wg generador de trabajador
	 */
	public DNSE3SimulationComponent(Simulation sim, TaskCollector tc, WorkerGenerator wg) { //Metemos este par�metro para llevar la misma referencia
		Server server = getServers().add(Protocol.HTTP, 8083);
		server.getContext().getParameters().set("tracing", "true");
		getDefaultHost().attachDefault(new DNSE3SimulationApplication(tc,wg));
	}

	/**
	 * M�todo principal de la clase DNSE3SimulationComponent. Se encarga de generar la nueva simulaci�n, de iniciar el CloudManager, 
	 * la lista de tareas y el generador de trabajadores.
	 * @param args Necesitamos que nos llegue un array con 4 elementos de la clase String para iniciar todo el proceso.
	 * <p>Primer par�metro:direcci�n del servicio de colas
	 * <p>Segundo par�metro: nombre de usuario del servicio de almacenamiento
	 * <p>Tercer par�metro: contrase�a del usuario del servicio de almacenamiento
	 * <p>Cuarto par�metro: l�mite de tareas
	 * @throws Exception Lanzar� una excepci�n gen�rica cuando no pueda convertir el tercer elemento del vector de Strings a un entero. 
	 * Tambi�n lanzar� una excepci�n cuando el n�mero de par�metros que le llegan a este m�todo no es el adecuado.
	 */
	public static void main(String[] args) throws Exception {
		if(args.length == 2){
			Simulation sim = new Simulation();
			
			int limit = 0;
			try{
				limit = Integer.parseInt(args[1]);
			}catch(Exception e){
				
			}
			
			CloudManager.initialize();
			TaskPool taskPool = new TaskPool(limit);
			TaskCollector tc = new TaskCollector(args[0], taskPool);
			WorkerGenerator wk = new WorkerGenerator(taskPool, args[0], limit); 
			
			new DNSE3SimulationComponent(sim, tc, wk).start();
			
			tc.start();
			wk.start();
		}
		else{
			String m = "N�mero de par�metros introducidos incorrectos";
			String formato = "simulation <addrQueue:PortQueue> <n.Limit> \r\n"
					+ "Ex. simulation 192.168.50.7:8081 1";
			System.err.println(m);
			System.err.println(formato);
			throw new Exception(m);
		}
	}
}
//Necesito llevarme las referencias, al menos de la simulaci�n, aunque tambi�n de el cliente para abortar ejecuciones
