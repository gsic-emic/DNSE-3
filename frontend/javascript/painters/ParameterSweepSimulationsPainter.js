var ParameterSweepSimulationsPainter = {
    
    paramSweepSimulations: null,
    
    init: function(){
    },
    
    paint: function(){
        this.getParameterSweepSimulations();
    },
    
    paintParameterSweepSimulationsTable: function(){
        $("#paramSweepSimulationsTable").removeClass("hidden");
        $("#noParamSweepSimulationsAlert").addClass("hidden");
        var table_body = $("#paramSweepSimulationsTable > tbody");
        table_body.empty();
        for (var i = 0; i < this.paramSweepSimulations.length; i++){
            var now_date = new Date();
            var param_sweep_sim_creation_date_field = getDateFieldText(this.paramSweepSimulations[i].creationDate, now_date);
            var param_sweep_sim_update_date_field = getDateFieldText(this.paramSweepSimulations[i].updateDate, now_date);
            
            var param_sweep_sim = new ParameterSweepSimulation(this.paramSweepSimulations[i]);
            var table_tr = $("<tr></tr>");
            var td_name = $('<td>' + this.paramSweepSimulations[i].name + '</td>');
            var td_num_rep = $("<td>" + this.paramSweepSimulations[i].numRepetitions + "</td>");
            var td_priority = $("<td>" + this.paramSweepSimulations[i].priority + "</td>")
            var td_param_cd = $("<td>" + param_sweep_sim_creation_date_field + "</td>");
            var td_param_ud = $("<td>" + param_sweep_sim_update_date_field + "</td>");
            var param_status_html = SingleSimulationsPainter.getStatusHtml(param_sweep_sim);
            var td_param_status = $(param_status_html);
            
            var disabled_actions = "";
            if (this.paramSweepSimulations[i].status == SimulationStatus.REMOVING){
                disabled_actions = 'disabled="disabled"';
            }
            
            var actions_html = '<td class="actions"><div class="btn-group actions-group">' +
                               '<button class="btn btn-default btn-sm" ' + disabled_actions + ' onclick="ViewParameterSweepSimulationModal.viewParameterSweepSimulation(' + this.paramSweepSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver detalles</button>' +
                               '<button data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle" ' + disabled_actions + '><span class="caret"></span></button>' +
                               '<ul class="dropdown-menu">' +
                                    '<li><a href="#" onclick="EditSimulationModal.editSimulation(' + this.paramSweepSimulations[i].simulationId + ',\'sweep\')"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar simulación</a></li>';            
            switch(this.paramSweepSimulations[i].status){
                case SimulationStatus.WAITING: //the simulation can be started
                                                 actions_html+= '<li><a href="#" onclick="ParameterSweepSimulationController.startSimulation(' + this.paramSweepSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> Ejecutar simulación</a></li>';
                                                 break;
                case SimulationStatus.PREPARING:   //no operations are allowed
                                                 break;
                case SimulationStatus.PROCESSING://the simulation can be paused or stopped
                                                 actions_html+= '<li><a href="#" onclick="ParameterSweepSimulationController.pauseSimulation(' + this.paramSweepSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-pause" aria-hidden="true"></span> Pausar simulación</a></li>';
                                                 actions_html+= '<li><a href="#" onclick="ParameterSweepSimulationController.stopSimulation(' + this.paramSweepSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-stop" aria-hidden="true"></span> Detener simulación</a></li>';
                                                 break;
                case SimulationStatus.PAUSED:    //the simulation can be resumed or stopped
                                                 actions_html+= '<li><a href="#" onclick="ParameterSweepSimulationController.startSimulation(' + this.paramSweepSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> Reanudar simulación</a></li>';
                                                 //actions_html+= '<li><a href="#" onclick="ParameterSweepSimulationController.stopSimulation(' + thisparamSweepSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-stop" aria-hidden="true"></span> Detener simulación</a></li>';
                                                 break;
                case SimulationStatus.CLEANING:  //no operations are allowed
                                                 break;
                case SimulationStatus.ERROR:     //no operations are allowed
                                                 break;
                case SimulationStatus.REPORTING:  //no operations are allowed
                                                 break;
                case SimulationStatus.FINISHED:  //actions_html+= '<li><a href="#" onclick="SimulationManager.showResults(' + this.paramSweepSimulations[i].simulationId + ')"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> Ver resultados</a></li>';
                                                 actions_html+= '<li><a href="' + getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"]  + '/parametersweepsimulations/' + this.paramSweepSimulations[i].simulationId + '/results"><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Obtener resultados</a></li>';
                                                 break;
                case SimulationStatus.REMOVING:  //no operations are allowed
                                                 break;
            }
            actions_html+= '<li><a href="#" onclick="DeleteSimulationModal.deleteSimulation(' + this.paramSweepSimulations[i].simulationId + ',\'sweep\')"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar simulación</a></li>' +
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
    
    /**
     * Hide the table for the parameter sweep simulations and show an alert message
     */
    showNoParameterSweepSimulations: function(){
        $("#paramSweepSimulationsTable").addClass("hidden");
        $("#noParamSweepSimulationsAlert").removeClass("hidden");
    },
    
    getParameterSweepSimulations: function(){
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/parametersweepsimulations/",
            method: "GET",
            dataType: "json",//the expected data type from the server
            success : function(data) {
                if (typeof data != 'undefined'){
                    ParameterSweepSimulationsPainter.paramSweepSimulations = data;
                }else{
                    ParameterSweepSimulationsPainter.paramSweepSimulations = null;
                }
                if (ParameterSweepSimulationsPainter.paramSweepSimulations!=null && ParameterSweepSimulationsPainter.paramSweepSimulations.length > 0){
                    ParameterSweepSimulationsPainter.paintParameterSweepSimulationsTable();
                }else{
                    ParameterSweepSimulationsPainter.showNoParameterSweepSimulations();
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


