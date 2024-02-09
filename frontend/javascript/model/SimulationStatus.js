var SimulationStatus = {
    /**
     * The simulation is waiting to be executed. All of its attributes can be modified (name, numRepetitions, parameters, outputfiles). It can be started
     */
    WAITING: "WAITING",
    /**
     * The simulation is starting to be executed. None of its attributes can be modified. No operations are allowed.
     */
    PREPARING: "PREPARING",
     /**
     * The simulation has been published and it is being executed. Only its name can be modified. It can be cancelled or paused.
     */
    PROCESSING: "PROCESSING",
    /**
     * The simulation has been paused. Only its name can be modified. It can be resumed or cancelled.
     */
    PAUSED: "PAUSED",
    /**
     * All the published simulations are being deleted. All of its attributes can be modified.
     */
    CLEANING: "CLEANING",
    /**
     * There was an error in the simulation. All of its attributes can be modified to allow the user to solve the error.
     */
    ERROR: "ERROR",
    /**
     * The simulation has been completed and it is waiting for the report being completed. Only its name can be modified. 
     */
    REPORTING: "REPORTING",
    /**
     * The simulation has been completed and the result files can be downloaded. Only its name can be modified.
     */
    FINISHED: "FINISHED",
    /**
     * The simulation is being removed. None of its attributes can be modified.
     */
    REMOVING: "REMOVING",
    
    getDescription: function(simulation_status){
        var description = "";
        switch(simulation_status){
            case SimulationStatus.WAITING:
                description = "Esperando al usuario";
                break;
            case SimulationStatus.PREPARING:
                description = "Generando simulaciones";
                break;
            case SimulationStatus.PROCESSING:
                description = "En ejecución";
                break;
            case SimulationStatus.PAUSED:
                description = "Pausado";
                break;
            case SimulationStatus.CLEANING:
                description = "Eliminando recursos obsoletos";
                break;
            case SimulationStatus.ERROR:
                description = "Error";
                break;
            case SimulationStatus.REPORTING:
                description = "Generando informe de resultados";
                break;
            case SimulationStatus.FINISHED:
                description = "Finalizado";
                break;
            case SimulationStatus.REMOVING:
                description = "Eliminando la simulación";
                break;
        }
        return description;
    }
};
