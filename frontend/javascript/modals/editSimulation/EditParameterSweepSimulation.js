/**
 * Manager for the modal dialog that creates a new parameter sweep simulation
 */
var EditParameterSweepSimulation = {
    
    /**
     * the parameters that the user has added to the sweep simulation from those available
     */
    sweepSimulationParameters: null,
    
    init: function(){
        
    },
    
    reset: function(){
        this.sweepSimulationParameters = null;
        this.resetTableParameterSweepSimulationParameters();
    },
    
    resetTableParameterSweepSimulationParameters: function(){
        var table_body = $("#edit-simulation-parameters-sweep-table > tbody");
        table_body.empty();
    },
    
    printTableParameterSweepSimulationParameters: function(){
        var table_body = $("#edit-simulation-parameters-sweep-table > tbody");
        table_body.empty();
        for (var i = 0; i < this.sweepSimulationParameters.length; i++){
            var sweep_sim_param = this.sweepSimulationParameters[i];
            var param = EditSimulationModal.getParameter(sweep_sim_param.getName());
            var def_value = "random";
            if (param.getDefaultValue()!=null){
                def_value = param.getDefaultValue();
            }
            var table_tr = $("<tr></tr>");
            var td_number = $("<td>" + (i+1) + "</td>");
            var td_name = $("<td>" + sweep_sim_param.getName() + "</td>");
            var td_type = $("<td>" + ParameterType.getDescription(param.getType()) + "</td>");
            var td_value = $('<td><div id="form-group-esp-sweep-value-' + i + '" class="form-group"><input type="text" class="form-control" id="edit-simulation-esp-sweep-value-' + i + '" placeholder="' + def_value + '" value="' + sweep_sim_param.getValueDnse3Format("sweep") + '" onchange="EditParameterSweepSimulation.changeParameterSweepSimulationParameter(\'' + sweep_sim_param.getName() + '\',this)" ></div></td>');
            table_tr.append(td_number);
            table_tr.append(td_name);
            table_tr.append(td_type);
            table_tr.append(td_value);
            table_body.append(table_tr);
            /*if (param.getType()=="SEED"){
                //it is a random value, so we disable the input field
                $("#edit-simulation-esp-sweep-value-" + i).prop("disabled", true);
            }*/
        }
    },
       
    setParameterSweepSimulationFields: function(){
        if (EditParameterSweepSimulation.sweepSimulationParameters == null){
            $.ajax({
                dataType: "json",
                url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/parameters/",
                method: "GET",
                success : function(data) {
                    //save the simulation parameters that are available for the simulation
                    EditSimulationModal.parameters = new Array();
                    for(var i = 0; i < data.length; i++){
                        var param = new Parameter(data[i]);
                        EditSimulationModal.parameters.push(param);
                    }
                    EditParameterSweepSimulation.setParameterSweepParameterResources();
                    //EditParameterSweepSimulation.printTableParameterSweepSimulationParameters();
                },
                error : function(xhr, status) {
                    //hide the dialog and show the error
                    EditSimulationModal.hideModal();
                    //manageRequestError(xhr, status);
                    ErrorModal.errorMessage(" Parameters", "Error al intentar cargar los parámetros del barrido.");
                },

                // código a ejecutar sin importar si la petición falló o no
                complete : function(xhr, status) {
                //alert(status);
                }
            });
        }else{
            EditParameterSweepSimulation.printTableParameterSweepSimulationParameters();
        }
    },
    
    /**
     * get the current values for the parameters of the parameter sweep simulation
     * that is being edited and set the fields with that values
     */
    setParameterSweepParameterResources: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/parametersweepsimulations/" + EditSimulationModal.simulationId + "/parameters/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {                
                EditParameterSweepSimulation.sweepSimulationParameters = new Array();
                for(var i = 0; i < data.length; i++){
                    var parameter_data = data[i];
                    var param = new ParameterResource(parameter_data);
                    EditParameterSweepSimulation.sweepSimulationParameters.push(param);
                }
                EditParameterSweepSimulation.printTableParameterSweepSimulationParameters();
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                EditSimulationModal.hideModal();
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" ParameterResource", "Error en la obtención de ParameterResource.");
            },

            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
    },
    
    checkSweepSimulationParameters: function(){
        var errors = false;
        for (var i = 0; i < this.sweepSimulationParameters.length; i++){
            var sweep_sim_param_res = this.sweepSimulationParameters[i];
            var value = $("#edit-simulation-esp-sweep-value-" + i).val();
            //value = value.replace(" ", "");
            value = value.replace(new RegExp(' ', 'g'), '');//remove the black spaces
            if (value == ""){//we don't let more blank spaces
                $("#form-group-esp-sweep-value-" + i).addClass("has-error");
                if (!errors){
                    $("#edit-simulation-esp-sweep-value-" + i).focus();
                }
                errors = true;
            }else{
                //check the value type
                var sweep_sim_param = EditSimulationModal.getParameter(sweep_sim_param_res.getName());
                var type = sweep_sim_param.getType();
                var type_error = false;
                switch(type){
                    case "STRING_VALUE":
                        var possible_values = sweep_sim_param.getPossibleValues();
                        if (value.indexOf(":")!=-1){
                            type_error = true;
                        }else{
                            var entered_values = value.split(",");
                            for (var j = 0; j < entered_values.length; j++){
                                var entered_value = entered_values[j];
                                //for each value check if it is a string
                                if (typeof entered_value != 'string' && !(entered_value instanceof String)){
                                    type_error = true;
                                }else{
                                    //check if the value is in the list on possible accepted values
                                    if (typeof possible_values !='undefined' && possible_values!="" && possible_values.indexOf(entered_value)==-1){
                                        type_error = true;
                                    }
                                }
                            }
                        }
                        break;                    
                    case "INTEGER_VALUE":
                        if (value.indexOf(":")!=-1){
                            var range = value.split(":"); 
                            //min:step:max
                            if (range.length!=3){
                                type_error = true;
                            }else{
                                var min = range[0];
                                var step = range[1];
                                var max = range[2];
                                if ( (Math.floor(min) != min || !$.isNumeric(min)) || (Math.floor(step) != step || !$.isNumeric(step)) || (Math.floor(max) != max || !$.isNumeric(max))){
                                    type_error = true;
                                }else if (parseInt(min,10) > parseInt(max,10) || parseInt(step,10)<=0) {
                                    type_error = true;
                                }else{
                                    if (typeof sweep_sim_param.getGreaterThan()!='undefined' &&  parseInt(min,10) <= sweep_sim_param.getGreaterThan()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getLessThan()!='undefined' &&  parseInt(max,10) >= sweep_sim_param.getLessThan()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getGreaterThanOrEqualTo()!='undefined' &&  parseInt(min,10) < sweep_sim_param.getGreaterThanOrEqualTo()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getLessThanOrEqualTo()!='undefined' &&  parseInt(max,10) > sweep_sim_param.getLessThanOrEqualTo()){
                                        type_error = true;
                                    } 
                                }
                            }
                        }else{
                            var entered_values = value.split(",");
                            for (var j = 0; j < entered_values.length; j++){
                                var entered_value = entered_values[j];
                                //for each value check if it is an integer
                                if (Math.floor(entered_value) != entered_value || !$.isNumeric(entered_value)){
                                    type_error = true;
                                }else{
                                    if (typeof sweep_sim_param.getGreaterThan()!='undefined' &&  parseInt(entered_value) <= sweep_sim_param.getGreaterThan()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getLessThan()!='undefined' &&  parseInt(entered_value) >= sweep_sim_param.getLessThan()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getGreaterThanOrEqualTo()!='undefined' &&  parseInt(entered_value) < sweep_sim_param.getGreaterThanOrEqualTo()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getLessThanOrEqualTo()!='undefined' &&  parseInt(entered_value) > sweep_sim_param.getLessThanOrEqualTo()){
                                        type_error = true;
                                    } 
                                }
                            }                            
                        }
                        break;
                    case "RATIONAL_VALUE":
                        if (value.indexOf(":")!=-1){
                            var range = value.split(":"); 
                            //min:step:max
                            if (range.length!=3){
                                type_error = true;
                            }else{
                                var min = range[0];
                                var step = range[1];
                                var max = range[2];
                                if ( !$.isNumeric(min) || !$.isNumeric(step) || !$.isNumeric(max)){
                                    type_error = true;
                                }else if (parseFloat(min) > parseFloat(max) || parseFloat(step)<=0) {
                                    type_error = true;
                                }else{
                                    if (typeof sweep_sim_param.getGreaterThan()!='undefined' &&  parseFloat(min) <= sweep_sim_param.getGreaterThan()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getLessThan()!='undefined' &&  parseFloat(max) >= sweep_sim_param.getLessThan()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getGreaterThanOrEqualTo()!='undefined' &&  parseFloat(min) < sweep_sim_param.getGreaterThanOrEqualTo()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getLessThanOrEqualTo()!='undefined' &&  parseFloat(max) > sweep_sim_param.getLessThanOrEqualTo()){
                                        type_error = true;
                                    } 
                                }
                            }
                        }else{
                            var entered_values = value.split(",");
                            for (var j = 0; j < entered_values.length; j++){
                                var entered_value = entered_values[j];
                                //for each value check if it is a numeric value
                                if (!$.isNumeric(entered_value)){
                                    type_error = true;
                                }else{
                                    if (typeof sweep_sim_param.getGreaterThan()!='undefined' &&  parseFloat(entered_value) <= sweep_sim_param.getGreaterThan()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getLessThan()!='undefined' && parseFloat(entered_value) >= sweep_sim_param.getLessThan()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getGreaterThanOrEqualTo()!='undefined' &&  parseFloat(entered_value) < sweep_sim_param.getGreaterThanOrEqualTo()){
                                        type_error = true;
                                    }
                                    if (typeof sweep_sim_param.getLessThanOrEqualTo()!='undefined' &&  parseFloat(entered_value) > sweep_sim_param.getLessThanOrEqualTo()){
                                        type_error = true;
                                    } 
                                }
                            }                            
                        }
                        break;
                }
                if (type_error){
                    $("#form-group-esp-sweep-value-" + i).addClass("has-error");
                    if (!errors){
                        $("#edit-simulation-esp-sweep-value-" + i).focus();
                    }
                    errors = true;
                }else{
                    $("#form-group-esp-sweep-value-" + i).removeClass("has-error");
                }
            }
        }    
        if (errors){
            $("#alert-danger-panel-edit-parameters-sweep").removeClass("hidden");
            $("#alert-danger-panel-edit-parameters-sweep").html("Comprueba los campos marcados en rojo");
        }else{
            $("#alert-danger-panel-edit-parameters-sweep").addClass("hidden");
        }
        return errors;
    },
    
    changeParameterSweepSimulationParameter: function(name, input){
        var sweep_sim_param = this.getSweepSimulationParameter(name);
        var value = $(input).val();
        sweep_sim_param.setValueDnse3Format(value,"sweep");
    },
    
    getSweepSimulationParameter: function(name){
        for (var i = 0; i < this.sweepSimulationParameters.length; i++){
            var sweep_sim_param = this.sweepSimulationParameters[i];
            if (sweep_sim_param.getName() == name){
                return sweep_sim_param;
            }
        }
        return null;
    }
}


