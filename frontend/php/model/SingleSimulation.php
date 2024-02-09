<?php

/**
 * Class for a single simulation in a simulation project
 */
class SingleSimulation {
    
    /**
     * The single simulation id
     * @var @integer simulationId
     */
    private $simulationId;
    
    /**
     * The simulation name
     * @var string type 
     */
    private $name;
    
    private $creationDate;
    private $updateDate;
    private $status;
    private $parameterSweepSimulation;
    private $completedSimulations;
    private $totalSimulations;
    private $startDate;
    private $finishedDate;
    private $numRepetitions;
    private $priority;
    private $parametersUri;
    private $outputFilesUri;
    
    /**
     *  the project this single simulation is for
     * @var array 
     */
    private $project;

    public function __construct($properties) {
        $reflectionClass = new ReflectionClass(get_class($this));
        //get the private properties of the class
        $reflection_properties = $reflectionClass->getProperties(ReflectionProperty::IS_PRIVATE);
        foreach ($reflection_properties as $r_prop) {
            $r_prop_name = $r_prop->getName();
            if (array_key_exists($r_prop_name, $properties)) {
                $this->$r_prop_name = $properties[$r_prop_name];
            }
        }
    }
    
    public function getSimulationId(){
        return $this->simulationId;
    }
    
    public function setSimulationId($simulationId){
        $this->simulationId = $simulationId;
    }
    
    public function getName(){
        return $this->name;
    }
    
    public function setName($name){
        $this->name = $name;
    }
    
    public function getCreationDate(){
        return $this->creationDate;
    }
    
    public function setCreationDate($creationDate){
        $this->creationDate = $creationDate;
    }
   
    public function getUpdateDate(){
        return $this->updateDate;
    }
    
    public function setUpdateDate($updateDate){
        $this->updateDate = $updateDate;
    }
    
    public function getStatus(){
        return $this->status;
    }
    
    public function setStatus($status){
        $this->status = $status;
    }
    
    public function getParameterSweepSimulation(){
        return $this->parameterSweepSimulation;
    }
    
    public function setParameterSweepSimulation($parameterSweepSimulation){
        $this->parameterSweepSimulation = $parameterSweepSimulation;
    }
    
    public function getCompletedSimulations(){
        return $this->completedSimulations;
    }
    
    public function setCompletedSimulations($completedSimulations){
        $this->completedSimulations = $completedSimulations;
    }
    
    public function getTotalSimulations(){
        return $this->totalSimulations;
    }
    
    public function setTotalSimulations($totalSimulations){
        $this->totalSimulations = $totalSimulations;
    }
    
    public function getStartDate(){
        return $this->startDate;
    }
    
    public function setStartDate($startDate){
        $this->startDate = $startDate;
    }
    
    public function getFinishedDate(){
        return $this->finishedDate;
    }
    
    public function setFinishedDate($finishedDate){
        $this->finishedDate = $finishedDate;
    }
 
    public function getNumRepetitions(){
        return $this->numRepetitions;
    }
    
    public function setNumRepetitions($numRepetitions){
        $this->numRepetitions = $numRepetitions;
    }

    public function getPriority(){
        return $this->priority;
    }

    public function setPriority($priority){
        $this->priority = $priority;
    }
    
    public function getParametersUri(){
        return $this->parametersUri;
    }
    
    public function setParametersUri($parametersUri){
        $this->parametersUri = $parametersUri;
    }
    
    public function getOutputFilesUri(){
        return $this->outputFilesUri;
    }
    
    public function setOutputFilesUri($outputFilesUri){
        $this->outputFilesUri = $outputFilesUri;
    }
    
    public function getProject(){
        return $this->project;
    }
    
    public function setProject($project){
        $this->project = $project;
    }
        
    public function getProperties($remove_nulls = true) {
        $properties = get_object_vars($this);
        if ($remove_nulls){
            foreach($properties as $prop_key => $prop_value){
                if (is_null($prop_value)){
                    unset($properties[$prop_key]);
                }
            }
        }
        return $properties;
    }
    
    public function getPercentageCompleted($decimals = 0){
        if ($this->totalSimulations == 0 || ($this->completedSimulations >= $this->totalSimulations)){
            return 100;
        }
        return round(($this->completedSimulations/$this->totalSimulations)*100,$decimals);
    }

}

?>

