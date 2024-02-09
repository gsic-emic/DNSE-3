import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dnse3.common.Task;

public class Report extends Task {
	
	private String packageID;
	private String simDescID;
	private String userID;
	private ArrayList<OperationReport> operations;
	private boolean saveTraceFile;
	private String inputfile;
	
	public Report(String id, String input, String output, String packageID, String simulationID,HashMap<String, String> parameters, ArrayList<OperationReport> operationList, boolean saveTraceFile) {
		super(id, null, null, parameters, output);
		this.inputfile=input;
		this.packageID=packageID;
		this.simDescID=simulationID;
		this.operations=operationList;
		this.saveTraceFile=saveTraceFile;
		
	}
	
	public Report(String id, String input, String output, String packageID, String simulationID,HashMap<String, String> parameters, ArrayList<OperationReport> operationList) {
		super(id, null, null, parameters, output);
		this.inputfile=input;
		this.packageID=packageID;
		this.simDescID=simulationID;
		this.operations=operationList;
		this.saveTraceFile=false;
		
	}
	
	public Report(String id, String input, String output, String packageID, String simulationID,HashMap<String, String> parameters, boolean saveTraceFile) {
		super(id, null, null, parameters, output);
		this.inputfile=input;
		this.packageID=packageID;
		this.simDescID=simulationID;
		this.operations=new ArrayList<OperationReport>();
		this.saveTraceFile=saveTraceFile;
		
	}

	@Override
	public void changeETag() {
		// TODO Auto-generated method stub
		//Not used

	}

	@Override
	public Map<String, String> getAditionalFields() { //No devuelvo de momento los parámetros adicionales, al tener que mirar lo de operations
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getPackageID(){
		return packageID;
	}
	
	public String getSimDescID(){
		return simDescID;
	}
	
	public String getUserID(){
		return userID;
	}

	public List<OperationReport> getOperations(){
		return operations;
	}
	
	public boolean getSaveTraceFile(){
		return saveTraceFile;
	}
	
	public String getInputFile(){
		return inputfile;
	}
}
