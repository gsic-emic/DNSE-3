/**
 * Manager for the modal dialog that views a simulation
 */
var ViewSingleSimulationModal = {
    
    modalId: "view-simulation-modal",
    closeButtonId: "view-simulation-modal-close",
    simulationId: null,
    
    init: function(){
        
    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    },
    
    viewSingleSimulation: function(simulationId){
        this.simulationId = simulationId;
        
        this.getSimulationDescription();
        this.getSimulationParameters();
        this.getSimulationOutputFiles();
        this.showModal();
        //show the first tab when opening the modal
        $('#view-simulation-details-tabs a:first').tab('show');
    },
    
    getSimulationDescription: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/singlesimulations/" + ViewSingleSimulationModal.simulationId,
            method: "GET",
            success : function(data) {
                ViewSingleSimulationModal.setSimulationDescriptionTab(data);
            },
            error : function(xhr, status) {
                //show the error
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Simulación individual", "Error en la obtención de la descripción de la simulación individual.");
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    },
            
    getSimulationParameters: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/singlesimulations/" + ViewSingleSimulationModal.simulationId + "/parameters/",
            method: "GET",
            success : function(data) {
                ViewSingleSimulationModal.setSimulationParametersTab(data);
            },
            error : function(xhr, status) {
                //show the error
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Simulación individual", "Error en la obtención de los parámetros de la simulación individual.");
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
    },
    
    getSimulationOutputFiles: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/singlesimulations/" + ViewSingleSimulationModal.simulationId + "/outputfiles/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                ViewSingleSimulationModal.setSimulationOutputFiles(data);
            },
            error : function(xhr, status) {
                //show the error
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Simulación individual", "Error en la obtención de los ficheros de salida de la simulación individual.");
            },

            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
      
    },
    
    setSimulationDescriptionTab: function(simulation){
        var now_date = new Date();
        var simulation_creation_date_field = getDateFieldText(simulation.creationDate, now_date);
        var simulation_update_date_field = getDateFieldText(simulation.updateDate, now_date);
        
        var details_tp = $("#view-simulation-details-tp-desc");
        details_tp.empty();
        var div_inf_panel = $('<div class="information-panel"></div');
        var panel_content = "";
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Nombre</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + simulation.name + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Número de repeticiones</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + simulation.numRepetitions + '</div>';
        panel_content += '</div>';

        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Prioridad de la simulación</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + simulation.priority + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Tipo de simulación</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + 'Individual' + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Estado</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + SimulationStatus.getDescription(simulation.status) + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Fecha de creación</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + simulation_creation_date_field + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Última modificación</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + simulation_update_date_field + '</div>';
        panel_content += '</div>';
        
        if (typeof simulation.completedSimulations != 'undefined'){
            panel_content += '<div class="row">';
            panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Simulaciones completadas</span></div>';
            panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' +  simulation.completedSimulations + '</div>';
            panel_content += '</div>';
        }
        if (typeof simulation.totalSimulations != 'undefined'){
            panel_content += '<div class="row">';
            panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title"><span class="title">Simulaciones totales</span></div>';
            panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' +  simulation.totalSimulations + '</div>';
            panel_content += '</div>';
        }
        details_tp.append(div_inf_panel);
        div_inf_panel.html(panel_content);
    },
    
    setSimulationParametersTab: function(parameters){
        var params_tp = $("#view-simulation-details-tp-params");
        params_tp.empty();
        for (var i = 0; i < parameters.length; i++){
            var param = parameters[i];
            var div_inf_panel = $('<div class="information-panel"></div');
            params_tp.append(div_inf_panel);
            var panel_title = $('<div class="information-panel-title expandable"></div>');
            div_inf_panel.append(panel_title);
            var panel_title_html = '<span class="title">' + param.name + '<span class="glyphicon glyphicon-triangle-bottom pull-right" aria-hidden="true"></span></span>';
            panel_title.html(panel_title_html);
            var panel_content = $('<div class="information-panel-content"></div>');
            div_inf_panel.append(panel_content);
            var panel_content_html = '';
            if (typeof param.value != 'undefined'){
                panel_content_html += '<div class="row">';
                panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Valor</span></div>';
                panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + param.value + '</div>';
                panel_content_html += '</div>';
            }else if (param.random){
                panel_content_html += '<div class="row">';
                panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title"><span class="title">Valor</span></div>';
                panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + 'random' + '</div>';
                panel_content_html += '</div>';
            }
            
            panel_content.html(panel_content_html);
            setExpandableSection(div_inf_panel[0]);
        }
    },
    
    setSimulationOutputFiles: function(outputFiles){
        var output_files_tp = $("#view-simulation-details-tp-files");
        output_files_tp.empty();
        for (var i = 0; i < outputFiles.length; i++){
            var outputFile = outputFiles[i];
            var outputFileStructure = outputFile.outputFileStructure;
            var div_inf_panel = $('<div class="information-panel"></div');
            output_files_tp.append(div_inf_panel);
            var panel_title = $('<div class="information-panel-title expandable"></div>');
            div_inf_panel.append(panel_title);
            var panel_title_html = '<span class="title">' + outputFile.outputFileName + '<span class="glyphicon glyphicon-triangle-bottom pull-right" aria-hidden="true"></span></span>';
            panel_title.html(panel_title_html);
            
            var panel_content = $('<div class="information-panel-content"></div>');
            div_inf_panel.append(panel_content);
            var panel_content_html = '';
            panel_content_html += '<div class="row">';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Tipo</span></div>';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + OutputFileType.getDescription(outputFile.type) + '</div>';
            panel_content_html += '</div>';
            
            panel_content_html += '<div class="row">';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Multilínea</span></div>';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">';
            if (outputFile.type == "TRACE_FILE"){
                panel_content_html += 'No';
            }
            else{
                if (outputFileStructure.multiLine){
                    panel_content_html += 'Sí';
                }else{
                    panel_content_html += 'No';
                }
            }
            panel_content_html += '</div>';
            panel_content_html += '</div>';
            
            panel_content_html += '<div class="row">';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Variables de salida</span></div>';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">';
            if (outputFile.type == "TRACE_FILE"){
                panel_content_html += "-";
            }
            else{
                var output_vars = outputFileStructure.outputVariables;
                for (var j = 0; j < output_vars.length; j++){
                    panel_content_html += output_vars[j];
                    if (j < (output_vars.length-1)){
                        panel_content_html += ', ';
                    }
                }
            }
            panel_content_html += '</div>';
            panel_content_html += '</div>';
            
            panel_content.html(panel_content_html);
            setExpandableSection(div_inf_panel[0]);
        }
    }
};

