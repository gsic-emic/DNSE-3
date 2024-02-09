<?php

/**
 * A simulation project in the system
 */
class SimulationProject{

    private $projectId;
    private $name;
    private $numSingleSimulations;
    private $numParameterSweepSimulations;
    private $creationDate;
    private $updateDate;
    private $description;
    private $outputFileStructuresUri;
    private $parametersUri;
    private $singleSimulationsUri;
    private $parameterSweepSimulationsUri;
    private $removing;
    
    public function __construct($properties) {
        $reflectionClass = new ReflectionClass(get_class($this));
        //get the private properties of the class
        $reflection_properties = $reflectionClass->getProperties(ReflectionProperty::IS_PRIVATE);
        foreach ($reflection_properties as $r_prop) {
            $r_prop_name = $r_prop->getName();
            if (array_key_exists($r_prop_name, $properties)){
                $this->$r_prop_name = $properties[$r_prop_name];
            }
        }
    }

    public function getProjectId() {
        return $this->projectId;
    }

    public function getName() {
        return $this->name;
    }
    
    public function getNumSingleSimulations() {
        return $this->numSingleSimulations;
    }
    
    public function getNumParameterSweepSimulations(){
        return $this->numParameterSweepSimulations;
    }

    public function getCreationDate() {
        return $this->creationDate;
    }

    public function getUpdateDate() {
        return $this->updateDate;
    }

    public function getDescription() {
        return $this->description;
    }

    public function getOutputFileStructuresUri() {
        return $this->outputFileStructuresUri;
    }

    public function getParametersUri() {
        return $this->parametersUri;
    }

    public function getSingleSimulationsUri() {
        return $this->singleSimulationsUri;
    }

    public function getParameterSweepSimulationsUri() {
        return $this->parameterSweepSimulationsUri;
    }
    
    public function isRemoving(){
        return $this->removing;
    }

    public function setProjectId($projectId) {
        $this->projectId = $projectId;
    }

    public function setName($name) {
        $this->name = $name;
    }
    
    public function setNumSingleSimulations($numSingleSimulations) {
        $this->numSingleSimulations = $numSingleSimulations;
    }
    
    public function setNumParameterSweepSimulations($numParameterSweepSimulations){
        $this->numParameterSweepSimulations = $numParameterSweepSimulations;
    }

    public function setCreationDate($creationDate) {
        $this->creationDate = $creationDate;
    }

    public function setUpdateDate($updateDate) {
        $this->updateDate = $updateDate;
    }

    public function setDescription($description) {
        $this->description = $description;
    }

    public function setOutputFileStructuresUri($outputFileStructuresUri) {
        $this->outputFileStructuresUri = $outputFileStructuresUri;
    }

    public function setParametersUri($parametersUri) {
        $this->parametersUri = $parametersUri;
    }

    public function setSingleSimulationsUri($singleSimulationsUri) {
        $this->singleSimulationsUri = $singleSimulationsUri;
    }

    public function setParameterSweepSimulationsUri($parameterSweepSimulationsUri) {
        $this->parameterSweepSimulationsUri = $parameterSweepSimulationsUri;
    }
    
    public function setRemoving($removing){
        $this->removing = $removing;
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
    
    public function getNumSimulations() {
        return $this->getNumSingleSimulations()  + $this->getNumParameterSweepSimulations();
    }

}
?>