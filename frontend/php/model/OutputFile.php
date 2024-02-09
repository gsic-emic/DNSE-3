<?php

/**
 * 
 */
class OutputFile {
    
    /**
     * @var string the output file name 
     */
    private $outputFileName;
    
     /**
     * @var object the output file structure for this output file
     */
    private $outputFileStructure;
    
    /**
     *
     * @var string the output file type 
     */
    private $type;
    
    /**
     * @var object the project this output file structure is for
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
    
    public function getOutputFileName(){
        return $this->outputFileName;
    }
    
    public function setOutputFileName($outputFileName){
        $this->outputFileName = $outputFileName;
    }
    
    public function getOutputFileStructure(){
        return $this->outputFileStructure;
    }
    
    public function setOutputFileStructure($outputFileStructure){
        $this->outputFileStructure = $outputFileStructure;
    }
    
    public function getType(){
        return $this->type;
    }
    
    public function setType($type){
        $this->type = $type;
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
