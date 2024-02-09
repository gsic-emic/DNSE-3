import java.math.BigInteger;
import java.rmi.server.Operation;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Tag;

import dnse3.common.CloudManager;
import dnse3.common.Simulation;
import dnse3.common.Task;
import dnse3.common.TaskException;
import dnse3.common.TaskExceptionEnum;
import dnse3.common.TaskStatusEnum;
import dnse3.queue.data.TaskQueue;

public class ReportQueue implements TaskQueue {
	
	private LinkedHashMap<String, Report> reportQueue;
	private SecureRandom idGenerator = new SecureRandom();
	private CloudManager cm; //Necesario para pasar al hilo
	private ReportGenerator rg; //Este hilo se debe de crear cada vez que finaliza su tarea
	
	public ReportQueue(CloudManager cm) {
		this.reportQueue=new LinkedHashMap<String,Report>();
		this.cm=cm;
		this.rg= new ReportGenerator(this, cm);
	}

	@Override
	public Task getTask(String idTask) throws TaskException {
		if(!reportQueue.containsKey(idTask))
			throw new TaskException(TaskExceptionEnum.NOT_FOUND);
		return reportQueue.get(idTask);
	}

	@Override
	public Task getNextTask() { // Idem a SimulationQueue, lo único que es improbable que se envíe otra vez Progressing
		for(Report r : reportQueue.values()){
			if(r.getStatus().equals(TaskStatusEnum.WAITING))
				return r;
		}
		return null;
	}
	
	public Report getNextReport() { // Idem a SimulationQueue, lo único que es improbable que se envíe otra vez Progressing
		for(Report r : reportQueue.values()){
			if(r.getStatus().equals(TaskStatusEnum.WAITING))
				return r;
		}
		return null;
	}

	@Override
	public Collection<Task> getTasks() {
		return new ArrayList<Task>(reportQueue.values());
	}

	@Override
	public String addNewTask(JSONObject document) throws TaskException { //Empieza la ejecución de los informes. En caso de que ya esté ejecutándose, no hace nada
		//El worker ya se encarga de realizar los informes y, a continuación, recoger el siguiente informe necesario. Necesita referencia a CloudManager y a la propia Cola que lo contiene
		String id = null;
		if(document.has("input")&&document.has("outputPattern")&&document.has("parameters")&&document.has("packageID")&&document.has("simulationID")){
			try{
				String input = document.getString("input");
				String output = document.getString("outputPattern");
				String packageID = document.getString("packageID");
				String simulationID = document.getString("simulationID"); //Utilizado para identificar de donde recoger los datos
				
				
				JSONArray paramArray = document.getJSONArray("parameters");
				HashMap<String,String> parameters = new HashMap<String,String>();
				for(int i=0; i<paramArray.length(); i++){
					JSONObject paramObj = paramArray.getJSONObject(i);
					if(!paramObj.has("name")||!paramObj.has("value")){
						System.out.println("Mal parámetro");
						throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
					}
					String paramName = paramObj.getString("name");
					if(parameters.containsKey(paramName)){
						System.out.println("Parámetro repetido");
						throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
					}
					switch(paramName){
					case "variableSpan": //Especifica la asignación de las variables a usar en Octave
					case "traceSet": //Especifica el formato del fichero de traza a generar. Debería de ser sólo válido cuando se use output
					case "paramOrder": //Indica el orden de los parámetros en el nombre del fichero de traza
					case "numSimulations":
						parameters.put(paramName, paramObj.getString("value"));
						break;
					default:
						System.out.println("Parámetro no válido");
						throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
					}
				}
				if(document.has("operations")){ //Puede tener operaciones
					ArrayList<OperationReport> operationList = new ArrayList<OperationReport>();
					JSONArray operationArray = document.getJSONArray("operations");
					for(int i=0; i< operationArray.length(); i++){
						JSONObject operationObj = operationArray.getJSONObject(i);
						if(!operationObj.has("operation")||!operationObj.has("inputData")){
							System.out.println("Mal operación");
							throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
						}
						if(operationObj.has("preprocessing"))
							operationList.add(new OperationReport(operationObj.getString("operation"),operationObj.getString("inputData"), operationObj.getString("preprocessing")));
						else
							operationList.add(new OperationReport(operationObj.getString("operation"),operationObj.getString("inputData")));
						
						do {
							id = new BigInteger(65, idGenerator).toString(32); //generate a new random id
						} while (reportQueue.containsKey(id));
						
						Report newReport;
						
						if(document.has("saveTraceFile")){
							newReport = new Report(id, input, output, packageID, simulationID, parameters, operationList, document.getBoolean("saveTraceFile"));
						}
						else
							newReport = new Report(id, input, output, packageID, simulationID, parameters, operationList);
						reportQueue.put(id, newReport);
						
						if(!rg.isAlive()){ //Si no hay ningún proceso en ejecución, se inicia
							rg = new ReportGenerator(this, cm);
							rg.start();
						}
					}
				}
				else if(document.has("saveTraceFile")&&document.getBoolean("saveTraceFile")){ //Puede que sólo se quiera generar un fichero de traza, requiriendo que se indique el formato del fichero de traza
					do {
						id = new BigInteger(65, idGenerator).toString(32); //generate a new random id
						System.out.println("Hola, esto está bien");
					} while (reportQueue.containsKey(id));
					
					Report newReport = new Report(id, input, output, packageID, simulationID, parameters, true);
					reportQueue.put(id, newReport);
					
					if(!rg.isAlive()){ //Si no hay ningún proceso en ejecución, se inicia
						rg = new ReportGenerator(this, cm);
						rg.start();
					}
				}
				else{
					System.out.println("Mal informe");
					throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
				}
			} catch (JSONException e){
				System.out.println("Error JSON");
				throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
			}
		}
		else{
			System.out.println("Faltan parámetros petición");
			throw new TaskException(TaskExceptionEnum.BAD_REQUEST);
		}
			
		
		return id;
	}

	@Override
	public void deleteTask(String idTask) throws TaskException {
		if(!reportQueue.containsKey(idTask))
			throw new TaskException(TaskExceptionEnum.NOT_FOUND);
		if(reportQueue.get(idTask).getStatus().equals(TaskStatusEnum.PROCESSING))
			rg.stop(); //Detengo su ejecución, es decir, pido que aborte la recogida de datos, borre todos los ficheros y recoja un nuevo informe
		reportQueue.remove(idTask);
	}

	@Override
	public Tag udateTask(String idTask, JSONArray document, String string, String string2) throws TaskException { //No se utilizará, ya que modifica automáticamente, al ejecutarse en el mismo servidor
		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public Tag getETag(String idTask) throws TaskException { //Not used, no se permite modificar el estado de los informes
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSize() { 
		return reportQueue.size();
	}

}
