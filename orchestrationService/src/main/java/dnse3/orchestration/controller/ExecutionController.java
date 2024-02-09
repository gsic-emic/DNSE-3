package dnse3.orchestration.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import dnse3.common.CloudManager;
import dnse3.common.tasks.OutputFileSummary;
import dnse3.common.tasks.Task;
import dnse3.orchestration.auxiliar.RegistraLog;
import dnse3.orchestration.jpa.controller.JpaController;
import dnse3.orchestration.jpa.model.project.OutputFile;
import dnse3.orchestration.jpa.model.simulation.Parameter;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.SimulationStatus;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;
import dnse3.orchestration.jpa.model.user.User;

public class ExecutionController {

	private JpaController jpaController;
	private DataController dataController; //Necesito el gestor de datos? Puede que no, procesa las peticiones del servidor casi principalmente, la recuperaci�n de los elementos es con JpaController
	private String queueAddress;
	private HashMap<String, Integer> userQuota;
	private HashMap<Long, Integer> simulationRepetitions; //Solo las individuales
	private HashMap<Long, ArrayList<Long>> parameterToSingleSimulations; //Contiene el listado de simulaciones individuales de cada barrido (por si se pausa o se cancela)
	private HashMap<Long, Long> singleToParameterSimulations; //Contiene el identificador de la simulaci�n de barrido due�a de la simulaci�n individual
	private HashMap<Long, String> simulationUser; //Identifica el usuario de cada simulaci�n
	private HashMap<Long, HashMap<Integer, String>> simulationAddress; //Contiene los URIs del servicio de colas
	private HashMap<Long, HashSet<Integer>> simulationCompleted;
	private ExecutorService publisherPool;

	//Eliminaci�n de cola
	private LinkedList<String> tasksToRemove;
	private LinkedList<Long> simulationsToRemove;
	private HashMap<Long, ArrayList<String>> simulationsRemovingAddress;
	private ArrayList<Long> parameterSweepsToRemove;
	private ArrayList<Long> simulationsRemainingToRemove; //Comprobaci�n cruzada
	private final Lock lock = new ReentrantLock();
	private final Condition notEmpty = lock.newCondition();
	private ExecutorService removingPool;

	//Atributos seg�n los vaya necesitando :)

	//Informes
	private HashMap<Long, SimulationTypeEnum> reportsType;
	private HashMap<Long, ArrayList<Long>> sweepSinglesCompleted;

	//private static final Logger usersLogger = Logger.getLogger("DNSE3UsersLogger");

	public ExecutionController(JpaController jpaController, DataController dataController, String queueAddress){
		this.jpaController = jpaController;
		this.dataController = dataController;
		this.queueAddress = queueAddress;
		this.dataController.setExecutionController(this);
		this.userQuota = new HashMap<>();
		this.simulationRepetitions = new HashMap<>();
		this.parameterToSingleSimulations = new HashMap<>();
		this.singleToParameterSimulations = new HashMap<>();
		this.simulationUser = new HashMap<>();
		this.simulationAddress = new HashMap<>();
		this.simulationCompleted = new HashMap<>();
		this.publisherPool = Executors.newCachedThreadPool();

		//Eliminaci�n
		this.tasksToRemove = new LinkedList<>();
		this.simulationsToRemove = new LinkedList<>();
		this.simulationsRemovingAddress = new HashMap<>();
		this.simulationsRemainingToRemove = new ArrayList<>();
		this.parameterSweepsToRemove = new ArrayList<>();
		this.removingPool = Executors.newFixedThreadPool(4); //De momento fijo en 4 los hilos de eliminaci�n, se puede poner variable
		for(int i = 0; i<4; i++){
			TaskRemoval removal = new TaskRemoval(this);
			removingPool.execute(removal);
		}

		//Informes
		reportsType = new HashMap<>();

		sweepSinglesCompleted = new HashMap<>();
	}

	/**
	 * M�todo para inicializar una simulaci�n
	 * @param simulationId Identificador de la simulaci�n
	 * @param type Tipo de simulaci�n
	 * @throws Exception
	 */
	public void startSimulation(long simulationId, SimulationTypeEnum type) throws Exception{
		//Pasos a realizar
		//
		//  -Recuperaci�n de la simulaci�n
		//  -Comprobaci�n del estado
		//  	-Si es un estado err�neo, abortar
		//		-Si est� en espera o en pausa, continuar
		//	-Comprobar que el usuario tiene cuota
		//	-Crear el objeto con la petici�n
		//  -Crear la petici�n de publicaci�n en cola (hilo)
		//		-Registrar el identificador de la simulaci�n y una lista con las repeticiones
		//		-En cada repetici�n, guardar la direcci�n del servicio de cola

		switch(type){
		case SINGLE_SIMULATION:{
			SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
			if(!simulation.getStatus().equals(SimulationStatus.WAITING) && !simulation.getStatus().equals(SimulationStatus.PAUSED)){ //Estado err�neo
				throw new Exception("No se puede inciar una simulaci�n que no est� parada o esperando");
			}
			//Se crean los directorios iniciales para reducir el n�mero de HEAD en la carga de resultados de las primeras simulaciones
			if(simulation.getStatus().equals(SimulationStatus.WAITING)){
				String primeraParte = "users/"+ simulation.getProject().getUser().getUsername() + "/" + simulation.getProject().getId() + "/" + simulationId + "/";
				if(!(CloudManager.newFolder(primeraParte)) || !(CloudManager.newFolder(primeraParte + "outputs/")))
					throw new Exception("No se puden crear los directorios donde se van a guardar los resultados, por lo que no se comienza la simulaci�n");
			}

			User user = simulation.getProject().getUser();
			String username = user.getUsername();

			int quota;
			synchronized(userQuota){
				if(userQuota.get(username) == null)
					userQuota.put(username, 0);
				quota = userQuota.get(username);
				//Comparaci�n de la cuota. En el HashMap se guarda el uso actual (en cola) y las que se han prerregistrado.
				if((quota + simulation.getRemainingSimulations()) > user.getSimulationLimit()){ //Excede la cuota
					throw new Exception("El usuario excede la cuota. No puede iniciar la simulaci�n");
				}
			}

			simulation.setStatus(SimulationStatus.PREPARING);
			simulation.setStartDate(new Date());
			jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
			synchronized(userQuota){
				userQuota.put(username, quota + simulation.getRemainingSimulations()); //Reservo antes   
			}
			synchronized(simulationUser){
				simulationUser.put(simulationId, username);
			}
			synchronized(simulationRepetitions){
				simulationRepetitions.put(simulationId, simulation.getRemainingSimulations());
			}

			//Comienzo del hilo
			PublisherTask publisher = new PublisherTask(username, simulation, this, this.queueAddress);
			publisherPool.execute(publisher);
			String extra = "{Priority:"+simulation.getPriority()+"}";
			RegistraLog.registra(RegistraLog.iniSimula, username, simulation.getProject().getId(), simulationId, extra);
			//usersLogger.info("Username:"+username + " - ProjectId:" + simulation.getProject().getId() + " - SimulationId:" + simulationId + " - Priority:" + simulation.getPriority() + " - Simulaciones:"+simulation.getNumRepetitions()+" - INICIO SIMULACI�N INDIVIDUAL");
			break;
		}
		case PARAMETER_SWEEP_SIMULATION:{
			ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
			if(!simulation.getStatus().equals(SimulationStatus.WAITING) && !simulation.getStatus().equals(SimulationStatus.PAUSED)){ //Estado err�neo
				throw new Exception("No se puede inciar una simulaci�n que no est� parada o esperando");
			}

			//Se crean los directorios iniciales para reducir el n�mero de HEAD en la carga de resultados de las primeras simulaciones
			if(simulation.getStatus().equals(SimulationStatus.WAITING)){
				String primeraParte = "users/"+ simulation.getProject().getUser().getUsername() + "/" + simulation.getProject().getId() + "/" + simulationId + "/";
				if(!(CloudManager.newFolder(primeraParte)) || !(CloudManager.newFolder(primeraParte + "outputs/")))
					throw new Exception("No se puden crear los directorios donde se van a guardar los resultados, por lo que no se comienza la simulaci�n");
			}

			User user = simulation.getProject().getUser();
			String username = user.getUsername();
			int quota;
			synchronized (userQuota) {
				if(userQuota.get(username) == null)
					userQuota.put(username, 0);
				quota = userQuota.get(username);
				//Comparaci�n de la cuota. En el HashMap se guarda el uso actual (en cola) y las que se han prerregistrado.
				if((quota + simulation.getRemainingSimulations()) > user.getSimulationLimit()){ //Excede la cuota
					throw new Exception("El usuario excede la cuota. No puede iniciar la simulaci�n");
				}
			}
			jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);

			simulation.createSingleSimulations();
			for(SingleSimulation s: simulation.getSingleSimulations())
				jpaController.getSingleSimulationController().createSingleSimulation(s,simulation.getProject().getId());

			simulation.setStatus(SimulationStatus.PREPARING);
			simulation.setStartDate(new Date());
			jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
			synchronized(userQuota){
				userQuota.put(username, quota + simulation.getRemainingSimulations());
			}
			ArrayList<Long> ids = new ArrayList<>();
			long idIndi;
			for(SingleSimulation s : simulation.getSingleSimulations()){
				idIndi = s.getId();
				synchronized(simulationUser){
					simulationUser.put(idIndi, username);
				}
				synchronized(simulationRepetitions){
					simulationRepetitions.put(idIndi, s.getRemainingSimulations());
				}
				synchronized(singleToParameterSimulations){
					singleToParameterSimulations.put(idIndi, simulationId);
				}
				ids.add(idIndi);
			}
			synchronized(parameterToSingleSimulations){
				parameterToSingleSimulations.put(simulationId, ids);
			}
			ids = null;

			PublisherTask publisher = new PublisherTask(username, simulation, this, this.queueAddress);
			publisherPool.execute(publisher);
			//usersLogger.info("Username:"+username + " - ProjectId:" + simulation.getProject().getId() + " - SimulationId:" + simulationId + " - Priority:"+ simulation.getPriority() + " - Simulaciones:" + simulation.getRemainingSimulations() +" - INICIO SIMULACI�N DE BARRIDO");
			String extra = "{Priority:"+simulation.getPriority()+"}";
			RegistraLog.registra(RegistraLog.iniSimula, username, simulation.getProject().getId(), simulationId, extra);
			break;
		}
		default:
			throw new Exception("Tipo de simulaci�n no registrada");
		}

	}

	/**
	 * M�todo para pausar una simulaci�n que se est� ejecutando
	 * @param simulationId Identificador de la simulaci�n
	 * @param type Tipo de simulaci�n
	 * @throws Exception
	 */
	public void pauseSimulation(long simulationId, SimulationTypeEnum type) throws Exception{
		switch(type){
		case SINGLE_SIMULATION: {
			synchronized(simulationRepetitions){
				if(!simulationRepetitions.containsKey(simulationId)){
					throw new Exception("La simulaci�n tiene que estar registrada");
				}
			}
			SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
			if(!simulation.getStatus().equals(SimulationStatus.PROCESSING)){
				throw new Exception("La simulaci�n se tiene que estar procesando");
			}

			simulation.setStatus(SimulationStatus.PAUSED);
			synchronized(simulationRepetitions){
				simulationRepetitions.remove(simulationId);
			}
			HashMap<Integer, String> individualExecutions;
			synchronized (simulationAddress) {
				individualExecutions = simulationAddress.get(simulationId);
			}
			//Poner las tareas para eliminar
			lock.lock();
			try{
				synchronized(simulationsRemovingAddress){
					simulationsRemovingAddress.put(simulationId, new ArrayList<>(individualExecutions.values()));
				}
				synchronized(simulationsToRemove){
					simulationsToRemove.add(simulationId);
				}
				notEmpty.signal();
			}
			finally{
				lock.unlock();
			}
			synchronized (simulationCompleted) {
				if(simulationCompleted.get(simulationId)!=null)
					simulation.getCompletedRepetitions().addAll(simulationCompleted.get(simulationId));
			}
			synchronized(simulationAddress){
				simulationAddress.remove(simulationId);
			}
			String username = simulation.getProject().getUser().getUsername(); 

			synchronized(userQuota){
				int quota = userQuota.get(username);
				userQuota.put(username, ((quota - individualExecutions.size())<0)?0:quota - individualExecutions.size());
			}
			individualExecutions = null;
			jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
			break;
		}
		case PARAMETER_SWEEP_SIMULATION:{
			if(!parameterToSingleSimulations.containsKey(simulationId)){
				throw new Exception("La simulaci�n tiene que estar registrada");
			}
			ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
			if(!simulation.getStatus().equals(SimulationStatus.PROCESSING)){
				throw new Exception("La simulaci�n se tiene que estar procesando");
			}
			simulation.setStatus(SimulationStatus.PAUSED);
			ArrayList<Long> singleSimulations;
			synchronized(parameterToSingleSimulations){
				singleSimulations = parameterToSingleSimulations.get(simulationId);
				parameterToSingleSimulations.remove(simulationId);
			}
			String username = simulation.getProject().getUser().getUsername();
			SingleSimulation singleSimulation;
			for(Long id : singleSimulations){
				try{
					singleSimulation = jpaController.getSingleSimulationController().getSingleSimulation(id); //Comprueba que la simulaci�n existe
					synchronized(singleToParameterSimulations){
						singleToParameterSimulations.remove(id);
					}
					synchronized(simulationRepetitions){
						simulationRepetitions.remove(id);
					}
					HashMap<Integer, String> individualExecutions;
					synchronized (simulationAddress) {
						individualExecutions = simulationAddress.get(id);
					}
					//Registro de las addresses de cada simulaci�n
					synchronized(simulationsRemovingAddress){
						if(simulationsRemovingAddress.containsKey(simulationId)){
							simulationsRemovingAddress.get(simulationId).addAll(individualExecutions.values());
						}
						else{
							simulationsRemovingAddress.put(simulationId, new ArrayList<>(individualExecutions.values()));
						}
					}
					boolean esNulo;
					HashSet<Integer> ids = null;
					synchronized (simulationCompleted) {
						esNulo = simulationCompleted.get(id)!=null;
						if(esNulo)
							ids = simulationCompleted.get(id);
					}
					if(esNulo){
						singleSimulation.getCompletedRepetitions().addAll(ids);
						jpaController.getSingleSimulationController().updateSingleSimulation(singleSimulation);
						System.err.println("Actualizado el valor de las simulaciones completadas de "+id);
					}
					synchronized(simulationAddress){
						simulationAddress.remove(id);
					}
					synchronized(userQuota){
						int quota = userQuota.get(username);
						userQuota.put(username, ((quota - individualExecutions.size())<0)?0:quota - individualExecutions.size());
					}
				}catch(Exception e){
					System.err.println("Se ha capturado una excepci�n "+e.getMessage());
					e.printStackTrace();
				}
			}
			singleSimulation = null;

			lock.lock();
			try{
				synchronized(parameterSweepsToRemove){
					parameterSweepsToRemove.add(simulationId);
				}
				synchronized(simulationsToRemove){
					simulationsToRemove.add(simulationId);
				}
				notEmpty.signal();
			} finally{
				lock.unlock();
			}
			jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
			break;
		}
		default:
			throw new Exception("Tipo de la simulaci�n no registrado");
		}
	}

	/**
	 * Detiene la ejecuci�n de una simulaci�n. 
	 * @param simulationId Identificador de la simulaci�n.
	 * @param type Tipo de simulaci�n
	 * @throws Exception
	 */
	public void stopSimulation(long simulationId, SimulationTypeEnum type) throws Exception{
		switch(type){
		case SINGLE_SIMULATION: {
			synchronized(simulationRepetitions){
				if(!simulationRepetitions.containsKey(simulationId)){
					throw new Exception("La tarea tiene estar registrada");
				}
			}
			SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
			if(!simulation.getStatus().equals(SimulationStatus.PROCESSING) && !simulation.getStatus().equals(SimulationStatus.PAUSED) && !simulation.getStatus().equals(SimulationStatus.REMOVING)){
				throw new Exception("No se pude eliminar. Estado inv�lido para iniciar la eliminaci�n");
			}
			if(!simulation.getStatus().equals(SimulationStatus.REMOVING))
				simulation.setStatus(SimulationStatus.CLEANING);
			synchronized(simulationRepetitions){
				simulationRepetitions.remove(simulationId);
			}
			HashMap<Integer, String> individualExecutions;
			synchronized (simulationAddress) {
				individualExecutions = simulationAddress.get(simulationId);
			}
			String username = simulation.getProject().getUser().getUsername();
			synchronized(userQuota){
				int quota = userQuota.get(username);
				userQuota.put(username, ((quota - individualExecutions.size())<0)?0:quota - individualExecutions.size());
			}
			//Poner las tareas para eliminar
			lock.lock();
			try{
				synchronized(simulationsRemovingAddress){
					simulationsRemovingAddress.put(simulationId, new ArrayList<>(individualExecutions.values()));
				}
				synchronized(simulationsToRemove){
					simulationsToRemove.add(simulationId);
				}
				synchronized(simulationsRemainingToRemove){
					simulationsRemainingToRemove.add(simulationId);
				}
				notEmpty.signal();
			}
			finally{
				lock.unlock();
			}

			ArrayList<String> addresses = new ArrayList<>();
			addresses.addAll(individualExecutions.values());
			//Aqu� iba la devoluci�n al usuario de la cuota
			//if(simulationCompleted.get(simulationId)!=null)
			//simulation.getCompletedRepetitions().addAll(simulationCompleted.get(simulationId));
			//Agregado con la idea de eliminar las simulaciones completadas
			synchronized(simulationCompleted){
				if(simulationCompleted.get(simulationId)!=null)
					simulationCompleted.remove(simulationId);
			}
			simulation.setCompletedRepetitions(new HashSet<>());
			//Fin
			synchronized(simulationAddress){
				simulationAddress.remove(simulationId);
			}
			jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
			//Limpiar el servicio de almacenamiento
			//dataController.cleanParameterSweepSimulation(simulationId);
			break;
		}
		case PARAMETER_SWEEP_SIMULATION:{
			if(!parameterToSingleSimulations.containsKey(simulationId)){
				throw new Exception("La tarea tiene que estar registrada");
			}
			ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
			if(!simulation.getStatus().equals(SimulationStatus.PROCESSING) && !simulation.getStatus().equals(SimulationStatus.PAUSED) && !simulation.getStatus().equals(SimulationStatus.REMOVING)){
				throw new Exception("Estado inv�lido para iniciar la detenci�n");
			}
			if(!simulation.getStatus().equals(SimulationStatus.REMOVING))
				simulation.setStatus(SimulationStatus.CLEANING);
			ArrayList<Long> singleSimulations;
			synchronized(parameterToSingleSimulations){
				singleSimulations = parameterToSingleSimulations.get(simulationId);
				parameterToSingleSimulations.remove(simulationId);
			}
			String username = simulation.getProject().getUser().getUsername();
			SingleSimulation singleSimulation;
			for(Long id : singleSimulations){
				singleSimulation = jpaController.getSingleSimulationController().getSingleSimulation(id);
				synchronized(singleToParameterSimulations){
					singleToParameterSimulations.remove(id);
				}
				synchronized(simulationRepetitions){
					simulationRepetitions.remove(id);
				}
				boolean contiene;
				HashMap<Integer, String> individualExecutions = null;
				synchronized (simulationAddress) {
					contiene = simulationAddress.get(id) != null;
					if(contiene)
						individualExecutions = simulationAddress.get(id);
				}
				if(contiene){
					synchronized(userQuota){
						int quota = userQuota.get(username);
						userQuota.put(username, ((quota - individualExecutions.size())<0)?0:quota - individualExecutions.size());
					}
					//Registro de las addresses de cada simulaci�n
					synchronized(simulationsRemovingAddress){
						if(simulationsRemovingAddress.containsKey(simulationId))
							simulationsRemovingAddress.get(simulationId).addAll(individualExecutions.values());
						else
							simulationsRemovingAddress.put(simulationId, new ArrayList<>(individualExecutions.values()));
					}
					synchronized(simulationCompleted){
						if(simulationCompleted.get(id)!=null){
							//simulation.getCompletedRepetitions().addAll(simulationCompleted.get(id));
							simulationCompleted.remove(id);
						}
					}
					synchronized(simulationAddress){
						simulationAddress.remove(id);
					}
				}else{
					//userQuota.put(username, userQuota.get(username) - singleSimulation.getNumRepetitions());
					//System.out.println("Se le restaura la cuota al usuario");
					synchronized(simulationCompleted){
						if(simulationCompleted.get(id)!=null)
							simulationCompleted.remove(id);
					}
					synchronized(simulationAddress){
						simulationAddress.remove(id);
					}
				}
				//userQuota.put(username, userQuota.get(username) - individualExecutions.size());
			}
			singleSimulation = null;
			lock.lock();
			try{
				synchronized(parameterSweepsToRemove){
					parameterSweepsToRemove.add(simulationId);
				}
				synchronized(simulationsToRemove){
					simulationsToRemove.add(simulationId);
				}
				synchronized(simulationsRemainingToRemove){
					simulationsRemainingToRemove.add(simulationId);
				}
				notEmpty.signal();
			} finally{
				lock.unlock();
			}
			simulation.setCompletedRepetitions(new HashSet<>());
			jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
			//Limpiar el servicio de almacenamiento
			//dataController.cleanParameterSweepSimulation(simulationId);
			break;
		}
		default:
			throw new Exception("Tipo de simulaci�n no registrado");
		}
	}

	/**
	 * M�todo para agregar la direcci�n de cada tarea de simulaci�n
	 * @param simulationId Identificador de la simulaci�n
	 * @param repetition Veces que se va a repetir
	 * @param taskAddress Direcci�n de la tarea en el Servicio de Simulaci�n
	 */
	public void addSimulationAddress(long simulationId, int repetition, String taskAddress){
		synchronized(simulationAddress){
			if(simulationAddress.containsKey(simulationId)){
				simulationAddress.get(simulationId).put(repetition, taskAddress);
			}
			else{
				HashMap<Integer, String> map = new HashMap<>();
				map.put(repetition, taskAddress);
				simulationAddress.put(simulationId, map);
			}
		}
	}

	/**
	 * M�todo para notificar la publicaci�n de la simulaci�n
	 * @param simulationId Identificador de la simulaci�n
	 * @param type Tipo de simulaci�n
	 */
	public void notifySimulationPublished(long simulationId, SimulationTypeEnum type) throws Exception { //Detectar si el proyecto se ha cancelado
		// TODO Auto-generated method stub
		boolean removing=false;
		switch(type){
		case SINGLE_SIMULATION:{
			SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
			if(simulation.getStatus().equals(SimulationStatus.REMOVING))
				removing=true;
			else if(simulation.getStatus().equals(SimulationStatus.PREPARING))
				simulation.setStatus(SimulationStatus.PROCESSING);
			jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
			break;
		}
		case PARAMETER_SWEEP_SIMULATION:{
			ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
			if(simulation.getStatus().equals(SimulationStatus.REMOVING))
				removing=true;
			else if(simulation.getStatus().equals(SimulationStatus.PREPARING))
				simulation.setStatus(SimulationStatus.PROCESSING);
			jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
			break;
		}
		default:
			break;
		}
		if(removing || dataController.isProjectRemoving(simulationId, type))
			stopSimulation(simulationId, type);
	}

	/**
	 * M�todo que es llamado cuando una simulaci�n se ha completado. 
	 * Si todas las simulaciones de trabajo (individual o de barrido) han finalizado crea una tarea para que se realice el informe
	 * @param simulationId Identificador de la simulaci�n. Si es barrido de par�metro, se referir� al tipo de simulaci�n individual dentro del barrido
	 * @param repetition Repetifici�n finalizada
	 * @throws Exception 
	 */
	public void notifySimulationCompleted(long simulationId, int repetition) throws Exception{
		synchronized (simulationRepetitions) {
			if(!simulationRepetitions.containsKey(simulationId))
				throw new Exception("La simulaci�n no existe en la lista de simulaciones");
		}
		synchronized (simulationAddress) {
			if(!simulationAddress.containsKey(simulationId) || !simulationAddress.get(simulationId).containsKey(repetition)){
				throw new Exception("La simulaci�n no pertenece a la lista de simulaciones");
			}
		}
		synchronized(simulationCompleted){
			if(simulationCompleted.containsKey(simulationId)){
				simulationCompleted.get(simulationId).add(repetition);
			}
			else{
				HashSet<Integer> completed = new HashSet<>();
				completed.add(repetition);
				simulationCompleted.put(simulationId, completed);
			}
		}

		//Poner las tareas para eliminar
		lock.lock();
		try{
			String id;
			synchronized (simulationAddress) {
				id = simulationAddress.get(simulationId).get(repetition);
			}
			synchronized(tasksToRemove){
				tasksToRemove.add(id);
			}
			notEmpty.signal();
		}
		finally{
			lock.unlock();
		}
		synchronized(simulationAddress){
			simulationAddress.get(simulationId).remove(repetition);
		}
		String username;
		synchronized (simulationUser) {
			username = simulationUser.get(simulationId);
		}
		synchronized(userQuota){
			userQuota.put(username, userQuota.get(username) - 1);
		}
		//userQuota.put(username, userQuota.get(username) - 1);
		boolean vacio;
		synchronized (simulationAddress) {
			vacio = simulationAddress.get(simulationId).isEmpty();
		}
		if(vacio){
			synchronized(simulationAddress){
				simulationAddress.remove(simulationId);
			}
			//Cambio de estado y env�o de informe, o notificaci�n a la simulaci�n de barrido
			//Retirar de la lista de usuarios?
			boolean contiene;
			synchronized (singleToParameterSimulations) {
				contiene = singleToParameterSimulations.containsKey(simulationId);
			}
			if(contiene) {//Barrido de par�metros
				//Agrego la tarea individual finalizada para conocer cu�ntas han acabado
				ArrayList<Long> a = null;
				long idSingleToParameterSimulation;
				synchronized (singleToParameterSimulations) {
					idSingleToParameterSimulation = singleToParameterSimulations.get(simulationId);
				}
				synchronized(sweepSinglesCompleted){
					if(!sweepSinglesCompleted.containsKey(idSingleToParameterSimulation))
						a = new ArrayList<>();
					else
						a = sweepSinglesCompleted.get(idSingleToParameterSimulation);
					a.add(simulationId);
					sweepSinglesCompleted.put(idSingleToParameterSimulation, a);
				}

				//Actualizo el estado de la simulaci�n finalizada para que no de error al borrar
				SingleSimulation s = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
				s.setStatus(SimulationStatus.FINISHED);
				jpaController.getSingleSimulationController().updateSingleSimulation(s);
				s = null;

				int tamaParameterToSingle, tamaSweepSinglesComplete;
				synchronized (parameterToSingleSimulations) {
					tamaParameterToSingle = parameterToSingleSimulations.get(idSingleToParameterSimulation).size();
				}
				synchronized (sweepSinglesCompleted) {
					tamaSweepSinglesComplete = sweepSinglesCompleted.get(idSingleToParameterSimulation).size();
				}

				if( tamaParameterToSingle == tamaSweepSinglesComplete){//Compruebo si todas las simulaciones del barrido han finalizado
					long idLocal = 0;
					ParameterSweepSimulation simulation;
					synchronized (sweepSinglesCompleted) {
						simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(idSingleToParameterSimulation);
					}

					//usersLogger.info("Username:" + username + " - ProjectId:" + simulation.getProject().getId() + " - SimulationId:" + simulation.getId() + " - SIMULACIONES DEL BARRIDO FINALIZADAS");
					//simulation.setStatus(SimulationStatus.REPORTING);
					//jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
					//A partir de aqu� falta publicar la tarea en report
					HashMap<String, String> parameters = putParameters(simulation.getParameters());
					ArrayList<OutputFileSummary> outputFiles = putOutFiles(simulation.getOutputFiles());
					String path = "users/" + username + "/" + simulation.getProject().getId() + "/" + simulation.getId();
					String listener = "v0.2/simulations/" + simulation.getId() + "/report";

					Task task = new Task(simulation.getPriority(),
							String.valueOf(simulation.getId()),
							username, 
							path + "/outputs/", 
							parameters, 
							outputFiles, 
							listener, 
							path + "/results/");
					//Agrego una equivalencia informe tipo de simulaci�n para trabajar con ella de manera adecuada una vez que se notifique la finalizaci�n del informe
					synchronized(reportsType){
						reportsType.put(simulation.getId(), SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
					}
					new PublisherReport(username, this, task, queueAddress, idLocal, SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
				}
			}
			else {//Simulaciones individuales
				SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
				//usersLogger.info("Username:" + username + " - ProjectId:" + simulation.getProject().getId() + " - SimulationId:" + simulation.getId() + " - SIMULACIONES INDIVIDUALES FINALIZADAS");
				if(simulation.getNumRepetitions()>1) { //Hay que hacer un informe
					//simulation.setStatus(SimulationStatus.REPORTING);
					//jpaController.getSingleSimulationController().updateSingleSimulation(simulation);

					//Creaci�n de la tarea del informe
					HashMap<String, String> parameters = putParameters(simulation.getParameters());

					ArrayList<OutputFileSummary> outputFiles = putOutFiles(simulation.getOutputFiles());

					String path = "users/" + username + "/" + simulation.getProject().getId() + "/" + simulationId;
					String listener = "v0.2/simulations/" + simulationId + "/report";
					Task task = new Task(simulation.getPriority(),
							String.valueOf(simulationId),
							username, 
							path + "/outputs/", 
							parameters, 
							outputFiles, 
							listener, 
							path + "/results/");
					synchronized(reportsType){
						reportsType.put(simulationId, SimulationTypeEnum.SINGLE_SIMULATION);
					}
					new PublisherReport(username, this, task, queueAddress, simulationId, SimulationTypeEnum.SINGLE_SIMULATION);
				}
				else {
					simulation.setStatus(SimulationStatus.FINISHED);
					jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
				}
			}
		}
	}

	/**
	 * M�todo para recuperar el JpaController. Se utiliza en la clase PublisherReport
	 * @return
	 */
	public JpaController getJpa(){
		return this.jpaController;
	}

	private HashMap<String, String> putParameters(List<Parameter> listParameter){
		HashMap<String, String> parameters = new HashMap<>();
		for(Parameter parameter : listParameter) {
			if(!parameter.isRandom())
				parameters.put(parameter.getParameterDescription().getName(), parameter.getSingleValue());
		}
		return parameters;
	}

	private ArrayList<OutputFileSummary> putOutFiles(List<OutputFile> listOutputFile){
		ArrayList<OutputFileSummary> outputFiles = new ArrayList<>();
		for(OutputFile outputFile : listOutputFile) {
			if(!outputFile.getOutputFileStructure().isMultiLine()) {
				outputFiles.add(new OutputFileSummary(outputFile.getName(), false, outputFile.getOutputFileStructure().getOutputVars()));
			}
		}
		return outputFiles;
	}


	public void notifySimulationError(long simulationId, int repetition) throws Exception{
		synchronized(simulationRepetitions){
			if(!simulationRepetitions.containsKey(simulationId))
				throw new Exception();
		}
		synchronized (simulationAddress) {
			if(!simulationAddress.containsKey(simulationId) || !simulationAddress.get(simulationId).containsKey(repetition)){
				throw new Exception();
			}
		}

		boolean contiene;
		synchronized (singleToParameterSimulations) {
			contiene = singleToParameterSimulations.containsKey(simulationId);
		}
		if(contiene){
			long idSingleToParameterSimulation;
			synchronized (singleToParameterSimulations) {
				idSingleToParameterSimulation = singleToParameterSimulations.get(simulationId);
			}
			ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(idSingleToParameterSimulation);
			simulation.setStatus(SimulationStatus.ERROR);
			jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);

			ArrayList<Long> simulationIds;
			synchronized(parameterToSingleSimulations){
				simulationIds = parameterToSingleSimulations.get(simulation.getId());
				parameterToSingleSimulations.remove(simulation.getId());
			}
			for(Long id : simulationIds){
				synchronized(singleToParameterSimulations){
					singleToParameterSimulations.remove(id);
				}
				ArrayList<String> addresses = new ArrayList<>();
				synchronized (simulationAddress) {
					addresses.addAll(simulationAddress.get(id).values());
				}
				String username;
				synchronized (simulationUser) {
					username = simulationUser.get(id);
				}
				synchronized(userQuota){
					userQuota.put(username, userQuota.get(username) - addresses.size());
				}
				synchronized(simulationAddress){
					simulationAddress.remove(id);
				}
				synchronized(simulationRepetitions){
					simulationRepetitions.remove(id);
				}
				synchronized(simulationCompleted){
					simulationCompleted.remove(id);
				}
				synchronized(simulationUser){
					simulationUser.remove(id);
				}

				//Registro de las addresses de cada simulaci�n
				synchronized(simulationsRemovingAddress){
					if(simulationsRemovingAddress.containsKey(simulationId))
						simulationsRemovingAddress.get(simulationId).addAll(addresses);
					else
						simulationsRemovingAddress.put(simulationId, addresses);
				}
			}

			lock.lock();
			try{
				synchronized(parameterSweepsToRemove){
					parameterSweepsToRemove.add(simulationId);
				}
				synchronized(simulationsToRemove){
					simulationsToRemove.add(simulationId);
				}
				synchronized(simulationsRemainingToRemove){
					simulationsRemainingToRemove.add(simulationId);
				}
				notEmpty.signal();
			} finally{
				lock.unlock();
			}

			//Terminar la limpieza: eliminar simulaciones individuales y retirar de almacenamiento
		}
		else{
			SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
			simulation.setStatus(SimulationStatus.ERROR);
			simulation.getCompletedRepetitions().clear();
			jpaController.getSingleSimulationController().updateSingleSimulation(simulation);

			ArrayList<String> addresses = new ArrayList<>();
			synchronized (simulationAddress) {
				addresses.addAll(simulationAddress.get(simulationId).values());
			}
			String username;
			synchronized (simulationUser) {
				username = simulationUser.get(simulationId);
			}
			synchronized(userQuota){
				userQuota.put(username, userQuota.get(username) - addresses.size());
			}
			synchronized(simulationAddress){
				simulationAddress.remove(simulationId);
			}
			synchronized(simulationRepetitions){
				simulationRepetitions.remove(simulationId);
			}
			synchronized(simulationCompleted){
				simulationCompleted.remove(simulationId);
			}
			synchronized(simulationUser){
				simulationUser.remove(simulationId);
			}

			//Terminar la limpieza: retirar de almacenamiento

			//Poner las tareas para eliminar
			lock.lock();
			try{
				synchronized(simulationsRemovingAddress){
					simulationsRemovingAddress.put(simulationId, addresses);
				}
				synchronized(simulationsToRemove){
					simulationsToRemove.add(simulationId);
				}
				synchronized(simulationsRemainingToRemove){
					simulationsRemainingToRemove.add(simulationId);
				}
				notEmpty.signal();
			}
			finally{
				lock.unlock();
			}
		}
	}

	public void checkToRemove() throws InterruptedException{
		lock.lock();

		try{
			while(simulationsToRemove.isEmpty() && tasksToRemove.isEmpty()) {
				notEmpty.await();
			}
		}
		finally{
			lock.unlock();
		}
	}

	public long getSimulationToRemove(){
		synchronized(simulationsToRemove){
			if(simulationsToRemove.isEmpty())
				return -1;
			return simulationsToRemove.poll();
		}
	}

	public String getTaskToRemove(){
		synchronized(tasksToRemove) {
			return tasksToRemove.poll();
		}
	}

	public ArrayList<String> getTasksUrisToRemove(long simulationId){
		synchronized (simulationsRemovingAddress) {
			return simulationsRemovingAddress.get(simulationId);
		}
	}

	public void notifySimulationRemoved(long simulationId){ //Me queda comrpobar que no es por eliminaci�n del proyecto... TT_TT
		synchronized(simulationsRemovingAddress){
			simulationsRemovingAddress.remove(simulationId);
		}
		try{
			boolean contiene;
			synchronized (parameterSweepsToRemove) {
				contiene = parameterSweepsToRemove.contains(simulationId);
			}
			if(contiene){ //Barrido de par�metros
				synchronized(parameterSweepsToRemove){
					parameterSweepsToRemove.remove(simulationId);
				}
				synchronized(simulationsRemainingToRemove){
					if(simulationsRemainingToRemove.contains(simulationId))
						simulationsRemainingToRemove.remove(simulationId);
				}
				if(!dataController.isProjectRemoving(simulationId, SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION)){
					ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
					if(simulation.getStatus().equals(SimulationStatus.CLEANING)){
						if(dataController.isSimulationAlreadyCleaned(simulationId)){//Puede que me toque cambiarlo de posici�n :S
							simulation.setStatus(SimulationStatus.WAITING);
							jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
						}
					}
					else if(simulation.getStatus().equals(SimulationStatus.REMOVING)){
						if(dataController.isSimulationAlreadyCleaned(simulationId))//Puede que me toque cambiarlo de posici�n :S
							jpaController.getParameterSweepSimulationController().deleteParameterSweepSimulation(simulation);
					}
				}
				else{
					dataController.notifySimulation4ProjectRemoved(simulationId, SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
				}
			}
			else{ //Simulaci�n individual
				synchronized(simulationsRemainingToRemove){
					if(simulationsRemainingToRemove.contains(simulationId))
						simulationsRemainingToRemove.remove(simulationId);
				}
				//if(!dataController.isProjectRemoving(simulationId, SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION)){
				if(!dataController.isProjectRemoving(simulationId, SimulationTypeEnum.SINGLE_SIMULATION)){
					SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
					//Comprobar si el proyecto se est� borrando
					if(simulation.getStatus().equals(SimulationStatus.CLEANING)){
						if(dataController.isSimulationAlreadyCleaned(simulationId)){//Comprobar si el proyecto se est� borrando
							simulation.setStatus(SimulationStatus.WAITING);
							jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
						}
					}
					else if(simulation.getStatus().equals(SimulationStatus.REMOVING)){
						if(dataController.isSimulationAlreadyCleaned(simulationId))//Puede que me toque cambiarlo de posici�n :S
							jpaController.getSingleSimulationController().deleteSingleSimulation(simulation);
					}

				}
				else{
					dataController.notifySimulation4ProjectRemoved(simulationId, SimulationTypeEnum.SINGLE_SIMULATION);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public boolean isAlreadyRemoved(long simulationId){
		synchronized(simulationsRemainingToRemove){
			return !simulationsRemainingToRemove.contains(simulationId);
		}
	}

	public int getUserCurrentSimulations(String username){
		synchronized (userQuota) {
			if(!userQuota.containsKey(username))
				return -1;
			return userQuota.get(username);
		}
	}

	public int getTotalSimulations(long simulationId, SimulationTypeEnum type){
		switch(type){
		case SINGLE_SIMULATION:
			synchronized(simulationRepetitions){
				if(!simulationRepetitions.containsKey(simulationId))
					return 0;
				return simulationRepetitions.get(simulationId);
			}
		case PARAMETER_SWEEP_SIMULATION:
			synchronized(parameterToSingleSimulations) {
				if(!parameterToSingleSimulations.containsKey(simulationId))
					return 0;
				int total = 0;
				for(long id : parameterToSingleSimulations.get(simulationId))
					total += simulationRepetitions.get(id);
				return total;
			}
		default:
			return 0;
		}
	}

	public int getCompletedSimulations(long simulationId, SimulationTypeEnum type){
		switch(type){
		case SINGLE_SIMULATION:
			synchronized(simulationCompleted) {
				if(!simulationCompleted.containsKey(simulationId))
					return 0;
				return simulationCompleted.get(simulationId).size();
			}
		case PARAMETER_SWEEP_SIMULATION:
			synchronized(parameterToSingleSimulations) {
				if(!parameterToSingleSimulations.containsKey(simulationId))
					return 0;
				int total = 0;
				synchronized (simulationCompleted) {
					for(long id : parameterToSingleSimulations.get(simulationId)) 
						if(simulationCompleted.containsKey(id))
							total += simulationCompleted.get(id).size();
				}
				return total;
			}
		default:
			return 0;
		}
	}

	/**
	 * M�todo para actualizar la prioridad de un conjunto de simulaciones que ya se est�n 
	 * en colas
	 * @param username Nombre del usuario al que pertenece la simulaci�n
	 * @param idJob Identificado de la simulaci�n en la cola del usuario
	 * @param priority Nueva prioridad para la simulaci�n
	 */
	public void updatePriorityJob(String username, String idJob, int priority){
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("username", username);
		jsonObject.put("idJob", idJob);
		jsonObject.put("priority", priority);
		new UpdateJob().updateJob(queueAddress, jsonObject);
		//usersLogger.info("Username:" + username + " - SimulationId:" + idJob + " - Priority:" + priority + " - ACTUALIZACI�N PRIORIDAD");
	}

	/**
	 * M�todo utilizado para cambiar el estado de una simulaci�n en la que se estuviera haciendo el informe
	 * @param simulationId Identificador de la simulaci�n
	 * @param simulationStatus Estado que va a pasar a tener la simulaci�n
	 */
	public void notifyReport(Long simulationId, SimulationStatus simulationStatus) {
		try {
			SimulationTypeEnum simulationTypeEnum;
			synchronized (reportsType) {
				simulationTypeEnum = reportsType.get(simulationId);
			}
	
			switch (simulationTypeEnum) {
			case SINGLE_SIMULATION:
				SingleSimulation single = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
				single.setStatus(simulationStatus);
				if(simulationStatus == SimulationStatus.FINISHED)
					single.setFinishDate(new Date());
				jpaController.getSingleSimulationController().updateSingleSimulation(single);
				//usersLogger.info("Username:" + single.getProject().getUser().getUsername() + " - ProjectId:" + single.getProject().getId() + " - SimulationId:" + single.getId() + " - INFORME INDIVIDUAL FINALIZADO");
				break;
			case PARAMETER_SWEEP_SIMULATION:
				ParameterSweepSimulation sweep = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
				sweep.setStatus(simulationStatus);
				if(simulationStatus == SimulationStatus.FINISHED)
					sweep.setFinishDate(new Date());
				jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(sweep);
				//usersLogger.info("Username:" + sweep.getProject().getUser().getUsername() + " - ProjectId:" + sweep.getProject().getId() + " - SimulationId:" + sweep.getId() + " - INFORME DE BARRIDO FINALIZADO");
				break;
			default:
				break;
			}
			synchronized(reportsType){
				reportsType.remove(simulationId);
			}
		} catch (Exception e) {//Saltar� la excecpci�n cuando se intente recuperar una simulaci�n no registrada en el sistema
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
		}
	}
}
