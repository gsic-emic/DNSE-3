/**
 * Manager for the modal dialog that deletes a simulation
 */
var DeleteSimulationModal = {
    
    modalId: "delete-simulation-modal",
    cancelButtonId: "delete-simulation-modal-cancel",
    acceptButtonId: "delete-simulation-modal-accept",
    simulationId: null,
    simulationType: null,
    painters: null,
    
    init: function(){
        this.painters = new Array();
        //set the events for the buttons
        $("#" + this.acceptButtonId).click(function(){
            DeleteSimulationModal.acceptDeleteSimulation();
        });
        $("#" + this.cancelButtonId).click(function(){
            DeleteSimulationModal.cancelDeleteSimulation();
        });
    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    },
    
    setModalFields: function(){
        //there are no fields to set
        //remove the error messages
        $("#alert-danger-delete-simulation-modal").addClass("hidden");
        this.showModal();
    },
    
    checkModalFields: function(){
        //there are no fields to check
        if (this.simulationType == "single"){
            this.deleteSingleSimulation();
        }else if (this.simulationType == "sweep"){
            this.deleteParameterSweepSimulation();
        }
    },
    
    deleteSimulation: function(simulationId, simulationType){
        this.simulationId = simulationId;
        this.simulationType = simulationType;
        this.setModalFields();
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
    
    acceptDeleteSimulation: function(){
        this.checkModalFields();
    },
    
    deleteSingleSimulation: function(){
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"]  + "/singlesimulations/" + this.simulationId,
            method: 'DELETE',
            success: function(data){
                DeleteSimulationModal.simulationId = null;
                DeleteSimulationModal.simulationType = null;
                DeleteSimulationModal.hideModal();
                DeleteSimulationModal.notifyPainters();
            },
            error : function(xhr, status) {
                $("#alert-danger-delete-simulation-modal").removeClass("hidden");
                $("#alert-danger-delete-simulation-modal").html("Se ha producido un error al tratar de eliminar la simulación.");
                //$("#alert-danger-delete-simulation-modal").html(xhr.responseText);
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    },
    
    deleteParameterSweepSimulation: function(){
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"]  + "/parametersweepsimulations/" + this.simulationId,
            method: 'DELETE',
            success: function(data){
                DeleteSimulationModal.simulationId = null;
                DeleteSimulationModal.simulationType = null;
                DeleteSimulationModal.hideModal();
                DeleteSimulationModal.notifyPainters();
            },
            error : function(xhr, status) {
                $("#alert-danger-delete-simulation-modal").removeClass("hidden");
                $("#alert-danger-delete-simulation-modal").html("Se ha producido un error al tratar de eliminar la simulación.");
                //$("#alert-danger-delete-simulation-modal").html(xhr.responseText);
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    },
    
    cancelDeleteSimulation: function(){
        this.hideModal();
    }
    
};
