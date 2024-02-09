/**
 * Manager for the modal dialog that creates a new single simulation
 */
var NewSingleSimulation = {
    
    /**
     * the parameters that the user has added to the single simulation from those available
     */
    singleSimulationParameters: null,
    
    /**
     * the name of the parameter that is being updated in a single simulation
     */
    singleSimulationUpdateParameterName: null,
    
    init: function(){
        
        $("#new-simulation-nsp-value-type").change(function(){
            $("#new-simulation-nsp-value").val("");
            if ($("#new-simulation-nsp-value-type").val()=="fixed"){
                $("#form-group-nsp-value").show();
            }else{
                $("#form-group-nsp-value").hide();
            }
        });
        
        //onclick event on the add parameter button
        $("#add-parameter-single").click(function(){
            NewSingleSimulation.setAddIndividualSimulationParameter();
        });
        
        //onclick event on the close parameter button
        $("#close-parameter-single").click(function(){
            NewSingleSimulation.closeIndividualSimulationParameter();
        });
        
        //onclick event on the update parameter button
        $("#update-parameter-single").click(function(){
            NewSingleSimulation.updateIndividualSimulationParameter();
        });
        
        //onclick event on the save parameter button
        $("#save-parameter-single").click(function(){
            NewSingleSimulation.saveIndividualSimulationParameter();
        });        
    },
    
    reset: function(){
        this.singleSimulationParameters = null;
        this.resetTableIndividualSimulationParameters();
    },
    
    getIndividualSimulationParameters: function(){
        $.ajax({
            dataType: "json",
            url: getBaseApiUrl() + "/users/username/projects/" + NewSimulationModal.projectId + "/parameters/",
            method: "GET",
            //data: {projectId: 1},
            success : function(data) {
                NewSimulationModal.parameters = new Array();
                for(var i = 0; i < data.length; i++){
                    var param = new Parameter(data[i]);
                    NewSimulationModal.parameters.push(param);
                }
                NewSingleSimulation.fillIndividualSimulationParameterNameSelector(true);
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                NewSimulationModal.hideModal();
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Carga Parameters", "Se ha producido al cargar los parámetros de la simulación individual.");
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
    },
    
    /*printTableIndividualSimulationParameters: function(){
        var table_body = $("#new-simulation-parameters-single-table > tbody");
        table_body.empty();
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var param = this.singleSimulationParameters[i];
            var table_tr = $("<tr></tr>");
            var td_number = $("<td>" + (i+1) + "</td>");
            var td_name = $("<td>" + param.getName() + "</td>");
            var td_actions = $('<td>' +
                                    '<button onclick="NewSingleSimulation.setViewIndividualSimulationParameter(\'' + param.getName() + '\')" class="btn btn-success btn-xs"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver </button> ' +
                                    '<button onclick="NewSingleSimulation.setEditIndividualSimulationParameter(\'' + param.getName() + '\')" class="btn btn-info btn-xs"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar </button> ' +
                                    '<button onclick="NewSingleSimulation.deleteIndividualSimulationParameter(\'' + param.getName() + '\')" class="btn btn-danger btn-xs" ><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar </a>' +
                              '</td>');
            table_tr.append(td_number);
            table_tr.append(td_name);
            table_tr.append(td_actions);
            table_body.append(table_tr);
        }
    },*/
    
    resetTableIndividualSimulationParameters: function(){
        var table_body = $("#new-simulation-parameters-single-table > tbody");
        table_body.empty();
    },
    
    printTableIndividualSimulationParameters: function(){
        var table_body = $("#new-simulation-parameters-single-table > tbody");
        table_body.empty();
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var ss_param = this.singleSimulationParameters[i];
            var param = NewSimulationModal.getParameter(ss_param.getName());
            var def_value = "random";
            if (typeof param !='undefined' && param.getDefaultValue()!=null){
                def_value = param.getDefaultValue();
            }
            var table_tr = $("<tr></tr>");
            var td_number = $("<td>" + (i+1) + "</td>");
            var td_name = $("<td>" + ss_param.getName() + "</td>");
            var td_type = $("<td>" + ParameterType.getDescription(param.getType()) + "</td>");
            var td_value = $('<td><div id="form-group-nsp-value-' + i + '" class="form-group"><input type="text" class="form-control" id="new-simulation-nsp-value-' + i + '" placeholder="' + def_value + '" value="' + ss_param.getValueDnse3Format("single") + '" onchange="NewSingleSimulation.changeIndividualSimulationParameter(\'' + ss_param.getName() + '\',this)" ></div></td>');
            table_tr.append(td_number);
            table_tr.append(td_name);
            table_tr.append(td_type);
            table_tr.append(td_value);
            table_body.append(table_tr);
            /*if (param.getType()=="SEED"){
                //it is a random value, so we disable the input field
                $("#new-simulation-nsp-value-" + i).prop("disabled", true);
            }*/
        }
    },
    
    setViewIndividualSimulationParameter: function(name){
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var param = this.singleSimulationParameters[i];
            if (param.getName()==name){
                this.fillIndividualSimulationParameterNameSelector(false);
                $("#new-simulation-ps-name").val(param.getName());
                $("#new-simulation-nsp-value-type").val(param.getValueType());
                if (param.getValueType() == "fixed"){
                    $("#new-simulation-nsp-value").val(param.getValue());
                }
                break;
            }         
        }
        //set the title
        $("#panel-add-parameter-single-title").html("Ver parámetro: " + name);

        //hide the errors
        $("#form-new-simulation-parameters-single .has-error").each(function(){
            $(this).removeClass("has-error");
        });
        $("#alert-danger-panel-add-parameter-single").addClass("hidden");
        
        //disable the form elements
        $("#form-new-simulation-parameters-single select").each(function(){
            $(this).prop("disabled", true);
        });
        $("#form-new-simulation-parameters-single input").each(function(){
            $(this).prop("disabled", true);
        });
        
        //show or hide the inputs that depend on the value type
        if ($("#new-simulation-nsp-value-type").val()=="fixed"){
            $("#form-group-nsp-value").show();
        }else{
            $("#form-group-nsp-value").hide();
        }
        
        //show the right buttons
        $("#close-parameter-single").removeClass("hidden");
        $("#update-parameter-single").addClass("hidden");
        $("#save-parameter-single").addClass("hidden");
        
        //show the form
        $("#panel-add-parameter-single").slideDown("slow");
    },
    
    setEditIndividualSimulationParameter: function(name){
        this.singleSimulationUpdateParameterName = name;
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var param = this.singleSimulationParameters[i];
            if (param.getName()==name){
                this.fillUpdateIndividualSimulationParameterNameSelector(param);
                $("#new-simulation-ps-name").val(param.getName());
                $("#new-simulation-nsp-value-type").val(param.getValueType());
                if (param.getValueType() == "fixed"){
                    $("#new-simulation-nsp-value").val(param.getValue());
                }
                break;
            }         
        }
        //set the title
        $("#panel-add-parameter-single-title").html("Editar parámetro: " + name);        
        
        //hide the errors
        $("#form-new-simulation-parameters-single .has-error").each(function(){
            $(this).removeClass("has-error");
        });
        $("#alert-danger-panel-add-parameter-single").addClass("hidden");
        
        //enable the form elements
        $("#form-new-simulation-parameters-single select").each(function(){
            $(this).prop("disabled", false);
        });
        $("#form-new-simulation-parameters-single input").each(function(){
            $(this).prop("disabled", false);
        });
        
        //show or hide the inputs that depend on the value type
        if ($("#new-simulation-nsp-value-type").val()=="fixed"){
            $("#form-group-nsp-value").show();
        }else{
            $("#form-group-nsp-value").hide();
        }
        
        //show the right buttons
        $("#close-parameter-single").removeClass("hidden");
        $("#update-parameter-single").removeClass("hidden");
        $("#save-parameter-single").addClass("hidden");
        
        //show the form
        $("#panel-add-parameter-single").slideDown("slow");
    },
    
    setSingleSimulationFields: function(){
        if (NewSingleSimulation.singleSimulationParameters == null){
            $.ajax({
                dataType: "json",
                url: getBaseApiUrl() + "/users/username/projects/" + NewSimulationModal.projectId + "/parameters/",
                method: "GET",
                //data: {projectId: 1},
                success : function(data) {
                    //save the simulation parameters that are available for the simulation
                    NewSimulationModal.parameters = new Array();
                    for(var i = 0; i < data.length; i++){
                        var param = new Parameter(data[i]);
                        NewSimulationModal.parameters.push(param);
                    }

                    //set the initial value for each single simulation parameter from their default value
                    NewSingleSimulation.singleSimulationParameters = new Array();
                    for(var i = 0; i < data.length; i++){
                        var parameter_data = new Object();
                        parameter_data.name = data[i].name;
                        if (data[i].type == "SEED"){
                            parameter_data.value = "random";//it is a reserved word
                        }else{
                            parameter_data.value = data[i].defaultValue;
                        }
                        if (parameter_data.value == "random"){
                            parameter_data.random = true;
                        }else{
                            parameter_data.random = false;
                        }
                        var param = new ParameterResource(parameter_data);
                        param.setValueDnse3Format(parameter_data.value,"single");
                        NewSingleSimulation.singleSimulationParameters.push(param);
                    }
                    NewSingleSimulation.printTableIndividualSimulationParameters();
                },
                error : function(xhr, status) {
                    //hide the dialog and show the error
                    NewSimulationModal.hideModal();
                    ErrorModal.errorMessage(" Carga valores parameters", "Se ha producido al cargar los valores de los parámetros de la simulación individual.");
                    //manageRequestError(xhr, status);
                },

                // código a ejecutar sin importar si la petición falló o no
                complete : function(xhr, status) {
                //alert(status);
                }
            });
        }else{
            NewSingleSimulation.printTableIndividualSimulationParameters();
        }
    },
    
    setAddIndividualSimulationParameter: function(){
        if (NewSimulationModal.parameters == null){
            this.getIndividualSimulationParameters();
        }else{
            this.fillIndividualSimulationParameterNameSelector(true);
        }
        $("#new-simulation-nsp-value-type option:selected").prop("selected", false);
        $("#new-simulation-nsp-value").val("");
        //hide the subsections that depend on the value type
        $("#form-group-nsp-value").hide();
        
        //set the title
        $("#panel-add-parameter-single-title").html("Nuevo parámetro");
        
        //hide the errors
        $("#form-new-simulation-parameters-single .has-error").each(function(){
            $(this).removeClass("has-error");
        });
        $("#alert-danger-panel-add-parameter-single").addClass("hidden");
        
        //enable the form elements
        $("#form-new-simulation-parameters-single select").each(function(){
            $(this).prop("disabled", false);
        });
        $("#form-new-simulation-parameters-single input").each(function(){
            $(this).prop("disabled", false);
        });
        
        //show the right buttons
        //$("#close-parameter-single").addClass("hidden");
        $("#close-parameter-single").removeClass("hidden");
        $("#update-parameter-single").addClass("hidden");
        $("#save-parameter-single").removeClass("hidden");
        
        //show the form
        $("#panel-add-parameter-single").slideDown("slow");
    },
    
    deleteIndividualSimulationParameter: function(name){
        //search and delete the parameter
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var ss_param = this.singleSimulationParameters[i];
            if (ss_param.getName()==name){
                this.singleSimulationParameters.splice(i,1);
                break;
            }
        }
        //hide the form for the parameter
        $("#panel-add-parameter-single").slideUp("slow");
        //refresh the table not to show the deleted parameter
        this.printTableIndividualSimulationParameters();
        //enable the new parameter button if now there are available parameters to be added
        if (this.singleSimulationParameters.length < NewSimulationModal.parameters.length){
            $("#add-parameter-single").prop("disabled", false);
        }
    },
    
    fillIndividualSimulationParameterNameSelector: function(only_available){
        $("#new-simulation-ps-name").empty();
        $('<option value="">-- Selecciona el nombre del parámetro --</option>').appendTo("#new-simulation-ps-name");
        for(var i = 0; i < NewSimulationModal.parameters.length; i++){
            var param = NewSimulationModal.parameters[i];
            if (only_available){
                //check if the parameter has not been added before to the simulation
                var available = true;
                for (var j = 0; j < this.singleSimulationParameters.length; j++){
                    var ss_param = this.singleSimulationParameters[j];
                    if (param.getName() == ss_param.getName()){
                        available = false;
                        break;
                    }
                }
                if (available){
                    $('<option value="' + param.getName() + '">' + param.getName() + '</option>').appendTo("#new-simulation-ps-name");
                }
            }else{
                $('<option value="' + param.getName() + '">' + param.getName() + '</option>').appendTo("#new-simulation-ps-name");
            }
        }
        $("#new-simulation-ps-name").val("");
    },
    
    fillUpdateIndividualSimulationParameterNameSelector: function(param){
        this.fillIndividualSimulationParameterNameSelector(true);
        $('<option value="' + param.getName() + '">' + param.getName() + '</option>').appendTo("#new-simulation-ps-name");
    },    
    
    
    /**
     * close view a parameter for an individual simulation
     */
    closeIndividualSimulationParameter: function(){
        //hide the form for the parameter
        $("#panel-add-parameter-single").slideUp("slow");
    },
    
    checkIndividualSimulationParameters: function(){
        var errors = false;
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var single_sim_param = this.singleSimulationParameters[i];
            var value = $("#new-simulation-nsp-value-" + i).val();
            value = value.replace(new RegExp(' ', 'g'), '');//remove the black spaces
            if (value == ""){
                $("#form-group-nsp-value-" + i).addClass("has-error");
                if (!errors){
                    $("#new-simulation-nsp-value-" + i).focus();
                }
                errors = true;
            }else{
                //check the value type
                var ss_param = NewSimulationModal.getParameter(single_sim_param.getName());
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
                    $("#form-group-nsp-value-" + i).addClass("has-error");
                    if (!errors){
                        $("#new-simulation-nsp-value-" + i).focus();
                    }
                    errors = true;
                }else{
                    $("#form-group-nsp-value-" + i).removeClass("has-error");
                }
            }
        }    
        if (errors){
            $("#alert-danger-panel-parameters-single").removeClass("hidden");
            $("#alert-danger-panel-parameters-single").html("Comprueba los campos marcados en rojo");
        }else{
            $("#alert-danger-panel-parameters-single").addClass("hidden");
        }
        return errors;
    },
    
    checkIndividualSimulationParameter: function(){
        var errors = false;
        
        var name = $("#new-simulation-ps-name").val();
        if (name == ""){
            $("#form-group-nsp-name").addClass("has-error");
            $('#new-simulation-ps-name').focus();
            errors = true;
        }else{
            $("#form-group-nsp-name").removeClass("has-error");
        }
        
        var value_type = $("#new-simulation-nsp-value-type").val();
        if (value_type == ""){
            $("#form-group-nsp-value-type").addClass("has-error");
            if(!errors){
                $('#new-simulation-nsp-value-type').focus();
            }
            errors = true;
        }else{
            $("#form-group-nsp-value-type").removeClass("has-error");
        }
        
        if (value_type == "fixed"){
            var value = $("#new-simulation-nsp-value").val();
            if (value == ""){
                $("#form-group-nsp-value").addClass("has-error");
                if (!errors){
                    $("#new-simulation-nsp-value").focus();
                }
                errors = true;
            }else{
                //check the value type
                var ss_param = NewSimulationModal.getParameter(name);
                var type = ss_param.getType();
                var type_error = false;
                switch(type){
                    case "STRING_VALUE":
                        if (typeof value != 'string' && !(value instanceof String)){
                            type_error = true;
                        }else{
                            var possible_values = ss_param.getPossibleValues();
                            //check if the value is in the list on possible accepted values
                            if (possible_values !=null && possible_values!="" && possible_values.indexOf(value)==-1){
                                type_error = true;
                            }
                        }
                        break;                    
                    case "INTEGER_VALUE":
                        if (Math.floor(value) != value || !$.isNumeric(value)){
                            type_error = true;
                        }else{
                            if (ss_param.getGreaterThan()!=null &&  value <= ss_param.getGreaterThan()){
                                type_error = true;
                            }
                            if (ss_param.getLessThan()!=null &&  value >= ss_param.getLessThan()){
                                type_error = true;
                            }
                            if (ss_param.getGreaterThanOrEqualTo()!=null &&  value < ss_param.getGreaterThanOrEqualTo()){
                                type_error = true;
                            }
                            if (ss_param.getLessThanOrEqualTo()!=null &&  value > ss_param.getLessThanOrEqualTo()){
                                type_error = true;
                            } 
                        }
                        break;
                    case "RATIONAL_VALUE":
                        if (!$.isNumeric(value)){
                            type_error = true;
                        }else{
                            if (ss_param.getGreaterThan()!=null &&  value <= ss_param.getGreaterThan()){
                                type_error = true;
                            }
                            if (ss_param.getLessThan()!=null &&  value >= ss_param.getLessThan()){
                                type_error = true;
                            }
                            if (ss_param.getGreaterThanOrEqualTo()!=null &&  value < ss_param.getGreaterThanOrEqualTo()){
                                type_error = true;
                            }
                            if (ss_param.getLessThanOrEqualTo()!=null &&  value > ss_param.getLessThanOrEqualTo()){
                                type_error = true;
                            } 
                        }
                }
                if (type_error){
                    $("#form-group-nsp-value").addClass("has-error");
                    if (!errors){
                        $("#new-simulation-nsp-value").focus();
                    }
                    errors = true;
                }else{
                    $("#form-group-nsp-value").removeClass("has-error");
                }
            }
        }
        
        if (errors){
            $("#alert-danger-panel-add-parameter-single").removeClass("hidden");
            $("#alert-danger-panel-add-parameter-single").html("Comprueba los campos marcados en rojo");
        }
        return errors;
    },
    
    changeIndividualSimulationParameter: function(name, input){
        var ss_param = this.getSingleSimulationParameter(name);
        var value = $(input).val();
        ss_param.setValueDnse3Format(value,"single");
    },
    
    /**
     * update a parameter for an individual simulation
     */
    updateIndividualSimulationParameter: function(){
        //only update if there are no errors in the form
        var errors = this.checkIndividualSimulationParameter();
        if (errors){
            return;
        }
        
        var name = $("#new-simulation-ps-name").val();
        var value_type = $("#new-simulation-nsp-value-type").val();
        var value = $("#new-simulation-nsp-value").val();
        var parameter_data = new Object();
        parameter_data.name = name;
        parameter_data.valueType = value_type;
        if (value_type == "fixed"){
            parameter_data.value = value;
        }
        var param = new ParameterResource(parameter_data);
        if (this.singleSimulationParameters == null){
            this.singleSimulationParameters = new Array();
        }
        
        for (var i = 0; i < this.singleSimulationParameters.length; i++){
            var ss_param = this.singleSimulationParameters[i];
            //check if it is the parameter that is being updated
            if (ss_param.getName() == this.singleSimulationUpdateParameterName){
                ss_param.setName(param.getName());
                ss_param.setValueType(param.getValueType());
                ss_param.setValue(param.getValue());
                break;
            }
        }
        this.singleSimulationUpdateParameterName = null;
        
        //hide the form for the parameter
        $("#panel-add-parameter-single").slideUp("slow");
        //print the table to update the parameter
        this.printTableIndividualSimulationParameters();
    },
    
    /**
     * save a parameter for an individual simulation
     */
    saveIndividualSimulationParameter: function(){
        //only save if there are no errors in the form
        var errors = this.checkIndividualSimulationParameter();
        if (errors){
            return;
        }
        var name = $("#new-simulation-ps-name").val();
        var value_type = $("#new-simulation-nsp-value-type").val();
        var value = $("#new-simulation-nsp-value").val();
        var parameter_data = new Object();
        parameter_data.name = name;
        parameter_data.valueType = value_type;
        if (value_type == "fixed"){
            parameter_data.value = value;
            parameter_data.random = false;
        }else{
            parameter_data.random = true;
        }
        var param = new ParameterResource(parameter_data);
        if (this.singleSimulationParameters == null){
            this.singleSimulationParameters = new Array();
        }
        this.singleSimulationParameters.push(param);
        
        //hide the form for the parameter
        $("#panel-add-parameter-single").slideUp("slow");
        //print the table to include the new parameter
        this.printTableIndividualSimulationParameters();
        //disable the new parameter button if there are no more available parameters to be added
        if (this.singleSimulationParameters.length == NewSimulationModal.parameters.length){
            $("#add-parameter-single").prop("disabled", true);
        }
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


