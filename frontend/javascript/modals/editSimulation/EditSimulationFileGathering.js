/**
 * Manager for the modal dialog that manage the output file structures
 */
var EditSimulationFileGathering = {
    
     /**
     * the output files that are available for the simulation
     */
    outputFiles: null,
    
    /**
     * it keeps the status of the output files that the user can choose
     */
    outputFilesChoose: null,
    
    init: function(){
        $("#edit-simulation-file-gathering-ofs-all").click(function(){
            EditSimulationFileGathering.changeOutputFileAlls();
        });
    },
    
    reset: function(){     
        this.outputFiles = null;
        this.outputFilesChoose = new Array();
        this.resetTableOutputFiles();
    },
    
    setFileGatheringFields: function(simulation_type, repetitions){
        if (EditSimulationFileGathering.outputFiles == null){
            $.ajax({
                dataType: "json",
                url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/outputfiles/",
                method: "GET",
                //data: {projectId: 1},
                success : function(data) {
                    EditSimulationFileGathering.outputFiles = new Array();
                    for(var i = 0; i < data.length; i++){
                        var output_file_data = new Object();
                        output_file_data.outputFileName = data[i].outputFileName;
                        output_file_data.outputFileStructure = new OutputFileStructure(data[i].outputFileStructure);
                        output_file_data.type = data[i].type;
                        
                        var param = new OutputFileResource(output_file_data);
                        EditSimulationFileGathering.outputFiles.push(param);
                    }

                    EditSimulationFileGathering.outputFilesChoose = new Array();
                    for(var i = 0; i < data.length; i++){
                        //show only the files of type "RESULT_FILE"!!
                        if (simulation_type == "parameter-sweep" && data[i].type == "RESULT_FILE"){
                            var output_file_data = new Object();
                            output_file_data.name = data[i].outputFileName;
                            output_file_data.chosen = false;
                            EditSimulationFileGathering.outputFilesChoose.push(output_file_data);
                        }else if (simulation_type == "single"){
                            if (data[i].type == "RESULT_FILE" || repetitions == 1){
                                var output_file_data = new Object();
                                output_file_data.name = data[i].outputFileName;
                                output_file_data.chosen = false;
                                EditSimulationFileGathering.outputFilesChoose.push(output_file_data);
                            }
                        }
                    }
                    EditSimulationFileGathering.setOutputFilesChoose(simulation_type);
                    //EditSimulationFileGathering.printTableOutputFiles();
                },
                error : function(xhr, status) {
                    //hide the dialog and show the error
                    EditSimulationModal.hideModal();
                    manageRequestError(xhr, status);
                },

                // código a ejecutar sin importar si la petición falló o no
                complete : function(xhr, status) {
                //alert(status);
                }
            });
        }else{
            //keep a copy of the previous values for the selected output files
            var oldOutputFilesChoose = new Array();
            for (var i = 0; i < this.outputFilesChoose.length; i++){
                oldOutputFilesChoose.push(this.outputFilesChoose[i]);
            }
            
            this.outputFilesChoose = new Array();
            for(var i = 0; i < this.outputFiles.length; i++){
                //show only the files of type "RESULT_FILE"!!
                if (simulation_type == "parameter-sweep" && this.outputFiles[i].getType() == "RESULT_FILE"){
                    var output_file_data = new Object();
                    output_file_data.name = this.outputFiles[i].getOutputFileName();
                    var chosen = false;
                    for (var j = 0; j < oldOutputFilesChoose.length; j++){
                        if (oldOutputFilesChoose[j].name == output_file_data.name){
                            chosen = oldOutputFilesChoose[j].chosen;
                            break;
                        }
                    }
                    output_file_data.chosen = chosen;
                    this.outputFilesChoose.push(output_file_data);
                }else if (simulation_type == "single"){
                    if (this.outputFiles[i].getType() == "RESULT_FILE" || repetitions == 1){
                        var output_file_data = new Object();
                        output_file_data.name = this.outputFiles[i].getOutputFileName();
                        var chosen = false;
                        for (var j = 0; j < oldOutputFilesChoose.length; j++){
                            if (oldOutputFilesChoose[j].name == output_file_data.name){
                                chosen = oldOutputFilesChoose[j].chosen;
                                break;
                            }
                        }
                        output_file_data.chosen = chosen;
                        this.outputFilesChoose.push(output_file_data);
                    }
                }
            }
            
             this.printTableOutputFiles();
        }
    },
    
    setOutputFilesChoose: function(simulation_type){
        if (simulation_type == "single"){
            var request_url = getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/singlesimulations/" + EditSimulationModal.simulationId + "/outputfiles/";
        }else if (simulation_type == "parameter-sweep"){
            var request_url = getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/parametersweepsimulations/" + EditSimulationModal.simulationId + "/outputfiles/";
        }
        $.ajax({
            dataType: "json",
            url: request_url,
            method: "GET",
            success : function(data) {
                for(var i = 0; i < data.length; i++){
                    var out_file_choose = EditSimulationFileGathering.getOutputFileChoose(data[i].outputFileName);
                    if (out_file_choose!=null){
                        out_file_choose.chosen = true;
                    }
                }
                EditSimulationFileGathering.printTableOutputFiles();
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                EditSimulationModal.hideModal();
                manageRequestError(xhr, status);
            },

            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
    },
    
    printTableOutputFiles: function(){
        var all_checked = true;
        var table_body = $("#edit-simulation-file-gathering-table > tbody");
        table_body.empty();
        for (var i = 0; i < this.outputFilesChoose.length; i++){
            var output_file_choose = this.outputFilesChoose[i];
            var outputFile = this.getOutputFile(output_file_choose.name);
            var table_tr = $("<tr></tr>");
            var td_chosen = $('<td><input type="checkbox" name="edit-simulation-file-gathering-ofs" id="edit-simulation-file-gathering-ofs-' + i + '" value="' + outputFile.getOutputFileName() + '" onchange="EditSimulationFileGathering.changeOutputFile(\'' + outputFile.getOutputFileName() + '\',this)" ></td>');
            var td_number = $("<td>" + (i+1) + "</td>");
            var td_name = $("<td>" + outputFile.getOutputFileName() + "</td>");
            var td_tipo = $("<td>" + OutputFileType.getDescription(outputFile.getType()) + "</td>");
            if(outputFile.getType() == "TRACE_FILE"){
                var td_vars = $("<td>-</td>");
            }else{
                var td_vars = $("<td>" + outputFile.getOutputFileStructure().getOutputVariables() + "</td>");
            }
            table_tr.append(td_chosen);
            table_tr.append(td_number);
            table_tr.append(td_name);
            table_tr.append(td_tipo);
            table_tr.append(td_vars);
            table_body.append(table_tr);
            if (output_file_choose.chosen){
                $("#edit-simulation-file-gathering-ofs-"+i).prop("checked", true);
            }else{
                all_checked = false;
            }
        }
        if (all_checked){
            $("#edit-simulation-file-gathering-ofs-all").prop("checked", true);
        }else{
            $("#edit-simulation-file-gathering-ofs-all").prop("checked", false);
        }
    },
    
    resetTableOutputFiles: function(){
        var table_body = $("#edit-simulation-file-gathering-table > tbody");
        table_body.empty();
    },
     
    changeOutputFile: function(name, input){
        var output_file = this.getOutputFileChoose(name);
        if ($(input).is(":checked")){
            output_file.chosen = true;
        }else{
            output_file.chosen = false;
        }
        //if all the checkbox are checked, we also check the checker for all of them
        if ($('input[type="checkbox"][name="edit-simulation-file-gathering-ofs"]:checked').length == this.outputFilesChoose.length){
            $("#edit-simulation-file-gathering-ofs-all").prop("checked",true);
        }else{
            $("#edit-simulation-file-gathering-ofs-all").prop("checked",false);
        }
    },
    
    changeOutputFileAlls: function(){
        if ($("#edit-simulation-file-gathering-ofs-all").is(":checked")){
            $("#edit-simulation-file-gathering-table input[type='checkbox']").each(function(){
                $(this).prop("checked", true);
                for (var i = 0; i < EditSimulationFileGathering.outputFilesChoose.length; i++){
                    EditSimulationFileGathering.outputFilesChoose[i].chosen = true;
                }
            });
        }else{
            $("#edit-simulation-file-gathering-table input[type='checkbox']").each(function(){
                $(this).prop("checked", false);
                for (var i = 0; i < EditSimulationFileGathering.outputFilesChoose.length; i++){
                    EditSimulationFileGathering.outputFilesChoose[i].chosen = false;
                }
            });
        }
    },
    
    checkFileGatheringFields: function(){
        var errors = false;
        var number_chosen = 0;
        for (var i = 0; i < this.outputFilesChoose.length; i++){
            if (this.outputFilesChoose[i].chosen){
                number_chosen++;
            }
        }
        if (number_chosen == 0){
            errors = true;
        }
        if (errors){
            $("#alert-danger-panel-edit-output-files").removeClass("hidden");
            $("#alert-danger-panel-edit-output-files").html("Selecciona al menos un fichero de salida");
        }else{
            $("#alert-danger-panel-edit-output-files").addClass("hidden");
        }
        return errors;
    },

    getOutputFile: function(name){
        for (var i = 0; i < this.outputFiles.length; i++){
            var out_file = this.outputFiles[i];
            if (out_file.getOutputFileName()== name){
                return out_file;
            }
        }
        return null;
    },
    
    getOutputFileChoose: function(name){
        for (var i = 0; i < this.outputFilesChoose.length; i++){
            var out_file = this.outputFilesChoose[i];
            if (out_file.name == name){
                return out_file;
            }
        }
        return null;
    }
}

