/* 
 * Controller for the different status of a single simulation
 */
var SingleSimulationController = {
    
    painters: null,
    
    init: function(){
        this.painters = new Array();
    },
    
    addPainter: function(painterObj){
        this.painters.push(painterObj);
    },
    
    deletePainter: function(painterObj){
        var index = this.painters.indexOf(painterObj);
        if (index > -1){
            this.painters.splice(index, 1);
        }
    },
    
    notifyPainters: function(){
        for (var i = 0; i < this.painters.length; i++){
            var painter = this.painters[i];
            painter.paint();
        }
    },
    
    startSimulation: function(simulationId){
        this.postOperationRequest(simulationId, SimulationOperation.START);
    },
    
    pauseSimulation: function(simulationId){
        this.postOperationRequest(simulationId, SimulationOperation.PAUSE);
    },
    
    stopSimulation: function(simulationId){
        this.postOperationRequest(simulationId, SimulationOperation.STOP);
    },

    /**
     * POST request that starts or stops a simulation depending on its current
     * status
     */
    postOperationRequest: function(simulationId, operation){
        var data = {
            operation: operation
        };
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"]  + "/singlesimulations/" + simulationId,
            method: "POST",
            data: data,
            //contentType: "application/json",
            success: function(data){
                SingleSimulationController.notifyPainters();
                switch (operation){
                    case SimulationOperation.START: InfoModal.infoMessage(" Ejecutar simulación", "La simulación se ha iniciado correctamente.");
                                                    break;
                    case SimulationOperation.PAUSE: InfoModal.infoMessage(" Pausar simulación", "La simulación ha sido pausada correctamente.");
                                                    break;
                    case SimulationOperation.STOP: InfoModal.infoMessage(" Detener simulación", "La simulación ha sido detenida correctamente.");
                                                    break;
                                                    
                }
            },
            error : function(xhr, status) {
                //show the error
                //manageRequestError(xhr, status);
                //alert("Se ha producido un error al cambiar el estado de la simulación");
                switch (operation){
                    case SimulationOperation.START:
                        ErrorModal.errorMessage(" Cuota insuficiente", "El usuario no dispone de suficiente cuota para iniciar la simulación.");
                        break;
                    default:
                        manageRequestError(xhr, status);
                }
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    },
    
    getResultsRequest: function(simulationId){
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"]  + "/singlesimulations/" + simulationId + "/results",
            method: "GET",
            headers: { 
                Accept : "application/zip"
            },
            success: function(data){

            },
            error : function(xhr, status) {
                //show the error
                manageRequestError(xhr, status);
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    }
}

