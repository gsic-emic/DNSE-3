package dnse3.common.tasks;

import java.util.ArrayList;
import java.util.List;

public class OutputFileSummary {

	private String name;
	private boolean multiLine;
	private ArrayList<String> outputVariables;
	
	public OutputFileSummary(String name, boolean multiLine){
		this.name=name;
		this.multiLine=multiLine;
		this.outputVariables = new ArrayList<>();
	}
	
	public OutputFileSummary(String name, boolean multiLine, List<String> outputVariables){
		this.name=name;
		this.multiLine=multiLine;
		this.outputVariables = new ArrayList<>(outputVariables);
	}

	public String getName() {
		return name;
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public ArrayList<String> getOutputVariables() {
		return outputVariables;
	}
}
