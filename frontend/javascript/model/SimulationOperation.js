var SimulationOperation = {
        /**
     * Start the execution of a simulation. Its status changes from PREPARING to WAITING
     */
    START:  "START",
    
    /**
     * Pause the execution of a simulation. Its status changes from PROCESSING to PAUSED
     */
    PAUSE: "PAUSE",
    
    /**
     * Stop the execution of a simulation. Its status changes from PROCESSING to FINISHED
     */
    STOP: "STOP"
};


