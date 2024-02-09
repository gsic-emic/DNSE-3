/**
 * Manager for the modal dialog that creates a new parameter sweep simulation
 */
var NewParameterSweepSimulation = {
    
    /**
     * the parameters that the user has added to the sweep simulation from those available
     */
    sweepSimulationParameters: null,
    
    /**
     * the name of the parameter that is being updated in a sweep simulation
     */
    sweepSimulationUpdateParameterName: null,
    
    /**
     * the values for the parameter of type fixed
     */
    sweepSimulationParameterValues: null,
    
    init: function(){
        
        $("#new-simulation-nsp-sweep-value-type").change(function(){
            switch($("#new-simulation-nsp-sweep-value-type").val()){
                case "range":
                    $("#form-group-nsp-sweep-range").show();
                    $("#form-group-nsp-sweep-fixed").hide();
                    break;
                case "fixed":
                    $("#form-group-nsp-sweep-range").hide();
                    $("#form-group-nsp-sweep-fixed").show();
                    break;
                default:
                    $("#form-group-nsp-sweep-range").hide();
                    $("#form-group-nsp-sweep-fixed").hide();
            }
        });
        
        //onclick event on the add parameter button
        $("#add-parameter-sweep").click(function(){
            NewParameterSweepSimulation.setAddSweepSimulationParameter();
        });
        
        //onclick event on the close parameter button
        $("#close-parameter-sweep").click(function(){
            NewParameterSweepSimulation.closeSweepSimulationParameter();
        });
        
        //onclick event on the update parameter button
        $("#update-parameter-sweep").click(function(){
            NewParameterSweepSimulation.updateSweepSimulationParameter();
        });
        
        //onclick event on the save parameter button
        $("#save-parameter-sweep").click(function(){
            NewParameterSweepSimulation.saveSweepSimulationParameter();
        });

        //onclick event on the add value button
        $("#nsp-sweep-fixed-values-add").click(function(){
            NewParameterSweepSimulation.sweepSimulationParameterValues.push("");
            NewParameterSweepSimulation.printFixedValueInputs(NewParameterSweepSimulation.sweepSimulationParameterValues);
        });
    },
    
    reset: function(){
        this.sweepSimulationParameters = null;
        this.sweepSimulationParameterValues = new Array();
        this.resetTableParameterSweepSimulationParameters();
    },
    
    getSweepSimulationParameters: function(){
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
                NewParameterSweepSimulation.fillSweepSimulationParameterNameSelector(true);
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                NewSimulationModal.hideModal();
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Carga parámetros", "Se ha producido un error al cargar los parámetros de la simulación de barrido.");
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
    },
    
    /*printTableSweepSimulationParameters: function(){
        var table_body = $("#new-simulation-parameters-sweep-table > tbody");
        table_body.empty();
        for (var i = 0; i < this.sweepSimulationParameters.length; i++){
            var param = this.sweepSimulationParameters[i];
            var table_tr = $("<tr></tr>");
            var td_number = $("<td>" + (i+1) + "</td>");
            var td_name = $("<td>" + param.getName() + "</td>");
            var td_actions = $('<td>' +
                                    '<button onclick="NewParameterSweepSimulation.setViewSweepSimulationParameter(\'' + param.getName() + '\')" class="btn btn-success btn-xs"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver </button> ' +
                                    '<button onclick="NewParameterSweepSimulation.setEditSweepSimulationParameter(\'' + param.getName() + '\')" class="btn btn-info btn-xs"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar </button> ' +
                                    '<button onclick="NewParameterSweepSimulation.deleteSweepSimulationParameter(\'' + param.getName() + '\')" class="btn btn-danger btn-xs" ><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar </a>' +
                              '</td>');
            table_tr.append(td_number);
            table_tr.append(td_name);
            table_tr.append(td_actions);
            table_body.append(table_tr);
        }
    },*/
    
    resetTableParameterSweepSimulationParameters: function(){
        var table_body = $("#new-simulation-parameters-sweep-table > tbody");
        table_body.empty();
    },
    
    printTableParameterSweepSimulationParameters: function(){
        var table_body = $("#new-simulation-parameters-sweep-table > tbody");
        table_body.empty();
        for (var i = 0; i < this.sweepSimulationParameters.length; i++){
            var sweep_sim_param = this.sweepSimulationParameters[i];
            var param = NewSimulationModal.getParameter(sweep_sim_param.getName());
            var def_value = "random";
            if (typeof param.getDefaultValue()!='undefined' && param.getDefaultValue()!=null){
                def_value = param.getDefaultValue();
            }
            var table_tr = $("<tr></tr>");
            var td_number = $("<td>" + (i+1) + "</td>");
            var td_name = $("<td>" + sweep_sim_param.getName() + "</td>");
            var td_type = $("<td>" + ParameterType.getDescription(param.getType()) + "</td>");
            var td_value = $('<td><div id="form-group-nsp-sweep-value-' + i + '" class="form-group"><input type="text" class="form-control" id="new-simulation-nsp-sweep-value-' + i + '" placeholder="' + def_value + '" value="' + sweep_sim_param.getValueDnse3Format("sweep") + '" onchange="NewParameterSweepSimulation.changeParameterSweepSimulationParameter(\'' + sweep_sim_param.getName() + '\',this)" ></div></td>');
            table_tr.append(td_number);
            table_tr.append(td_name);
            table_tr.append(td_type);
            table_tr.append(td_value);
            table_body.append(table_tr);
            /*if (param.getType()=="SEED"){
                //it is a random value, so we disable the input field
                $("#new-simulation-nsp-sweep-value-" + i).prop("disabled", true);
            }*/
        }
        table_body.append("<tr><td colspan='4'><div><b>Formatos válidos para indicar el barrido del parámetro a estudiar:</b></div><div><ul><li>Valores separados por comas SIN ESPACIOS. Ej.: <b>10,100,572</b></li><li>Rango de valores <i>min:step:max</i>. Ej.: <b>9:2:15</b></li></ul></div></td></tr>");
    },
    
    printFixedValueInputs: function(values){
        $("#form-group-nsp-sweep-fixed-values").empty();
        for (var i = 0; i < values.length; i++){
            var html_fixed = '<div id="form-group-nsp-sweep-value-' + i + '"  class="form-group col-md-6">' +
                                '<div class="input-group">' +
                                    '<input type="text" value="' + values[i] + '" class="form-control" id="new-simulation-nsp-sweep-value-' + i + '" name="new-simulation-nsp-sweep-value[]" placeholder="Valor" onchange="NewParameterSweepSimulation.setFixedValueInput(\'' + i + '\')">' +
                                    '<span class="input-group-btn">' +                                   
                                        '<button onclick="NewParameterSweepSimulation.deleteFixedValueInput(\'' + i + '\')" class="btn btn-default" type="button"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></button>' +
                                    '</span>' +
                                '</div>' +
                             '</div>';
            $("#form-group-nsp-sweep-fixed-values").append(html_fixed);
        }
    },
    
    printViewFixedValueInputs: function(values){
        $("#form-group-nsp-sweep-fixed-values").empty();
        for (var i = 0; i < values.length; i++){
            var html_fixed = '<div id="form-group-nsp-sweep-value-' + i + '"  class="form-group col-md-6">' +
                                '<input type="text" value="' + values[i] + '" class="form-control" id="new-simulation-nsp-sweep-value-' + i + '" name="new-simulation-nsp-sweep-value[]" placeholder="Valor" onchange="NewParameterSweepSimulation.setFixedValueInput(\'' + i + '\')">' +
                             '</div>';
            $("#form-group-nsp-sweep-fixed-values").append(html_fixed);
        }
    },
    
    setFixedValueInput: function(index){
        this.sweepSimulationParameterValues[index] = $("#new-simulation-nsp-sweep-value-" + index).val();
    },
    
    /**
     * delete a fix value input
     */
    deleteFixedValueInput: function(index){
       this.sweepSimulationParameterValues.splice(index,1); 
       //once it has been deleted, we print again the inputs
       this.printFixedValueInputs(this.sweepSimulationParameterValues);
    },
    
    setViewSweepSimulationParameter: function(name){
        
        var param = this.getSweepSimulationParameter(name);
        this.fillSweepSimulationParameterNameSelector(false);
        $("#new-simulation-sweep-name").val(param.getName());
        $("#new-simulation-nsp-sweep-value-type").val(param.getValueType());
        switch(param.getValueType()){
            case "range":$("#new-simulation-nsp-sweep-range-min").val(param.getMinValue());
                          $("#new-simulation-nsp-sweep-range-max").val(param.getMaxValue());
                          $("#new-simulation-nsp-sweep-range-step").val(param.getStep());
                          $("new-simulation-nsp-sweep-range-units").val(param.getUnits());
                          break;
            case "fixed":this.printViewFixedValueInputs(param.getValues());
                          break;
            case "random":break;
        }

        //set the title
        $("#panel-add-parameter-sweep-title").html("Ver parámetro: " + name);

        //hide the errors
        $("#form-new-simulation-parameters-sweep .has-error").each(function(){
            $(this).removeClass("has-error");
        });
        $("#alert-danger-panel-add-parameter-sweep").addClass("hidden");
        
        //disable the form elements
        $("#form-new-simulation-parameters-sweep select").each(function(){
            $(this).prop("disabled", true);
        });
        $("#form-new-simulation-parameters-sweep input").each(function(){
            $(this).prop("disabled", true);
        });
        
        var value_type = $("#new-simulation-nsp-sweep-value-type").val();
        //show or hide the inputs that depend on the value type
        switch(value_type){
            case "range":
                $("#form-group-nsp-sweep-range").show();
                $("#form-group-nsp-sweep-fixed").hide();
                break;
            case "fixed":
                $("#form-group-nsp-sweep-range").hide();
                $("#form-group-nsp-sweep-fixed-button").hide();
                $("#form-group-nsp-sweep-fixed").show();
                break;
            default:
                $("#form-group-nsp-sweep-range").hide();
                $("#form-group-nsp-sweep-fixed").hide();
        }
        
        //show the right buttons
        $("#close-parameter-sweep").removeClass("hidden");
        $("#update-parameter-sweep").addClass("hidden");
        $("#save-parameter-sweep").addClass("hidden");
        
        //show the form
        $("#panel-add-parameter-sweep").slideDown("slow");
    },
           
    setEditSweepSimulationParameter: function(name){
        this.sweepSimulationUpdateParameterName = name;
        var param = this.getSweepSimulationParameter(name);
        this.fillUpdateSweepSimulationParameterNameSelector(param);
        $("#new-simulation-sweep-name").val(param.getName());
        $("#new-simulation-nsp-sweep-value-type").val(param.getValueType());
        switch(param.getValueType()){
            case "range":$("#new-simulation-nsp-sweep-range-min").val(param.getMinValue());
                          $("#new-simulation-nsp-sweep-range-max").val(param.getMaxValue());
                          $("#new-simulation-nsp-sweep-range-step").val(param.getStep());
                          $("new-simulation-nsp-sweep-range-units").val(param.getUnits());
                          break;
            case "fixed":this.sweepSimulationParameterValues = new Array();
                          var values = param.getValues();
                          for (var i = 0; i < values.length; i++){
                             this.sweepSimulationParameterValues.push(values[i]);
                          }
                          this.printFixedValueInputs(this.sweepSimulationParameterValues);
                          break;
            case "random":break;
        }
                
        //set the title
        $("#panel-add-parameter-sweep-title").html("Editar parámetro: " + name);        
        
        //hide the errors
        $("#form-new-simulation-parameters-sweep .has-error").each(function(){
            $(this).removeClass("has-error");
        });
        $("#alert-danger-panel-add-parameter-sweep").addClass("hidden");
        
        //enable the form elements
        $("#form-new-simulation-parameters-sweep select").each(function(){
            $(this).prop("disabled", false);
        });
        $("#form-new-simulation-parameters-sweep input").each(function(){
            $(this).prop("disabled", false);
        });
        
        var value_type = $("#new-simulation-nsp-sweep-value-type").val();
        //show or hide the inputs that depend on the value type
        switch(value_type){
            case "range":
                $("#form-group-nsp-sweep-range").show();
                $("#form-group-nsp-sweep-fixed").hide();
                break;
            case "fixed":
                $("#form-group-nsp-sweep-range").hide();
                $("#form-group-nsp-sweep-fixed-button").show();
                $("#form-group-nsp-sweep-fixed").show();
                break;
            default:
                $("#form-group-nsp-sweep-range").hide();
                $("#form-group-nsp-sweep-fixed").hide();
        }
        
        //show the right buttons
        $("#close-parameter-sweep").removeClass("hidden");
        $("#update-parameter-sweep").removeClass("hidden");
        $("#save-parameter-sweep").addClass("hidden");
        
        //show the form
        $("#panel-add-parameter-sweep").slideDown("slow");
    },
    
    setParameterSweepSimulationFields: function(){
        if (NewParameterSweepSimulation.sweepSimulationParameters == null){
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

                    //set the initial value for each parameter sweep simulation parameter from their default value
                    NewParameterSweepSimulation.sweepSimulationParameters = new Array();
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
                        param.setValueDnse3Format(parameter_data.value,"sweep");
                        NewParameterSweepSimulation.sweepSimulationParameters.push(param);
                    }
                    NewParameterSweepSimulation.printTableParameterSweepSimulationParameters();
                },
                error : function(xhr, status) {
                    //hide the dialog and show the error
                    NewSimulationModal.hideModal();
                    //manageRequestError(xhr, status);
                    ErrorModal.errorMessage(" Carga parámetros", "Se ha producido un error al cargar los parámetros de la simulación de barrido.");
                },

                // código a ejecutar sin importar si la petición falló o no
                complete : function(xhr, status) {
                //alert(status);
                }
            });
        }else{
            NewParameterSweepSimulation.printTableParameterSweepSimulationParameters();
        }
    },
        
    setAddSweepSimulationParameter: function(){
        if (NewSimulationModal.parameters == null){
            this.getSweepSimulationParameters();
        }else{
            this.fillSweepSimulationParameterNameSelector(true);
        }
        //reset the different values
        $("#new-simulation-nsp-sweep-value-type option:selected").prop("selected", false);
        //$("#new-simulation-nsp-sweep-value").val("");
        $("#form-group-nsp-sweep-range input[type=\"text\"]").each(function(){
            $(this).val("");
        });
        this.sweepSimulationParameterValues = new Array();
        this.sweepSimulationParameterValues.push("");
        this.printFixedValueInputs(this.sweepSimulationParameterValues);
        
        //hide the subsections that depend on the value type
        $("#form-group-nsp-sweep-range").hide();
        $("#form-group-nsp-sweep-fixed").hide();
        
        //set the title
        $("#panel-add-parameter-sweep-title").html("Nuevo parámetro");
        
        //hide the errors
        $("#form-new-simulation-parameters-sweep .has-error").each(function(){
            $(this).removeClass("has-error");
        });
        $("#alert-danger-panel-add-parameter-sweep").addClass("hidden");
        
        //enable the form elements
        $("#form-new-simulation-parameters-sweep select").each(function(){
            $(this).prop("disabled", false);
        });
        $("#form-new-simulation-parameters-sweep input").each(function(){
            $(this).prop("disabled", false);
        });
        
        //show the right buttons
        //$("#close-parameter-sweep").addClass("hidden");
        $("#close-parameter-sweep").removeClass("hidden");
        $("#update-parameter-sweep").addClass("hidden");
        $("#save-parameter-sweep").removeClass("hidden");
        
        //show the form
        $("#panel-add-parameter-sweep").slideDown("slow");
    },
         
    deleteSweepSimulationParameter: function(name){
        //search and delete the parameter
        for (var i = 0; i < this.sweepSimulationParameters.length; i++){
            var sweep_sim_param = this.sweepSimulationParameters[i];
            if (sweep_sim_param.getName()==name){
                this.sweepSimulationParameters.splice(i,1);
                break;
            }
        }
        //hide the form for the parameter
        $("#panel-add-parameter-sweep").slideUp("slow");
        //refresh the table not to show the deleted parameter
        this.printTableSweepSimulationParameters();
        //enable the new parameter button if now there are available parameters to be added
        if (this.sweepSimulationParameters.length < NewSimulationModal.parameters.length){
            $("#add-parameter-sweep").prop("disabled", false);
        }
    },
    
    fillSweepSimulationParameterNameSelector: function(only_available){
        $("#new-simulation-sweep-name").empty();
        $('<option value="">-- Selecciona el nombre del parámetro --</option>').appendTo("#new-simulation-sweep-name");
        for(var i = 0; i < NewSimulationModal.parameters.length; i++){
            var param = NewSimulationModal.parameters[i];
            if (only_available){
                //check if the parameter has not been added before to the simulation
                var available = true;
                for (var j = 0; j < this.sweepSimulationParameters.length; j++){
                    var sweep_sim_param = this.sweepSimulationParameters[j];
                    if (param.getName() == sweep_sim_param.getName()){
                        available = false;
                        break;
                    }
                }
                if (available){
                    $('<option value="' + param.getName() + '">' + param.getName() + '</option>').appendTo("#new-simulation-sweep-name");
                }
            }else{
                $('<option value="' + param.getName() + '">' + param.getName() + '</option>').appendTo("#new-simulation-sweep-name");
            }
        }
        $("#new-simulation-sweep-name").val("");
    },
            
    fillUpdateSweepSimulationParameterNameSelector: function(param){
        this.fillSweepSimulationParameterNameSelector(true);
        $('<option value="' + param.getName() + '">' + param.getName() + '</option>').appendTo("#new-simulation-sweep-name");
    },

    
    /**
     * close view a parameter for an sweep simulation
     */
    closeSweepSimulationParameter: function(){
        //hide the form for the parameter
        $("#panel-add-parameter-sweep").slideUp("slow");
    },
    
    checkSweepSimulationParameters: function(){
        var errors = false;
        for (var i = 0; i < this.sweepSimulationParameters.length; i++){
            var sweep_sim_param_res = this.sweepSimulationParameters[i];
            var value = $("#new-simulation-nsp-sweep-value-" + i).val();
            //value = value.replace(" ", "");
            value = value.replace(new RegExp(' ', 'g'), '');//remove the black spaces
            if (value == ""){//we don't let more blank spaces
                $("#form-group-nsp-sweep-value-" + i).addClass("has-error");
                if (!errors){
                    $("#new-simulation-nsp-sweep-value-" + i).focus();
                }
                errors = true;
            }else{
                //check the value type
                var sweep_sim_param = NewSimulationModal.getParameter(sweep_sim_param_res.getName());
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
                                    if (typeof sweep_sim_param.getLessThan()!='undefined' &&  parseFloat(entered_value) >= sweep_sim_param.getLessThan()){
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
                    $("#form-group-nsp-sweep-value-" + i).addClass("has-error");
                    if (!errors){
                        $("#new-simulation-nsp-sweep-value-" + i).focus();
                    }
                    errors = true;
                }else{
                    $("#form-group-nsp-sweep-value-" + i).removeClass("has-error");
                }
            }
        }    
        if (errors){
            $("#alert-danger-panel-parameters-sweep").removeClass("hidden");
            $("#alert-danger-panel-parameters-sweep").html("Comprueba los campos marcados en rojo");
        }else{
            $("#alert-danger-panel-parameters-sweep").addClass("hidden");
        }
        return errors;
    },
    
    checkSweepSimulationParameter: function(){
        var errors = false;
        
        var name = $("#new-simulation-sweep-name").val();
        if (name == ""){
            $("#form-group-nsp-sweep-name").addClass("has-error");
            $('#new-simulation-sweep-name').focus();
            errors = true;
        }else{
            $("#form-group-nsp-sweep-name").removeClass("has-error");
        }
        
        var value_type = $("#new-simulation-nsp-sweep-value-type").val();
        if (value_type == ""){
            $("#form-group-nsp-sweep-value-type").addClass("has-error");
            if(!errors){
                $('#new-simulation-nsp-sweep-value-type').focus();
            }
            errors = true;
        }else{
            $("#form-group-nsp-sweep-value-type").removeClass("has-error");
        }
        
        $("#form-group-nsp-sweep-fixed > label.control-label").removeClass("label-error");
        if (value_type == "fixed"){
            $('input[type="text"][name="new-simulation-nsp-sweep-value[]"]').each(function(index, item){
                //var value = $("#new-simulation-nsp-sweep-value").val();
                var value = $(this).val();
                if (value == ""){
                    $("#form-group-nsp-sweep-fixed > label.control-label").addClass("label-error");
                    $("#form-group-nsp-sweep-value-" + index).addClass("has-error");
                    if (!errors){
                        $("#new-simulation-nsp-sweep-value-" + index).focus();
                    }
                    errors = true;
                }else{
                    //check the value type
                    var sweep_sim_param = NewSimulationModal.getParameter(name);
                    var type = sweep_sim_param.getType();
                    var type_error = false;
                    switch(type){
                        case "STRING_VALUE":
                            if (typeof value != 'string' && !(value instanceof String)){
                                type_error = true;
                            }else{
                                var possible_values = sweep_sim_param.getPossibleValues();
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
                                if (sweep_sim_param.getGreaterThan()!=null &&  value <= sweep_sim_param.getGreaterThan()){
                                    type_error = true;
                                }
                                if (sweep_sim_param.getLessThan()!=null &&  value >= sweep_sim_param.getLessThan()){
                                    type_error = true;
                                }
                                if (sweep_sim_param.getGreaterThanOrEqualTo()!=null &&  value < sweep_sim_param.getGreaterThanOrEqualTo()){
                                    type_error = true;
                                }
                                if (sweep_sim_param.getLessThanOrEqualTo()!=null &&  value > sweep_sim_param.getLessThanOrEqualTo()){
                                    type_error = true;
                                } 
                            }
                            break;
                        case "RATIONAL_VALUE":
                            if (!$.isNumeric(value)){
                                type_error = true;
                            }else{
                                if (sweep_sim_param.getGreaterThan()!=null &&  value <= sweep_sim_param.getGreaterThan()){
                                    type_error = true;
                                }
                                if (sweep_sim_param.getLessThan()!=null &&  value >= sweep_sim_param.getLessThan()){
                                    type_error = true;
                                }
                                if (sweep_sim_param.getGreaterThanOrEqualTo()!=null &&  value < sweep_sim_param.getGreaterThanOrEqualTo()){
                                    type_error = true;
                                }
                                if (sweep_sim_param.getLessThanOrEqualTo()!=null &&  value > sweep_sim_param.getLessThanOrEqualTo()){
                                    type_error = true;
                                } 
                            }
                    }
                    if (type_error){
                        $("#form-group-nsp-sweep-fixed > label.control-label").addClass("label-error");
                        $("#form-group-nsp-sweep-value-" + index).addClass("has-error");
                        if (!errors){
                            $("#new-simulation-nsp-sweep-value-" + index).focus();
                        }
                        errors = true;
                    }else{
                        $("#form-group-nsp-sweep-value-" + index).removeClass("has-error");
                    }
                }
            });
        }
        
        if (errors){
            $("#alert-danger-panel-add-parameter-sweep").removeClass("hidden");
            $("#alert-danger-panel-add-parameter-sweep").html("Comprueba los campos marcados en rojo");
        }
        return errors;
    },
    
    changeParameterSweepSimulationParameter: function(name, input){
        var sweep_sim_param = this.getSweepSimulationParameter(name);
        var value = $(input).val();
        sweep_sim_param.setValueDnse3Format(value,"sweep");
    },
        
    /**
     * update a parameter for a sweep simulation
     */
    updateSweepSimulationParameter: function(){
        //only update if there are no errors in the form
        var errors = this.checkSweepSimulationParameter();
        if (errors){
            return;
        }
        
        var name = $("#new-simulation-sweep-name").val();
        var value_type = $("#new-simulation-nsp-sweep-value-type").val();
        var value = $("#new-simulation-nsp-sweep-value").val();
        var parameter_data = new Object();
        parameter_data.name = name;
        parameter_data.valueType = value_type;
        //get the data that depends on the value type
        switch(value_type){
            case "range":parameter_data.minValue = $("#new-simulation-nsp-sweep-range-min").val();
                           parameter_data.maxValue = $("#new-simulation-nsp-sweep-range-max").val();
                           parameter_data.step = $("#new-simulation-nsp-sweep-range-step").val();
                           parameter_data.units = $("new-simulation-nsp-sweep-range-units").val();
                           parameter_data.random = false;
                           break;
            case "fixed":
                           parameter_data.values = new Array();
                           $('input[type="text"][name="new-simulation-nsp-sweep-value[]"]').each(function(index, item){
                                var value = $(this).val();
                                parameter_data.values.push(value);
                           });
                           parameter_data.random = false;
                           break;
            case "random":
                           parameter_data.random = true;
                           break;
        }
        var param = new ParameterResource(parameter_data);
        if (this.sweepSimulationParameters == null){
            this.sweepSimulationParameters = new Array();
        }
        
        //update the values
        var sweep_sim_param = this.getSweepSimulationParameter(this.sweepSimulationUpdateParameterName);
        sweep_sim_param.setName(param.getName());
        sweep_sim_param.setValueType(param.getValueType());
        switch(value_type){
            case "range":sweep_sim_param.setMinValue(param.getMinValue());
                         sweep_sim_param.setMaxValue(param.getMaxValue());
                         sweep_sim_param.setStep(param.getStep());
                         sweep_sim_param.setUnits(param.getUnits());
                         sweep_sim_param.setRandom(param.getRamdom());
                         break;
            case "fixed":sweep_sim_param.setValues(param.getValues());
                         sweep_sim_param.setRandom(param.getRamdom());
                         break;
            case "random":sweep_sim_param.setRandom(param.getRamdom());
                          break;
        }
                
        this.sweepSimulationUpdateParameterName = null;
        
        //hide the form for the parameter
        $("#panel-add-parameter-sweep").slideUp("slow");
        //print the table to update the parameter
        this.printTableSweepSimulationParameters();
    },
    
    /**
     * save a parameter for a sweep simulation
     */
    saveSweepSimulationParameter: function(){
        //only save if there are no errors in the form
        var errors = this.checkSweepSimulationParameter();
        if (errors){
            return;
        }
        var name = $("#new-simulation-sweep-name").val();
        var value_type = $("#new-simulation-nsp-sweep-value-type").val();
        var value = $("#new-simulation-nsp-sweep-value").val();
        var parameter_data = new Object();
        parameter_data.name = name;
        parameter_data.valueType = value_type;
        //get the data that depends on the value type
        switch(value_type){
            case "range":parameter_data.minValue = $("#new-simulation-nsp-sweep-range-min").val();
                           parameter_data.maxValue = $("#new-simulation-nsp-sweep-range-max").val();
                           parameter_data.step = $("#new-simulation-nsp-sweep-range-step").val();
                           parameter_data.units = $("new-simulation-nsp-sweep-range-units").val();
                           parameter_data.random = false;
                           break;
            case "fixed":
                           parameter_data.values = new Array();
                           $('input[type="text"][name="new-simulation-nsp-sweep-value[]"]').each(function(index, item){
                                var value = $(this).val();
                                parameter_data.values.push(value);
                           });
                           parameter_data.random = false;
                           break;
            case "random":
                           parameter_data.random = true;
                           break;
        }
        var param = new ParameterResource(parameter_data);
        if (this.sweepSimulationParameters == null){
            this.sweepSimulationParameters = new Array();
        }
        
        this.sweepSimulationParameters.push(param);
        
        //hide the form for the parameter
        $("#panel-add-parameter-sweep").slideUp("slow");
        //print the table to include the new parameter
        this.printTableSweepSimulationParameters();
        //disable the new parameter button if there are no more available parameters to be added
        if (this.sweepSimulationParameters.length == NewSimulationModal.parameters.length){
            $("#add-parameter-sweep").prop("disabled", true);
        }
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


