var SingleSimulationsPainter = {
    
    singleSimulations: null,
    
    init: function(){
    },
    
    paint: function(){
        this.getSingleSimulations();
    },
    
    /**
     * Fill in the table for the single simulations
     */
    paintSingleSimulationsTable: function(){
        $("#singleSimulationsTable").removeClass("hidden");
        $("#noSingleSimulationsAlert").addClass("hidden");
        var table_body = $("#singleSimulationsTable > tbody");
        table_body.empty();
        for (var i = 0; i < this.singleSimulations.length; i++){
            var now_date = new Date();
            var single_sim_creation_date_field = getDateFieldText(this.singleSimulations[i].creationDate, now_date);
            var single_sim_update_date_field = getDateFieldText(this.singleSimulations[i].updateDate, now_date);
            
            var single_sim = new SingleSimulation(this.singleSimulations[i]);
            var table_tr = $("<tr></tr>");
            var td_name = $('<td>' + this.singleSimulations[i].name + '</td>');
            var td_num_rep = $("<td>" + this.singleSimulations[i].numRepetitions + "</td>");
            var td_priority = $("<td>" + this.singleSimulations[i].priority + "</td>");
            var td_param_cd = $("<td>" + single_sim_creation_date_field + "</td>");
            var td_param_ud = $("<td>" + single_sim_update_date_field + "</td>");
            var param_status_html = this.getStatusHtml(single_sim);
            var td_param_status = $(param_status_html);
            
            var disabled_actions = "";
            if (this.singleSimulations[i].status == SimulationStatus.REMOVING){
                disabled_actions = 'disabled="disabled"';
            }
                                        
            var actions_html = '<td class="actions"><div class="btn-group actions-group">' +
                               '<button class="btn btn-default btn-sm" ' + disabled_actions + ' onclick="ViewSingleSimulationModal.viewSingleSimulation(' + this.singleSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver detalles</button>' +
                               '<button data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle" ' + disabled_actions + '><span class="caret"></span></button>' +
                               '<ul class="dropdown-menu">' +
                                    '<li><a href="#" onclick="EditSimulationModal.editSimulation(' + this.singleSimulations[i].simulationId + ',\'single\')"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar simulación</a></li>';
            switch(this.singleSimulations[i].status){
                case SimulationStatus.WAITING: //the simulation can be started
                                                 actions_html+= '<li><a href="#" onclick="SingleSimulationController.startSimulation(' + this.singleSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> Ejecutar simulación</a></li>';
                                                 break;
                case SimulationStatus.PREPARING:   //no operations are allowed
                                                 break;
                case SimulationStatus.PROCESSING://the simulation can be paused or stopped
                                                 actions_html+= '<li><a href="#" onclick="SingleSimulationController.pauseSimulation(' + this.singleSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-pause" aria-hidden="true"></span> Pausar simulación</a></li>';
                                                 actions_html+= '<li><a href="#" onclick="SingleSimulationController.stopSimulation(' + this.singleSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-stop" aria-hidden="true"></span> Detener simulación</a></li>';
                                                 break;
                case SimulationStatus.PAUSED:    //the simulation can be resumed or stopped
                                                 actions_html+= '<li><a href="#" onclick="SingleSimulationController.startSimulation(' + this.singleSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> Reanudar simulación</a></li>';
                                                 //actions_html+= '<li><a href="#" onclick="SingleSimulationController.stopSimulation(' + this.singleSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-stop" aria-hidden="true"></span> Detener simulación</a></li>';
                                                 break;
                case SimulationStatus.CLEANING:  //no operations are allowed
                                                 break;
                case SimulationStatus.ERROR:     //no operations are allowed
                                                 break;
                case SimulationStatus.REPORTING:  //no operations are allowed
                                                 break;
                case SimulationStatus.FINISHED:  //actions_html+= '<li><a href="#" onclick="SimulationManager.showResults(' + this.singleSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> Ver resultados</a></li>';
                                                 actions_html+= '<li><a href="' + getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"]  + "/singlesimulations/" +  this.singleSimulations[i].simulationId + '/results"><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Obtener resultados</a></li>';
                                                 break;
                case SimulationStatus.REMOVING:  //no operations are allowed
                                                 break;
            }
            actions_html+= '<li><a href="#" onclick="DeleteSimulationModal.deleteSimulation(' + this.singleSimulations[i].simulationId + ',\'single\')"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar simulación</a></li>' +
                           '</ul>' +
                           '</div></td>';
           var td_actions = $(actions_html);
           table_tr.append(td_name);
           table_tr.append(td_num_rep);
           table_tr.append(td_priority);
           table_tr.append(td_param_cd);
           table_tr.append(td_param_ud);
           table_tr.append(td_param_status);
           table_tr.append(td_actions);
           table_body.append(table_tr);
        }
        $('[data-toggle="tooltip"]').tooltip();
    },
    
    getStatusHtml: function(simulation){
        var param_status_html = '<td class="status">';
        param_status_html += '<div>';
        var progress_bar_type;
        var status_description = SimulationStatus.getDescription(simulation.getStatus());
        switch(simulation.getStatus()){
            case SimulationStatus.WAITING:
                param_status_html += '<span class="glyphicon glyphicon-pause" aria-hidden="true"></span>';
                param_status_html += '<span class="glyphicon glyphicon-cog" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">' + status_description + '</span>';
                break;
            case SimulationStatus.PREPARING:
                progress_bar_type = "progress-bar-warning";
                param_status_html += '<div class="progress">';
                param_status_html += '<div class="progress-bar ' + progress_bar_type + ' progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="100" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                param_status_html += '</div>';
                param_status_html += '<span class="glyphicon glyphicon-time" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">' + status_description + '</span>';
                break;
            case SimulationStatus.PROCESSING:
                progress_bar_type = "";
                param_status_html += '<div class="progress">';
                param_status_html += '<div class="progress-bar ' + progress_bar_type + ' progress-bar-striped active" role="progressbar" aria-valuenow="' + simulation.getPercentageCompleted(2) + '" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: ' + simulation.getPercentageCompleted(0) + '%;">' +
                simulation.getPercentageCompleted(2) + '%' + '</div>';
                param_status_html += '</div>';
                param_status_html += '<span class="sr-only">' + simulation.getPercentageCompleted(2) + '% Completed</span>';
                param_status_html += '<span class="glyphicon glyphicon-play" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">'+ status_description + '</span>';
                break;
            case SimulationStatus.PAUSED:
                progress_bar_type = "";
                param_status_html += '<div class="progress">';
                param_status_html += '<div class="progress-bar ' + progress_bar_type + ' progress-bar-striped" role="progressbar" aria-valuenow="' + simulation.getPercentageCompleted(2) + '" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: ' + simulation.getPercentageCompleted(0) + '%;">' +
                simulation.getPercentageCompleted(2) + '%' + '</div>';
                param_status_html += '</div>';
                param_status_html += '<span class="sr-only">' + simulation.getPercentageCompleted(2) + '% Completed</span>';
                param_status_html += '<span class="glyphicon glyphicon-pause" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">' + status_description + '</span>';
                break;
            case SimulationStatus.CLEANING:
                progress_bar_type = "progress-bar-info";
                param_status_html += '<div class="progress">';
                param_status_html += '<div class="progress-bar ' + progress_bar_type + ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                param_status_html += '</div>';
                param_status_html += '<span class="glyphicon glyphicon-erase" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">' + status_description + '</span>';
                break;
            case SimulationStatus.ERROR:
                progress_bar_type = "progress-bar-danger";
                param_status_html += '<div class="progress">';
                param_status_html += '<div class="progress-bar ' + progress_bar_type + ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                param_status_html += '</div>';
                param_status_html += '<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">' + '</span>';
                break;
            case SimulationStatus.REPORTING:
                progress_bar_type = "progress-bar-info";
                param_status_html += '<div class="progress">';
                param_status_html += '<div class="progress-bar ' + progress_bar_type + ' progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                param_status_html += '</div>';
                param_status_html += '<span class="glyphicon glyphicon-hourglass" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">' + status_description + '</span>';  
                break;
            case SimulationStatus.FINISHED:
                progress_bar_type = "progress-bar-success";
                param_status_html += '<div class="progress">';
                param_status_html += '<div class="progress-bar ' + progress_bar_type + ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                param_status_html += '</div>';
                param_status_html += '<span class="glyphicon glyphicon-ok" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">' + status_description + '</span>';
                break;
            case SimulationStatus.REMOVING:
                progress_bar_type = "progress-bar-danger";
                param_status_html += '<div class="progress">';
                param_status_html += '<div class="progress-bar ' + progress_bar_type + ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                param_status_html += '</div>';
                param_status_html += '<span class="glyphicon glyphicon-ban-circle" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' + status_description + '"></span><span class="sr-only">' + status_description + '</span>';
                break;
            default:
                param_status_html += simulation.getStatus();
                                                 
        }
        param_status_html += '</div>';
        param_status_html += "</td>";
        return param_status_html;
    },
    
     /**
     * Hide the table for the single simulations and shows an alert message
     */
    showNoSingleSimulations: function(){
        $("#singleSimulationsTable").addClass("hidden");
        $("#noSingleSimulationsAlert").removeClass("hidden");
    },
    
    getSingleSimulations: function(){
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/singlesimulations/",
            method: "GET",
            dataType: "json",//the expected data type from the server
            success : function(data) {
                if (typeof data != 'undefined'){
                    SingleSimulationsPainter.singleSimulations = data;
                }else{
                    SingleSimulationsPainter.singleSimulations = null;
                }
                if (SingleSimulationsPainter.singleSimulations!=null && SingleSimulationsPainter.singleSimulations.length > 0){
                    SingleSimulationsPainter.paintSingleSimulationsTable();
                }else{
                    SingleSimulationsPainter.showNoSingleSimulations();
                }
            },
            error : function(xhr, status) {
                switch(xhr.status){
                    case 404: //the simulation project doesn't exist
                              ErrorModal.error(" Error", "El proyecto no existe.",function(){SimulationPainter.projectDoesNotExist()});
                              G_VARS["refresh_painters"] = false;//Important!! Since the resource doesn't exist we can not update its info
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
    }
};

