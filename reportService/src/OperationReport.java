
public class OperationReport {
	
	private String operation;
	private String inputData;
	private String preprocessing;

	public OperationReport(String operation, String inputData, String preprocessing) {
		this.operation=operation;
		this.inputData=inputData;
		this.preprocessing=preprocessing;
	}
	
	public OperationReport(String operation, String inputData){
		this.operation=operation;
		this.inputData=inputData;
		this.preprocessing="";
	}
	
	public String getOperation(){
		return operation;
	}
	
	public String getPrepocessing(){
		return preprocessing;
	}
	
	public String getInputData(){
		return inputData;
	}
	
	public boolean hasPreprocessing(){
		return !preprocessing.isEmpty();
	}

}
