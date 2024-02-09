package dnse3.orchestration.jpa.model.simulation;

import java.lang.reflect.Type;

import org.restlet.data.Reference;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dnse3.orchestration.controller.DataController;

public class ParameterSweepSimulationSerializer implements JsonSerializer<ParameterSweepSimulation> {
	
	private boolean resume = false;
	private Reference locationRef;
	private DataController dataController;
	
	public ParameterSweepSimulationSerializer(Reference locationRef, DataController dataController) {
		this.locationRef=locationRef;
		// TODO Auto-generated constructor stub
		this.dataController = dataController;
	}
	
	public ParameterSweepSimulationSerializer(Reference locationRef, DataController dataController, boolean resume){
		this.locationRef=locationRef;
		this.resume=resume;
		this.dataController = dataController;
	}

	@Override
	public JsonElement serialize(ParameterSweepSimulation simulation, Type type, JsonSerializationContext context) {
		// TODO Auto-generated method stub
		final JsonObject simulationObj = new JsonObject();
		simulationObj.addProperty("simulationId", simulation.getId());
		simulationObj.addProperty("name", simulation.getName());
		simulationObj.addProperty("priority", simulation.getPriority());
		simulationObj.addProperty("creationDate", simulation.getCreationDate().toInstant().toString());
		simulationObj.addProperty("updateDate", simulation.getUpdateDate().toInstant().toString());
		simulationObj.addProperty("status", simulation.getStatus().toString());
		simulationObj.addProperty("numRepetitions", simulation.getNumRepetitions());
		
		switch(simulation.getStatus()){
		case PROCESSING:
		case PAUSED:
			simulationObj.addProperty("completedSimulations", dataController.getCompletedSimulations(simulation.getId(),SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION));
			simulationObj.addProperty("totalSimulations", dataController.getTotalSimulations(simulation.getId(),SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION));
			break;
		case FINISHED:
			//simulationObj.addProperty("startDate", simulation.getStartDate().toString());
			//simulationObj.addProperty("finishTime", simulation.getFinishDate().toString());
			simulationObj.addProperty("totalTime", String.valueOf(simulation.getTotalTime()));
			break;
		default:
			break;
		}
		
		if(resume)
			simulationObj.addProperty("uri", locationRef.toString()+simulation.getId()+"/");
		else{
			simulationObj.addProperty("parametersUri", locationRef.toString()+"parameters/");
			simulationObj.addProperty("outputFilesUri", locationRef.toString()+"outputfiles/");
			
			final JsonObject projectObj = new JsonObject();
			projectObj.addProperty("projectId", simulation.getProject().getId());
			projectObj.addProperty("uri", locationRef.getParentRef().getParentRef().toString());
			simulationObj.add("project", projectObj);
			//Pendiente resultados
		}
		
		return simulationObj;
	}
}
