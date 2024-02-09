package dnse3.orchestration.controller;

import dnse3.common.CloudManager;

public class FileRemoval implements Runnable {

	private DataController dataController;
	private long simulationId;
	private int projectId;
	
	public FileRemoval(DataController dataController) {
		this.dataController = dataController;
	}
	
	@Override
	public void run() {
		while(true){
			try{
				simulationId=-1;
				projectId=-1;
				while(true){
					System.out.println("CHECK TO CLEAN!!!");
					dataController.checkToClean();
					simulationId=dataController.getSimulationToClean();
					System.out.println("simulation to clean: "+simulationId);
					if(simulationId!=-1)
						break;
					projectId=dataController.getProjectToClean();
					System.out.println("project to clean: "+projectId);
					if(projectId!=-1)
						break;
				}
				
				if(simulationId!=-1)
					removeSimulation();
				else if(projectId!=-1)
					removeProject();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}

	}
	
	private void removeSimulation(){
		int projectId = dataController.getSimulationToCleanProject(simulationId);
		if(projectId!=-1){
			String path = "users/"+dataController.getProjectToCleanUsername(projectId)+"/"+projectId+"/"+simulationId;	
			CloudManager.deletePath(path);
			dataController.notifySimulationCleaned(simulationId); //Aquí siempre lo tengo que hacer
		}
	}
	
	private void removeProject(){
		String path = "users/"+dataController.getProjectToCleanUsername(projectId)+"/"+projectId;
		CloudManager.deletePath(path);
		dataController.notifyProjectCleaned(projectId);
	}
}
