<?php

/**
 * 
 */
class OutputFileStructure {
    
    
    /**
     * The output file structure name
     * @var string type 
     */
    private $name;
    
    /**
     * The list of variables that the structure contains
     * @var array 
     */
    private $outputVariables;
    
    /**
     * The file can contain more that one line or not
     * @var type 
     */
    private $multiLine;
    
    /**
     *  the project this output file structure is for
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
    
    public function getName(){
        return $this->name;
    }
    
    public function setName($name){
        $this->name = $name;
    }
    
    public function getOutputVariables(){
        return $this->outputVariables;
    }
    
    public function setOutputVariables($outputVariables){
        $this->outputVariables = $outputVariables;
    }
    
    public function isMultiLine(){
        return $this->multiLine;
    }
    
    public function setMultiLine($multiLine){
        $this->multiLine = $multiLines;
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

}

?>