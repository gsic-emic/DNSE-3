<?php

/*
 * Enum class for the different status for a simulation
 */
class SimulationStatus{
    
    /**
     * The simulation is waiting to be executed. All of its attributes can be modified (name, numRepetitions, priority, parameters, outputfiles). It can be started
     */
    const WAITING = "WAITING";
    /**
     * The simulation is starting to be executed. None of its attributes can be modified. No operations are allowed.
     */
    const PREPARING = "PREPARING";
    /**
     * The simulation has been published and it is being executed. Only its name and its priority can be modified. It can be cancelled or paused.
     */
    const PROCESSING = "PROCESSING";
    /**
     * The simulation has been paused. Only its name can be modified. It can be resumed or cancelled.
     */
    const PAUSED = "PAUSED";
     /**
     * All the published simulations are being deleted. All of its attributes can be modified.
     */
    const CLEANING = "CLEANING";
    /**
     * There was an error in the simulation. All of its attributes can be modified to allow the user to solve the error.
     */
    const ERROR = "ERROR";
     /**
     * The simulation has been completed and it is waiting for the report being completed. Only its name can be modified. 
     */
    const REPORTING = "REPORTING";
     /**
     * The simulation has been completed and the result files can be downloaded. Only its name can be modified.
     */
    const FINISHED = "FINISHED";
    /**
     * The simulation is being removed. None of its attributes can be modified.
     */
    const REMOVING = "REMOVING";
    
    /**
     * Get the description for a simulation status
     * @param type $simulation_status
     * @return string 
     */
    public static function getDescription($simulation_status){
        $description = "";
        switch($simulation_status){
            case SimulationStatus::PREPARING:
                $description = "Generando simulaciones";
                break;
            case SimulationStatus::WAITING:
                $description = "Esperando al usuario";
                break;
            case SimulationStatus::PROCESSING:
                $description = "En ejecución";
                break;
            case SimulationStatus::PAUSED:
                $description = "Pausado";
                break;
            case SimulationStatus::CLEANING:
                $description = "Eliminando recursos obsoletos";
                break;
            case SimulationStatus::ERROR:
                $description = "Error";
                break;
            case SimulationStatus::REPORTING:
                $description = "Generando informe de resultados";
                break;
            case SimulationStatus::FINISHED:
                $description = "Finalizado";
                break;
            case SimulationStatus::REMOVING:
                $description = "Eliminando la simulación";
                break;
        }
        return $description;
    }
}
?>
