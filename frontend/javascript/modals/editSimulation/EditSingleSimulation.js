/**
 * Manager for the modal dialog that creates a new single simulation
 */
var EditSingleSimulation = {
    
    /**
     * the parameter values for the single simulation that is being edited
     */
    singleSimulationParameters: null,
    
    init: function(){
  
    },
    
    reset: function(){
        this.singleSimulationParameters = null;
        this.resetTableIndividualSimulationParameters();
    },
    
    resetTableIndividualSimulationParameters: function(){
        var table_body = $("#edit-simulation-parameters-single-table > tbody");
        table_body.empty();
    },
    
    printTableIndividualSimulationParameters: function(){
        var table_body = $("#edit-simulation-parameters-single-table > tbody");
        table_body.empty();
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var ss_param = this.singleSimulationParameters[i];
            var param = EditSimulationModal.getParameter(ss_param.getName());
            var def_value = "random";
            if (typeof param !='undefined' && param.getDefaultValue()!=null){
                def_value = param.getDefaultValue();
            }
            var table_tr = $("<tr></tr>");
            var td_number = $("<td>" + (i+1) + "</td>");
            var td_name = $("<td>" + ss_param.getName() + "</td>");
            var td_type = $("<td>" + ParameterType.getDescription(param.getType()) + "</td>");
            var td_value = $('<td><div id="form-group-esp-value-' + i + '" class="form-group"><input type="text" class="form-control" id="edit-simulation-esp-value-' + i + '" placeholder="' + def_value + '" value="' + ss_param.getValueDnse3Format("single") + '" onchange="EditSingleSimulation.changeIndividualSimulationParameter(\'' + ss_param.getName() + '\',this)" ></div></td>');
            table_tr.append(td_number);
            table_tr.append(td_name);
            table_tr.append(td_type);
            table_tr.append(td_value);
            table_body.append(table_tr);
            /*if (param.getType()=="SEED"){
                //it is a random value, so we disable the input field
                $("#edit-simulation-esp-value-" + i).prop("disabled", true);
            }*/
        }
    },
    
    setSingleSimulationFields: function(){
        if (EditSingleSimulation.singleSimulationParameters == null){
            $.ajax({
                dataType: "json",
                url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/parameters/",
                method: "GET",
                //data: {projectId: 1},
                success : function(data) {
                    //save the simulation parameters that are available for the simulation
                    EditSimulationModal.parameters = new Array();
                    for(var i = 0; i < data.length; i++){
                        var param = new Parameter(data[i]);
                        EditSimulationModal.parameters.push(param);
                    }
                    EditSingleSimulation.setSingleSimulationParameterResources();
                    //EditSingleSimulation.printTableIndividualSimulationParameters();                   
                },
                error : function(xhr, status) {
                    //hide the dialog and show the error
                    EditSimulationModal.hideModal();
                    //manageRequestError(xhr, status);
                    ErrorModal.errorMessage(" Carga parameters", "Se ha producido al cargar los parámetros de la simulación individual.");
                },

                // código a ejecutar sin importar si la petición falló o no
                complete : function(xhr, status) {
                //alert(status);
                }
            });
        }else{
            EditSingleSimulation.printTableIndividualSimulationParameters();
        }
    },
    
    /**
     * get the current values for the parameters of the single simulation
     * that is being edited and set the fields with that values
     */
    setSingleSimulationParameterResources: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + G_VARS["projectId"] + "/singlesimulations/" + EditSimulationModal.simulationId + "/parameters/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                EditSingleSimulation.singleSimulationParameters = new Array();
                for(var i = 0; i < data.length; i++){
                    var parameter_data = data[i];
                    var param = new ParameterResource(parameter_data);
                    EditSingleSimulation.singleSimulationParameters.push(param);
                }
                EditSingleSimulation.printTableIndividualSimulationParameters();
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                EditSimulationModal.hideModal();
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Carga ResourceParameters", "Se ha producido al cargar los ResourceParameters de la simulación individual.");
            },

            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
    },
    
    checkIndividualSimulationParameters: function(){
        var errors = false;
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var single_sim_param = this.singleSimulationParameters[i];
            var value = $("#edit-simulation-esp-value-" + i).val();
            value = value.replace(new RegExp(' ', 'g'), '');//remove the black spaces
            if (value == ""){
                $("#form-group-esp-value-" + i).addClass("has-error");
                if (!errors){
                    $("#edit-simulation-esp-value-" + i).focus();
                }
                errors = true;
            }else{
                //check the value type
                var ss_param = EditSimulationModal.getParameter(single_sim_param.getName());
                var type = ss_param.getType();
                var type_error = false;
                switch(type){
                    case "STRING_VALUE":
                        //check it is a string and has only one value (the comma is the value separator)
                        if ( (typeof value != 'string' && !(value instanceof String)) || value.indexOf(",")!=-1 || value.indexOf(" ")!=-1){
                            type_error = true;
                        }else{
                            var possible_values = ss_param.getPossibleValues();
                            //check if the value is in the list on possible accepted values
                            if (typeof possible_values !='undefined' && possible_values!="" && possible_values.indexOf(value)==-1){
                                type_error = true;
                            }
                        }
                        break;                    
                    case "INTEGER_VALUE":
                        if (Math.floor(value) != value || !$.isNumeric(value) || value.indexOf(",")!=-1 || value.indexOf(" ")!=-1){
                            type_error = true;
                        }else{
                            if (typeof ss_param.getGreaterThan()!='undefined' &&  parseInt(value,10) <= ss_param.getGreaterThan()){
                                type_error = true;
                            }
                            if (typeof ss_param.getLessThan()!='undefined' &&  parseInt(value,10) >= ss_param.getLessThan()){
                                type_error = true;
                            }
                            if (typeof ss_param.getGreaterThanOrEqualTo()!='undefined' &&  parseInt(value,10) < ss_param.getGreaterThanOrEqualTo()){
                                type_error = true;
                            }
                            if (typeof ss_param.getLessThanOrEqualTo()!='undefined' &&  parseInt(value,10) > ss_param.getLessThanOrEqualTo()){
                                type_error = true;
                            } 
                        }
                        break;
                    case "RATIONAL_VALUE":
                        if (!$.isNumeric(value) || value.indexOf(",")!=-1 || value.indexOf(" ")!=-1){
                            type_error = true;
                        }else{
                            if (typeof ss_param.getGreaterThan()!='undefined' &&  parseFloat(value) <= ss_param.getGreaterThan()){
                                type_error = true;
                            }
                            if (typeof ss_param.getLessThan()!='undefined' &&  parseFloat(value) >= ss_param.getLessThan()){
                                type_error = true;
                            }
                            if (typeof ss_param.getGreaterThanOrEqualTo()!='undefined' &&  parseFloat(value) < ss_param.getGreaterThanOrEqualTo()){
                                type_error = true;
                            }
                            if (typeof ss_param.getLessThanOrEqualTo()!='undefined' &&  parseFloat(value) > ss_param.getLessThanOrEqualTo()){
                                type_error = true;
                            } 
                        }
                }
                if (type_error){
                    $("#form-group-esp-value-" + i).addClass("has-error");
                    if (!errors){
                        $("#edit-simulation-esp-value-" + i).focus();
                    }
                    errors = true;
                }else{
                    $("#form-group-esp-value-" + i).removeClass("has-error");
                }
            }
        }    
        if (errors){
            $("#alert-danger-panel-edit-parameters-single").removeClass("hidden");
            $("#alert-danger-panel-edit-parameters-single").html("Comprueba los campos marcados en rojo");
        }else{
            $("#alert-danger-panel-edit-parameters-single").addClass("hidden");
        }
        return errors;
    },
    
    changeIndividualSimulationParameter: function(name, input){
        var ss_param = this.getSingleSimulationParameter(name);
        var value = $(input).val();
        ss_param.setValueDnse3Format(value,"single");
    },
        
    getSingleSimulationParameter: function(name){
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var ss_param = this.singleSimulationParameters[i];
            if (ss_param.getName() == name){
                return ss_param;
            }
        }
        return null;
    }
    
}


