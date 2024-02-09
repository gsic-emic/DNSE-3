/**
 * Manager for the modal dialog that creates a simulation project
 */
var NewProjectModal = {
    
    modalId: "new_project_modal",
    cancelButtonId: "new_project_modal_cancel",
    acceptButtonId: "new_project_modal_accept",
    painters: null,
    
    init: function(){
        this.painters = new Array();
        //set the events for the buttons
        $("#" + this.acceptButtonId).click(function(){
            NewProjectModal.acceptNewProject();
        });
        $("#" + this.cancelButtonId).click(function(){
            NewProjectModal.cancelNewProject();
        });
    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    },
    
    setModalFields: function(){
        //reset the form
        $("#form-new_project_modal")[0].reset();
        //remove the error messages
        $("#form-group-projectFile").removeClass("has-error");
        $("#alert-danger-new_project_modal").addClass("hidden");
        NewProjectModal.showModal();
    },
    
    checkModalFields: function(){
        var errors = false;
        var file_name = $("#projectFile").val();
        var ext = file_name.substring(file_name.lastIndexOf(".")+1);
        if (file_name == "" || ext!= "zip"){
            $("#form-group-projectFile").addClass("has-error");
            $("#alert-danger-new_project_modal").removeClass("hidden");
            $("#alert-danger-new_project_modal").html("Comprueba los campos marcados en rojo");
            $('#projectFile').focus();
            errors = true;
        }
        if (!errors){
            this.postRequest();
        }
    },
    
    newProject: function(){
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
    
    acceptNewProject: function(){
        this.checkModalFields();
    },
    
    postRequest: function(){
        //upload the file through Ajax without using a iframe
        //the FormData object makes the tricky thing
        var data = new FormData();
        /*jQuery.each(jQuery('#form-new_project_modal input:file')[0].files, function(i, file) {
            //append the files
            data.append(file.name, file);
        });*/
        var file = $('#projectFile')[0].files[0];
        data.append("projectFile", file);
        //disable the button until the response is ready
        $("#" + this.acceptButtonId).prop("disabled", true);
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/",
            data: data,
            cache: false, //avoid using cache
            contentType: false, //force jQuery not to add the content type for us
            processData: false, //to avoid jQuery trying to convert the data to string, which would fail
            type: 'POST',
            success: function(data, status, xhr){
                NewProjectModal.hideModal();
                switch(xhr.status){
                    case 100: //the project is being created but has not been created yet
                        InfoModal.info(" Nuevo proyecto", "El proyecto se está creando. Una vez creado se mostrará en el listado de proyectos", function(){NewProjectModal.projectCreated()});
                        break;
                    case 201: //the project has been created
                        InfoModal.info(" Nuevo proyecto", "El proyecto se ha creado correctamente", function(){NewProjectModal.projectCreated()});
                        break;
                }
            },
            error : function(xhr, status) {
                $("#form-group-projectFile").addClass("has-error");
                $("#alert-danger-new_project_modal").removeClass("hidden");
                $("#alert-danger-new_project_modal").html(xhr.responseText);
                $('#projectFile').focus();
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                $("#" + NewProjectModal.acceptButtonId).prop("disabled", false);
                //alert(status);
            }
        });
    },
    
    projectCreated: function(){
        NewProjectModal.notifyPainters();
    },
    
    cancelNewProject: function(){
        this.hideModal();
    }
    
};

