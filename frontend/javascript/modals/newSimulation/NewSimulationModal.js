/**
 * Manager for the modal dialog that creates a new simulation
 */
var NewSimulationModal = {
    
    modalId: "new_simulation_modal",
    cancelButtonId: "new_simulation_modal_cancel",
    acceptButtonId: "new_simulation_modal_accept",
    projectId: null,
    painters: null,
    
    /**
     * the parameters that are available for the simulation
     */
    parameters: null,

    tiempo: 0,
        
    init: function(){
        this.painters = new Array();
        this.tiempo = date.now();

        //set the events for the buttons
        $("#" + this.acceptButtonId).click(function(){
            NewSimulationModal.acceptNewSimulation();
        });
        $("#" + this.cancelButtonId).click(function(){
            NewSimulationModal.cancelNewSimulation();
        });     
        
        //events for the tabs before showing them
        $('#new_simulation_modal a[data-toggle="tab"][href="#step1-details"]').on('shown.bs.tab', function (e) {
            //alert("step1");
            });
        $('#new_simulation_modal a[data-toggle="tab"][href="#step2-parameters"]').on('shown.bs.tab', function (e) {
            NewSimulationModal.setTabParametersFields();
            });
        $('#new_simulation_modal a[data-toggle="tab"][href="#step3-files"]').on('shown.bs.tab', function (e) {
            NewSimulationModal.setTabFileGatheringFields();
        });
        
        //next step button
        $("#new_simulation_modal .next-step").click(function (e) {
            var active = $('#new_simulation_modal .wizard .nav-tabs li.active');
            if (active.prop("id")=="new-simulation-modal-tabs-t1"){
                NewSimulationModal.checkModalFieldsDetailsTab();
            }else if (active.prop("id")=="new-simulation-modal-tabs-t2"){
                NewSimulationModal.checkModalFieldsSimulationParametersTab();
            }else if (active.prop("id")=="new-simulation-modal-tabs-t3"){
                //alert("check tab 3");
            }
            //$active.next().removeClass('disabled');
            //nextTab($active);

        });
        
        //previous step button
        $("#new_simulation_modal .prev-step").click(function (e) {
            var $active = $('#new_simulation_modal .wizard .nav-tabs li.active');
            prevTab($active);

        });
        
        //change on the simulation type
        $('input[type="radio"][name="new-simulation-type"]').change(function(){
            NewSimulationModal.simulationTypeChanged();
        });       

    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    },
    
    setModalFieldsDetailsTab: function(){
        $("#form-group-nsd-name").removeClass("has-error");
        $("#form-group-nsd-repetitions").removeClass("has-error");
        $("#form-group-nsd-priority").removeClass("has-error");
        $("#form-group-nsd-simulation-type").removeClass("has-error");
        $("#alert-danger-step1-details").addClass("hidden");
        
        $("#new-simulation-name").val("");
        $("#new-simulation-repetitions").val("");
        $("#new-simulation-priority").val("50");
        $('input[name="new-simulation-type"]:checked').each(function(){
            $(this).prop("checked",false);  
        });
        
        $("#new_simulation_modal .prev-step").hide();
        $("#new_simulation_modal .next-step").show();
        $("#new_simulation_modal_accept").hide();
        this.showModal();
        //show the first tab when opening the modal
        $('#new-simulation-modal-tabs a:first').tab('show');
        $('#new-simulation-modal-tabs-t2').addClass("disabled");
        $('#new-simulation-modal-tabs-t3').addClass("disabled");
    },
    
    checkModalFieldsDetailsTab: function(){
        var errors = false;
        
        var name = $("#new-simulation-name").val();
        if (name == ""){
            $("#form-group-nsd-name").addClass("has-error");
            $('#new-simulation-name').focus();
            errors = true;
        }else{
            $("#form-group-nsd-name").removeClass("has-error");
        }
        
        var repetitions = $("#new-simulation-repetitions").val();
        if (repetitions == "" || repetitions<=0 || repetitions > 10000 || Math.floor(repetitions) != repetitions || !$.isNumeric(repetitions) ){
            $("#form-group-nsd-repetitions").addClass("has-error");
            if(!errors){
                $('#new-simulation-repetitions').focus();
            }
            errors = true;
        }else{
            $("#form-group-nsd-repetitions").removeClass("has-error");
        }

        var priority = $("#new-simulation-priority").val();
        if(priority == "" || priority < 1 || priority > 100 || Math.floor(priority) != priority || !$.isNumeric(priority) ){
            $("#form-group-nsd-priority").addClass("has-error");
            if(!errors){
                $('#new-simulation-priority').focus();
            }
            errors = true;
        }else{
            $("#form-group-nsd-priority").removeClass("has-error");
        }
        
        if ($('input[name="new-simulation-type"]:checked').length == 0){
            $("#form-group-nsd-simulation-type").addClass("has-error");
            errors = true;
        }else{
            $("#form-group-nsd-simulation-type").removeClass("has-error");
        }
        
        if (errors){
            $("#alert-danger-step1-details").removeClass("hidden");
            $("#alert-danger-step1-details").html("Comprueba los campos marcados en rojo");
        }else{
            $("#alert-danger-step1-details").addClass("hidden");
            //this.updateRequest();
            var active = $('#new_simulation_modal .wizard .nav-tabs li.active');
            active.next().removeClass('disabled');
            nextTab(active);
        }
    },
    
    checkModalFieldsSimulationParametersTab: function(){
        var errors = false;
        var simulation_type = $('input[name="new-simulation-type"]:checked', '#form-new-simulation-details').val();
        if (simulation_type == "single"){
            errors = NewSingleSimulation.checkIndividualSimulationParameters();
        }else if (simulation_type == "parameter-sweep"){
            errors = NewParameterSweepSimulation.checkSweepSimulationParameters();
        }
        if (!errors){
            var active = $('#new_simulation_modal .wizard .nav-tabs li.active');
            active.next().removeClass('disabled');
            nextTab(active);
        }
    },
    
    newSimulation: function(projectId){
        this.projectId = projectId;
        this.parameters = null;
        NewSingleSimulation.reset();
        NewParameterSweepSimulation.reset();
        NewSimulationFileGathering.reset();
        this.setModalFieldsDetailsTab();
        this.tiempo = Date.new();
    },
    
    addPainter: function(painterObj){
        this.painters.push(painterObj);
    },
    
    deletePainter: function(painterObj){
        var index = this.painters.indexOf(painterObj);
        if (index > -1){
            this.painters.splice(index, 1);
        }
    },
    
    notifyPainters: function(){
        for (var i = 0; i < this.painters.length; i++){
            var painter = this.painters[i];
            painter.paint();
        }
    },
    
    acceptNewSimulation: function(){
        var errors = NewSimulationFileGathering.checkFileGatheringFields();
        if (!errors){
            var simulation_type = $('input[name="new-simulation-type"]:checked', '#form-new-simulation-details').val();
            if (simulation_type == "single"){
                this.singleSimulationPostRequest();
            }else{
                this.parameterSweepSimulationPostRequest();
            }
        }
    },
    
    singleSimulationPostRequest: function(){       
        var data = this.buildSingleSimulationData();
        //var jsonData = JSON.stringify(data);
        //disable the button until the response is ready
        $("#" + this.acceptButtonId).prop("disabled", true);
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + NewSimulationModal.projectId + "/singlesimulations/",
            method: "POST",
            //contentType: "application/json",
            data: data,
            success: function(data){
                NewSimulationModal.hideModal();
                InfoModal.info(" Nueva simulación", "La simulación se ha creado correctamente y está lista para ser ejecutada. <br>Para <strong>iniciarla</strong> elija la opción <em>Ejecutar simulación</em> en su menú de acciones.", function(){NewSimulationModal.simulationCreated()});
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                NewSimulationModal.hideModal();
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Simulaciones Individuales", "Se ha producido al crear la simulación individual.");
                //alert("Se ha producido un error al crear la simulación individual");
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                $("#" + NewSimulationModal.acceptButtonId).prop("disabled", false);
            }
        });
    },
    
    parameterSweepSimulationPostRequest: function(){
        var data = this.buildSweepSimulationData();
        //var jsonData = JSON.stringify(data);
        //disable the button until the response is ready
        $("#" + this.acceptButtonId).prop("disabled", true);
        $.ajax({
            url: getBaseApiUrl() + "/users/username/projects/" + NewSimulationModal.projectId + "/parametersweepsimulations/",
            method: "POST",
            //contentType: "application/json",
            data: data,
            success: function(data){
                NewSimulationModal.hideModal();
                InfoModal.info(" Nueva simulación", "La simulación se ha creado correctamente y está lista para ser ejecutada. <br>Para <strong>iniciarla</strong> elija la opción <em>Ejecutar simulación</em> en su menú de acciones.", function(){NewSimulationModal.simulationCreated()});
            },
            error : function(xhr, status) {
                //hide the dialog and show the error
                NewSimulationModal.hideModal();
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Simulación de barrido", "Se ha producido al crear la simulación de barrido de parámetro.");

                //alert("Se ha producido un error al crear la simulación de barrido de parámetros");
            },
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
                $("#" + NewSimulationModal.acceptButtonId).prop("disabled", false);
            }
        });
    },
    
    simulationCreated: function(){
        NewSimulationModal.notifyPainters();
    },
    
    /**
     * Get the information for the single simulation that is going to be created and return it in the data format that is requested 
     * by the POST request to the server side
     */
    buildSingleSimulationData: function(){
        //get the single simulation name
        var name = $("#new-simulation-name").val();
        //get the number of repetitions
        var repetitions = $("#new-simulation-repetitions").val();
        //get the priority
        var priority =$("#new-simulation-priority").val();
        var tiempoPreparacion = Date.now() - this.tiempo;
        //get the information about the output files
        var output_files = new Array();
        for (var i = 0; i < NewSimulationFileGathering.outputFilesChoose.length; i++){
            var output_file_choose = NewSimulationFileGathering.outputFilesChoose[i];
            if (output_file_choose.chosen){
                output_files.push(output_file_choose.name);
            }
        }
        //get the information about the parameters
        var parameters = new Array();
        for (var i = 0; i < NewSingleSimulation.singleSimulationParameters.length; i++){
            var singleSimParamRes = NewSingleSimulation.singleSimulationParameters[i];
            parameters.push(singleSimParamRes.getData());           
        }
        var single_sim_data = {
            name: name,
            numRepetitions: repetitions,
            priority: priority,
            outputFiles: output_files,
            parameters: parameters,
            tiempoPrepara: tiempoPreparacion,
        }
        return single_sim_data;
    },
    
     /**
     * Get the information from the parameter sweep simulation that is being created and return it in the data format that is requested 
     * by the POST request to the server side
     */
    buildSweepSimulationData: function(){
        //get the sweep simulation name
        var name = $("#new-simulation-name").val();
        //get the number of repetitions
        var repetitions = $("#new-simulation-repetitions").val();
        //get the priority
        var priority = $("#new-simulation-priority").val();
        //get the information about the output files
        var output_files = new Array();
        for (var i = 0; i < NewSimulationFileGathering.outputFilesChoose.length; i++){
            var output_file_choose = NewSimulationFileGathering.outputFilesChoose[i];
            if (output_file_choose.chosen){
                output_files.push(output_file_choose.name);
            }
        }
        //get the information about the parameters
        var parameters = new Array();
        for (var i = 0; i < NewParameterSweepSimulation.sweepSimulationParameters.length; i++){
            var sweepSimParamRes = NewParameterSweepSimulation.sweepSimulationParameters[i];
            parameters.push(sweepSimParamRes.getData());           
        }
        var sweep_sim_data = {
            name: name,
            numRepetitions: repetitions,
            priority: priority,
            outputFiles: output_files,
            parameters: parameters
        }
        return sweep_sim_data;
    },
    
    cancelNewSimulation: function(){
        this.hideModal();
    },
    
    setTabParametersFields: function(){
        var simulation_type = $('input[name="new-simulation-type"]:checked', '#form-new-simulation-details').val();
        if (simulation_type == "single"){
            $("#div-form-new-simulation-parameters-single").show();
            //NewSingleSimulation.printTableIndividualSimulationParameters();
            NewSingleSimulation.setSingleSimulationFields();
            $("#panel-add-parameter-single").hide();
            $("#div-form-new-simulation-parameters-sweep").hide();
            
            //this.getIndividualSimulationParameters();
        }else{
            $("#div-form-new-simulation-parameters-single").hide();
            $("#panel-add-parameter-sweep").hide();           
            //NewParameterSweepSimulation.printTableSweepSimulationParameters();
            NewParameterSweepSimulation.setParameterSweepSimulationFields();
            $("#div-form-new-simulation-parameters-sweep").show();
        }
    },
    
    setTabFileGatheringFields: function(){
        $("#panel-file-gathering-add-file").hide();
        //NewSimulationFileGathering.printTableOutputFileResources();
        var simulation_type = $('input[name="new-simulation-type"]:checked', '#form-new-simulation-details').val();
        var repetitions = $("#new-simulation-repetitions").val();
        NewSimulationFileGathering.setFileGatheringFields(simulation_type, repetitions);
    },
    
    simulationTypeChanged: function(){
        NewSingleSimulation.reset();
        NewParameterSweepSimulation.reset();
        NewSimulationFileGathering.reset();
        NewSimulationModal.setTabFileGatheringFields();
    },
    
    acceptNewSimulationDetailsTab: function(){
        this.checkModalFieldsDetailsTab();
    },
    
    getParameter: function(name){
        for (var i = 0; i < this.parameters.length; i++){
            var ss_param = this.parameters[i];
            if (ss_param.getName() == name){
                return ss_param;
            }
        }
        return null;
    }
    
};

