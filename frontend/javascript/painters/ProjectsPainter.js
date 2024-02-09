var ProjectsPainter = {
    
    projects: null,
    
    init: function(){
    },
    
    paint: function(){
        ProjectsPainter.getSimulationProjects();
    },
    
    paintProjectsTable: function(){
        $("#simulationProjectsTable").removeClass("hidden");
        $("#noSimulationProjectsAlert").addClass("hidden");
        var table_body = $("#simulationProjectsTable > tbody");
        table_body.empty();
        for (var i = 0; i < this.projects.length; i++){
            if (!this.projects[i].removing){
                var now_date = new Date();
                var project_creation_date_field = getDateFieldText(this.projects[i].creationDate, now_date);
                var project_update_date_field = getDateFieldText(this.projects[i].updateDate, now_date);

                var table_tr = $('<tr id="tr_projectId_' + this.projects[i].projectId + '"></tr>');
                var td_name = $('<td><a href="' + getBaseUrl() + 'php/controller/simulationProject.php?projectId=' + this.projects[i].projectId + '">' + this.projects[i].name + '</a></td>');
                var td_single_sim = $("<td>" + this.projects[i].numSingleSimulations + "</td>");
                var td_param_sweep_sim = $("<td>" + this.projects[i].numParameterSweepSimulations + "</td>");
                var td_param_cd = $("<td>" + project_creation_date_field + "</td>");
                var td_param_ud = $("<td>" + project_update_date_field + "</td>");
                var td_actions = $('<td class="actions"><div class="btn-group actions-group">' +
                                            '<button class="btn btn-default btn-sm" onclick="ViewProjectModal.viewProject(' + this.projects[i].projectId + ')"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver detalles</button>' +
                                            '<button data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle"><span class="caret"></span></button>' +
                                            '<ul class="dropdown-menu">' +
                                                '<li><a href="#" onclick="NewSimulationModal.newSimulation(' + this.projects[i].projectId + ')"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Nueva simulación</a></li>' +
                                                '<li><a href="#" onclick="EditProjectModal.editProject(' + this.projects[i].projectId + ')"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar proyecto</a></li>' +
                                                '<!--<li><a href="#" onclick="DeleteProjectModal.deleteProject(' + this.projects[i].projectId + ')"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar proyecto</a></li>-->' +
                                                '<li><a href="#" onclick="location.reload();" download><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Descargar modelo</a></li>' +
                                            '</ul>' +
                                        '</div></td>');
               table_tr.append(td_name);
               table_tr.append(td_single_sim);
               table_tr.append(td_param_sweep_sim);
               table_tr.append(td_param_cd);
               table_tr.append(td_param_ud);
               table_tr.append(td_actions);
               table_body.append(table_tr);
           }
        }
    },
    
    refreshProjectsTable: function(){
        $("#simulationProjectsTable").removeClass("hidden");
        $("#noSimulationProjectsAlert").addClass("hidden");
        var table_body = $("#simulationProjectsTable > tbody");
        //table_body.empty();
        $("#simulationProjectsTable > tbody tr").each(function(index, element){
            //remove the rows for the projects that no longer exist
            var exists = false;
            for (var i = 0; i < ProjectsPainter.projects.length ; i++){
                if (!ProjectsPainter.projects[i].removing){
                    if ($(this).prop("id") == ("tr_projectId_" + ProjectsPainter.projects[i].projectId)){
                        exists = true;
                        break;
                    }
                }
            }
            if (!exists){
                $(this).remove();
            }
        });
        
        for (var i = 0; i < this.projects.length; i++){
            //take into account only the projects which are not being removed
            if (!this.projects[i].removing){
                var now_date = new Date();
                var project_creation_date_field = getDateFieldText(this.projects[i].creationDate, now_date);
                var project_update_date_field = getDateFieldText(this.projects[i].updateDate, now_date);

                if ($('#tr_projectId_' + this.projects[i].projectId).length == 0){//a new project
                    var table_tr = $('<tr id="tr_projectId_' + this.projects[i].projectId + '"></tr>');
                    var td_name = $('<td><a href="' + getBaseUrl() + 'php/controller/simulationProject.php?projectId=' + this.projects[i].projectId + '">' + this.projects[i].name + '</a></td>');
                    var td_single_sim = $("<td>" + this.projects[i].numSingleSimulations + "</td>");
                    var td_param_sweep_sim = $("<td>" + this.projects[i].numParameterSweepSimulations + "</td>");
                    var td_param_cd = $("<td>" + project_creation_date_field + "</td>");
                    var td_param_ud = $("<td>" + project_update_date_field + "</td>");
                    var td_actions = $('<td class="actions"><div class="btn-group actions-group">' +
                                                '<button class="btn btn-default btn-sm" onclick="ViewProjectModal.viewProject(' + this.projects[i].projectId + ')"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver detalles</button>' +
                                                '<button data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle"><span class="caret"></span></button>' +
                                                '<ul class="dropdown-menu">' +
                                                    '<li><a href="#" onclick="NewSimulationModal.newSimulation(' + this.projects[i].projectId + ')"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Nueva simulación</a></li>' +
                                                    '<li><a href="#" onclick="EditProjectModal.editProject(' + this.projects[i].projectId + ')"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar proyecto</a></li>' +
                                                    '<!--<li><a href="#" onclick="DeleteProjectModal.deleteProject(' + this.projects[i].projectId + ')"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar proyecto</a></li>-->' +
                                                    '<li><a href="#" onclick="location.reload();" download><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Descargar modelo</a></li>' +
                                                '</ul>' +
                                            '</div></td>');
                   table_tr.append(td_name);
                   table_tr.append(td_single_sim);
                   table_tr.append(td_param_sweep_sim);
                   table_tr.append(td_param_cd);
                   table_tr.append(td_param_ud);
                   table_tr.append(td_actions);
                   table_body.append(table_tr);
                }else{//an already existing project
                   var td_name = $('#tr_projectId_' + this.projects[i].projectId + ' > td:nth-child(1)');
                   td_name.html('<a href="' + getBaseUrl() + 'php/controller/simulationProject.php?projectId=' + this.projects[i].projectId + '">' + this.projects[i].name + '</a>');
                   var td_single_sim = $('#tr_projectId_' + this.projects[i].projectId + ' > td:nth-child(2)');
                   td_single_sim.html(this.projects[i].numSingleSimulations);
                   var td_param_sweep_sim = $('#tr_projectId_' + this.projects[i].projectId + ' > td:nth-child(3)');
                   td_param_sweep_sim.html(this.projects[i].numParameterSweepSimulations);
                   var td_param_cd = $('#tr_projectId_' + this.projects[i].projectId + ' > td:nth-child(4)');
                   td_param_cd.html(project_creation_date_field);
                   var td_param_ud = $('#tr_projectId_' + this.projects[i].projectId + ' > td:nth-child(5)');
                   td_param_ud.html(project_update_date_field);
                }
            }
        }
    },
    
    /**
     * Hide the table for the single simulations and shows an alert message
     */
    showNoProjects: function(){
        $("#simulationProjectsTable").addClass("hidden");
        $("#noSimulationProjectsAlert").removeClass("hidden");
    },
    
    getSimulationProjects: function(){
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/",
            method: "GET",
            dataType: "json",//the expected data type from the server
            success : function(data) {
                ProjectsPainter.projects = data;
                var there_are_projects = false;
                for (var i = 0; i < ProjectsPainter.projects.length; i++){
                    if (!ProjectsPainter.projects[i].removing){
                        there_are_projects = true;
                        break;
                    }
                }
                if (there_are_projects){
                    //ProjectsPainter.paintProjectsTable();
                    ProjectsPainter.refreshProjectsTable();
                }else{
                    ProjectsPainter.showNoProjects();
                }
            },
            error : function(xhr, status) {
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Carga de proyectos de simulación", "Se ha producido un error al intentar cargar los proyectos.");
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    }
};


