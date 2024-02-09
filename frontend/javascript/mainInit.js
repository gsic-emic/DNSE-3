//The time in milliseconds between two calls to refresh the screen
const VERY_LOW_REFRESH_INTERVAL = 300000;
const LOW_REFRESH_INTERVAL = 60000;
const MEDIUM_REFRESH_INTERVAL = 30000;
const HIGH_REFRESH_INTERVAL = 15000;
const VERY_HIGH_REFRESH_INTERVAL = 5000;

var G_VARS = new Array();

$(document).ready(function(){
    //initialize the modals       
    NewProjectModal.init();
    NewProjectModal.addPainter(ProjectsPainter);
    
    EditProjectModal.init();
    EditProjectModal.addPainter(ProjectsPainter);
    
    ViewProjectModal.init();
    
    DeleteProjectModal.init();
    DeleteProjectModal.addPainter(ProjectsPainter);
    
    NewSimulationModal.init();
    NewSimulationModal.addPainter(ProjectsPainter);
    
    InfoModal.init();
    ErrorModal.init();
    SessionExpiredModal.init();
    
    NewSingleSimulation.init();
    NewParameterSweepSimulation.init();
    NewSimulationFileGathering.init();

    //init the painters
    ProjectsPainter.init();
    
    G_VARS["refresh_painters"] = true;
     /**
     * Set an interval to refresh the screen
     */
    setInterval(function(){
        if (G_VARS["refresh_painters"]){
            CurrentSimulationsPainter.paint();
            ProjectsPainter.paint();
        }
    },  VERY_HIGH_REFRESH_INTERVAL);
});

