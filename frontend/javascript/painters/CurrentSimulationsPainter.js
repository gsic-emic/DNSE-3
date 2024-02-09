/**
 * painter for the progress bar that shows the current number of simulations being executed
 */
var CurrentSimulationsPainter = {
    
    singleSimulations: null,
    
    init: function(){
    },
    
    paint: function(){
        this.getUser();
    },
    
    getUser: function(){
        $.ajax({
            url: getBaseApiUrl() + "/users/username/",
            method: "GET",
            dataType: "json",//the expected data type from the server
            success : function(data) {
                CurrentSimulationsPainter.paintExecutionProgressBar(data);
            },
            error : function(xhr, status) {
                //manageRequestError(xhr, status);
                ErrorModal.errorMessage(" Carga de datos usuario", "Se ha producido un error an cargar los datos del usuario (barra progreso)");
            },
 
            // código a ejecutar sin importar si la petición falló o no
            complete : function(xhr, status) {
            //alert(status);
            }
        });
    },
    
    paintExecutionProgressBar: function(data){
        var user = new User(data);
        $("#navbar-progress-bar").empty();
        var progress_bar_type;
        if (user.getPercentage() <= 50){
            progress_bar_type = "progress-bar-success";
        }else if (user.getPercentage() > 50 && user.getPercentage() <= 75){
            progress_bar_type = "progress-bar-warning";
        }else{
            progress_bar_type = "progress-bar-danger";
        }
        var active_css = "";
        if (user.getCurrentSimulations() > 0){
            active_css = "active";
        }
        var progress_bar_html = '<div class="progress-bar ' + progress_bar_type  + ' progress-bar-striped ' + active_css + '" role="progressbar" aria-valuenow="' + user.getPercentage(2) + '" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: ' + user.getPercentage(2) + '%;">' +
                                user.getPercentage(2) + '%' +
                                '</div>';
        $("#navbar-progress-bar").html(progress_bar_html);
        $("#navbar-progress-bar-info").html("(" + user.getCurrentSimulations() + "/" + user.getMaxSimulations() + ")");
    }
};

