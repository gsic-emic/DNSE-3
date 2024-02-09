/**
 * Manager for the modal dialog that deletes a project
 */
var DeleteProjectModal = {
    
    modalId: "delete-project-modal",
    cancelButtonId: "delete-project-modal-cancel",
    acceptButtonId: "delete-project-modal-accept",
    projectId: null,
    painters: null,
    
    init: function(){
        this.painters = new Array();
        //set the events for the buttons
        $("#" + this.acceptButtonId).click(function(){
            DeleteProjectModal.acceptDeleteProject();
        });
        $("#" + this.cancelButtonId).click(function(){
            DeleteProjectModal.cancelDeleteProject();
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
        $("#alert-danger-delete-project-modal").addClass("hidden");
        DeleteProjectModal.showModal();
    },
    
    checkModalFields: function(){
        //there are no fields to check
        this.deleteRequest();
    },
    
    deleteProject: function(projectId){
        this.projectId = projectId;
        this.setModalFields();
    },
    
    acceptDeleteProject: function(){
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
    
    deleteRequest: function(){
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + DeleteProjectModal.projectId  + "/",
            method: 'DELETE',
            success: function(data){
                DeleteProjectModal.projectId = null;
                DeleteProjectModal.hideModal();
                if (typeof G_VARS["projectId"] == "undefined"){
                    //delete a project from the projects page
                    InfoModal.info(" Eliminar proyecto", "El proyecto se ha borrado correctamente", function(){DeleteProjectModal.deleteRequestSuccessActionFromProjects()});
                }else{
                    G_VARS["refresh_painters"] = false;//Important!! Since the project has been deleted we can not update its info
                    //delete a project from the page for that project
                    InfoModal.info(" Eliminar proyecto", "El proyecto se ha borrado correctamente", function(){DeleteProjectModal.deleteRequestSuccessAction()});
                    //redirect to the projects page
                    //window.location.href = getBaseUrl() + "/php/controller/login.php";
                }
            },
            error : function(xhr, status) {
                $("#alert-danger-delete-project-modal").removeClass("hidden");
                $("#alert-danger-delete-project-modal").html("Se ha producido un error al tratar de eliminar el proyecto.");
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    },
    
    deleteRequestSuccessActionFromProjects: function(){
        DeleteProjectModal.notifyPainters();
    },
    
    deleteRequestSuccessAction: function(){
        window.location.href = getBaseUrl() + "/php/controller/login.php";
    },
    
    cancelDeleteProject: function(){
        this.hideModal();
    }
    
};

