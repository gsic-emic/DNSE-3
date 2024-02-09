/**
 * Manager for the modal dialog that edits a project
 */
var ViewProjectModal = {
    
    modalId: "view-project-modal",
    closeButtonId: "view-project-modal-close",
    projectId: null,
    
    init: function(){
        this.painters = new Array();
    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    },
    
    viewProject: function(projectId){
        this.projectId = projectId;
        
        this.getProjectDescription();
        this.getProjectParameters();
        this.getProjectOutputFiles();
        this.showModal();
        //show the first tab when opening the modal
        $('#view-project-details-tabs a:first').tab('show');
    },
    
    getProjectDescription: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + ViewProjectModal.projectId  + "/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                ViewProjectModal.setProjectDescriptionTab(data);
            },
            error : function(xhr, status) {
                //show the error
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Proyecto", "Error en la obtención de la descripción del proyecto.");
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                //alert(status);
            }
        });
    },
            
    getProjectParameters: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + ViewProjectModal.projectId + "/parameters/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                ViewProjectModal.setProjectParametersTab(data);
            },
            error : function(xhr, status) {
                //show the error
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Proyecto", "Error en la obtención de los parámetros del proyecto.");
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
    },
    
    getProjectOutputFiles: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + ViewProjectModal.projectId + "/outputfiles/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                ViewProjectModal.setProjectOutputFilesTab(data);
            },
            error : function(xhr, status) {
                //show the error
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Proyecto", "Error en la obtención de los ficheros de salida del proyecto.");
            },

            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
      
    },
    
    setProjectDescriptionTab: function(project){
        var now_date = new Date();
        var project_creation_date_field = getDateFieldText(project.creationDate, now_date);
        var project_update_date_field = getDateFieldText(project.updateDate, now_date);
        
        var details_tp = $("#view-project-details-tp-desc");
        details_tp.empty();
        var div_inf_panel = $('<div class="information-panel"></div');
        var panel_content = "";
        panel_content += '<span class="title">Nombre</span>' + project.name;
        panel_content += '<span class="title">Descripción</span>' + project.description;
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Fecha de creación</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + project_creation_date_field + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Última modificación</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + project_update_date_field + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Simulaciones individuales</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + project.numSingleSimulations + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Barrido de parámetros</span></div>';
        panel_content += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + project.numParameterSweepSimulations + '</div>';
        panel_content += '</div>';
        
        details_tp.append(div_inf_panel);
        div_inf_panel.html(panel_content);
    },
    
    setProjectParametersTab: function(parameters){
        var params_tp = $("#view-project-details-tp-params");
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
            panel_content_html += '<div class="row">';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Tipo de parámetro</span></div>';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">' + ParameterType.getDescription(param.type) + '</div>';
            panel_content_html += '</div>';
            
            if (typeof param.possibleValues != 'undefined'){
                panel_content_html += '<div class="row">';
                panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Posibles valores</span></div>';
                panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">';
                for (var j = 0; j < param.possibleValues.length; j++){
                    var pv = param.possibleValues[j];
                    panel_content_html += pv;
                    if (j < (param.possibleValues.length-1)){
                        panel_content_html += ', ';
                    }
                }
                panel_content_html += '</div>';
                panel_content_html += '</div>';
            }
            
            var pg = param.greaterThan;
            var pge = param.greaterThanOrEqualTo;
            var pl = param.lessThan;
            var ple = param.lessThanOrEqualTo;
            
            if (typeof pg != 'undefined' || typeof pge != 'undefined' || typeof pl != 'undefined' || typeof ple != 'undefined'){
                panel_content_html += '<div class="row">';
                panel_content_html += '<div class="col-sm-6 col-md-6 col-md-6 col-lg-6"><span class="title">Rango</span></div>';
                panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">';
                if (typeof pg != 'undefined'){
                    panel_content_html += pg + ' < ';
                }else if (typeof pge != 'undefined'){
                    panel_content_html += pge + ' <= ';
                }
                panel_content_html += '#';
                if (typeof pl != 'undefined'){
                    panel_content_html += ' < ' + pl;
                }else if (typeof ple != 'undefined'){
                    panel_content_html += ' <= ' + ple;
                }
                panel_content_html += '</div>';
                panel_content_html += '</div>';
            }   
            
            panel_content_html += '<div class="row">';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6"><span class="title">Valor por defecto</span></div>';
            panel_content_html += '<div class="col-sm-6 col-md-6 col-lg-6 content-value">';
            if (typeof param.defaultValue != 'undefined'){
                panel_content_html += param.defaultValue;
            }else{
                panel_content_html += 'random';
            }
            panel_content_html += '</div>';
            panel_content_html += '</div>';
            
            panel_content.html(panel_content_html);
            setExpandableSection(div_inf_panel[0]);
        }
    },
    
    setProjectOutputFilesTab: function(outputFiles){
        var output_files_tp = $("#view-project-details-tp-files");
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
            } else{
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
                panel_content_html += '-';
            }else{
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

