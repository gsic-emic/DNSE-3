var SimulationPainter = {
    
    init: function(){
    },
    
    paint: function(){
        SimulationPainter.getSimulationProject();
        //SimulationPainter.getProjectParameters();
        //SimulationPainter.getProjectOutputFiles();
    },   
    
    getSimulationProject: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"]  + "/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                SimulationPainter.paintProjectTitle(data);
                SimulationPainter.paintProjectDescription(data);
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
    },
    
    /**
     * the project doesn't exist. We have to warn the user and return to the projects page
     */
    projectDoesNotExist: function(){
        window.location.href = getBaseUrl() + "/php/controller/login.php";
    },
    
    getProjectParameters: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/parameters/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                SimulationPainter.paintProjectParameters(data);
            },
            error : function(xhr, status) {
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Carga parámetros de la simulación", "Se ha producido un error al intentar cargar los parámetros de la simulación.");
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
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/outputfiles/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                SimulationPainter.paintProjectOutputFiles(data);
            },
            error : function(xhr, status) {
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Carga de ficheros de salida", "Se ha producido un error al intentar cargar los ficheros de salida de la simulación.");

            },

            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
      
    },
    
    paintProjectTitle: function(simulation){
        //set the values for the fields
        $("#simulation-project-name").html("Proyecto: " + simulation.name);    
    },
    
    paintProjectDescription: function(project){
        var now_date = new Date();
        var project_creation_date_field = getDateFieldText(project.creationDate, now_date);
        var project_update_date_field = getDateFieldText(project.updateDate, now_date);
        
        var details_tp = $("#sim-project-details-tp-desc");
        details_tp.empty();
        var div_inf_panel = $('<div class="information-panel"></div');
        var panel_content = "";
        panel_content += '<span class="title">Nombre</span>' + project.name;
        panel_content += '<span class="title">Descripción</span>' + project.description;
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-lg-6"><span class="title">Fecha de creación</span></div>';
        panel_content += '<div class="col-sm-6 col-lg-6 content-value">' + project_creation_date_field + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-lg-6"><span class="title">Última modificación</span></div>';
        panel_content += '<div class="col-sm-6 col-lg-6 content-value">' + project_update_date_field + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-lg-6"><span class="title">Simulaciones individuales</span></div>';
        panel_content += '<div class="col-sm-6 col-lg-6 content-value">' + project.numSingleSimulations + '</div>';
        panel_content += '</div>';
        
        panel_content += '<div class="row">';
        panel_content += '<div class="col-sm-6 col-lg-6"><span class="title">Barrido de parámetros</span></div>';
        panel_content += '<div class="col-sm-6 col-lg-6 content-value">' + project.numParameterSweepSimulations + '</div>';
        panel_content += '</div>';
        
        details_tp.append(div_inf_panel);
        div_inf_panel.html(panel_content);
    },
    
    paintProjectParameters: function(parameters){
        var params_tp = $("#sim-project-details-tp-params");
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
            panel_content_html += '<span class="title">Tipo de parámetro</span>' + param.type;
            
            if (typeof param.possibleValues != 'undefined'){
                panel_content_html += '<span class="title">Posibles valores</span>';
                for (var j = 0; j < param.possibleValues.length; j++){
                    var pv = param.possibleValues[j];
                    panel_content_html += pv;
                    if (j < (param.possibleValues.length-1)){
                        panel_content_html += ', ';
                    }
                }
            }
            
            var pg = param.greaterThan;
            var pge = param.greaterThanOrEqualTo;
            var pl = param.lessThan;
            var ple = param.lessThanOrEqualTo;
            
            if (typeof pg != 'undefined' || typeof pge != 'undefined' || typeof pl != 'undefined' || typeof ple != 'undefined'){
                panel_content_html += '<span class="title">Rango</span>';
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
            }   
            
            panel_content_html += '<span class="title">Valor por defecto</span>';
            if (typeof param.defaultValue != 'undefined'){
                panel_content_html += param.defaultValue;
            }else{
                panel_content_html += 'random';
            }
            
            panel_content.html(panel_content_html);
            setExpandableSection(div_inf_panel[0]);
        }
        
        //setExpandableSections();
    },
    
    paintProjectOutputFiles: function(outputFiles){
        var output_files_tp = $("#sim-project-details-tp-files");
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
            panel_content_html += '<span class="title">Tipo</span>' + outputFile.type;
            panel_content_html += '<span class="title">Multilínea</span>';
            if (outputFileStructure.multiLine){
                panel_content_html += 'Sí';
            }else{
                panel_content_html += 'No';
            }
            panel_content_html += '<span class="title">Variables de salida</span>';
            
            var output_vars = outputFileStructure.outputVariables;
            for (var j = 0; j < output_vars.length; j++){
                panel_content_html += output_vars[j];
                if (j < (output_vars.length-1)){
                    panel_content_html += ', ';
                }
            }
            panel_content.html(panel_content_html);
            setExpandableSection(div_inf_panel[0]);
        }
    }
    
    
}

