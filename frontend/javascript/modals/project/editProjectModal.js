/**
 * Manager for the modal dialog that edits a project
 */
var EditProjectModal = {
    
    modalId: "edit_project_modal",
    cancelButtonId: "edit_project_modal_cancel",
    acceptButtonId: "edit_project_modal_accept",
    projectId: null,
    painters: null,
    
    init: function(){
        //set the events for the buttons
        $("#" + this.acceptButtonId).click(function(){
            EditProjectModal.acceptEditProject();
        });
        $("#" + this.cancelButtonId).click(function(){
            EditProjectModal.cancelEditProject();
        });
        this.painters = new Array();
    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    },
    
    setModalFields: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + EditProjectModal.projectId + "/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                //set the values for the fields
                $("#projectName").val(data.name);
                $("#projectDescription").val(data.description);
                //remove the error messages
                $("#form-group-projectName").removeClass("has-error");
                $("#alert-danger-projectName").addClass("hidden");
                EditProjectModal.showModal();
                
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                EditSimulationModal.hideModal();
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Modificación del proyecto", "Error en setModalFields. Código de error " + xhr.status);
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    },
    
    checkModalFields: function(){
        var errors = false;
        var name = $("#projectName").val();
        if (name == ""){
            $("#form-group-projectName").addClass("has-error");
            $("#alert-danger-projectName").removeClass("hidden");
            $("#alert-danger-projectName").html("Comprueba los campos marcados en rojo");
            $('#projectName').focus();
            errors = true;
        }
        var description = $("#projectDescription").val();
        if (description.length > 1023){
            $("#form-group-projectDescription").addClass("has-error");
            $("#alert-danger-projectName").removeClass("hidden");
            $("#alert-danger-projectName").html("Comprueba los campos marcados en rojo");
            if (!errors){
                $('#projectDescription').focus();
            }
            errors = true;
        }
        if (!errors){
            this.updateRequest();
        }
    },
    
    editProject: function(projectId){
        this.projectId = projectId;
        this.setModalFields();
    },
    
    acceptEditProject: function(){
        this.checkModalFields();
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
    
    updateRequest: function(){
        var name = $("#projectName").val();
        var description = $("#projectDescription").val();
        var projectData = {
          name: name,
          description: description
        };
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + EditProjectModal.projectId + "/",
            method: "PUT",
            //contentType: "application/json",
            data: projectData,
            success : function(data) {
                EditProjectModal.projectId = null;
                EditProjectModal.hideModal();
                EditProjectModal.notifyPainters();
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                EditSimulationModal.hideModal();
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Modificación del proyecto", "Error en updateRequest. Código de error " + xhr.status);
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    },
    
    cancelEditProject: function(){
        this.hideModal();
    }
};

