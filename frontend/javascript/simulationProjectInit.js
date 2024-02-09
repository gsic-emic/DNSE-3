//The time in milliseconds between two calls to refresh the screen
const VERY_LOW_REFRESH_INTERVAL = 300000;
const LOW_REFRESH_INTERVAL = 60000;
const MEDIUM_REFRESH_INTERVAL = 30000;
const HIGH_REFRESH_INTERVAL = 15000;
const VERY_HIGH_REFRESH_INTERVAL = 5000;

var G_VARS = new Array();

$(document).ready(function(){
    
    //initialize the global vars
    var projectId = getLocationParameter("projectId");
    G_VARS["projectId"] = projectId;
    
    //initialize the painters
    SingleSimulationsPainter.init();
    //SingleSimulationsPainter.paint();
    //ParameterSweepSimulationsPainter.paint();
    
    //initialize the tooltips
    $('[data-toggle="tooltip"]').tooltip();
    
    //initialize the controllers
    SingleSimulationController.init();
    SingleSimulationController.addPainter(CurrentSimulationsPainter);
    SingleSimulationController.addPainter(SingleSimulationsPainter);
    
    ParameterSweepSimulationController.init();
    ParameterSweepSimulationController.addPainter(CurrentSimulationsPainter);
    ParameterSweepSimulationController.addPainter(ParameterSweepSimulationsPainter);
    
    //initialize the modals      
    EditProjectModal.init();
    EditProjectModal.addPainter(SimulationPainter);
        
    DeleteProjectModal.init();
    DeleteProjectModal.addPainter(ProjectsPainter);
    
    NewSimulationModal.init();
    NewSimulationModal.addPainter(SingleSimulationsPainter);
    NewSimulationModal.addPainter(ParameterSweepSimulationsPainter);
    
    InfoModal.init();
    ErrorModal.init();
    SessionExpiredModal.init();
    
    ViewSingleSimulationModal.init();
    ViewParameterSweepSimulationModal.init();
    
    NewSingleSimulation.init();
    NewParameterSweepSimulation.init();
    NewSimulationFileGathering.init();
    
    EditSimulationModal.init();
    EditSimulationModal.addPainter(SingleSimulationsPainter);
    EditSimulationModal.addPainter(ParameterSweepSimulationsPainter);
    
    EditSingleSimulation.init();
    EditParameterSweepSimulation.init();
    EditSimulationFileGathering.init();
    
    DeleteSimulationModal.init();
    DeleteSimulationModal.addPainter(SingleSimulationsPainter);
    DeleteSimulationModal.addPainter(ParameterSweepSimulationsPainter);
    
    setExpandableSections();
    
    G_VARS["refresh_painters"] = true;
    /**
     * Set an interval to refresh the screen
     */
    setInterval(function(){
        if (G_VARS["refresh_painters"]){
            CurrentSimulationsPainter.paint();
            SimulationPainter.paint();
            SingleSimulationsPainter.paint();
            ParameterSweepSimulationsPainter.paint();
        }
    },  HIGH_REFRESH_INTERVAL);
    
});

