<?php

/*
 * The description of a parameter available for a simulation project
 */
class Parameter{
    
    /**
     *
     * @var string the parameter name 
     */
    private $name;
    
    /**
     *
     * @var string the parameter type
     */
    private $type;
    
    /**
     *
     * @var string the default value for the parameter
     */
    private $defaultValue;
    
    /**
     *
     * @var array the possible values accepted by the parameter
     */
    private $possibleValues;
    
    /**
     * the minimum value that can not be reached by the parameter
     * @var integer 
     */
    private $greaterThan;
    
    /**
     * the maximum value that can not be reached by the parameter
     * @var type 
     */
    private $lessThan;
    
    /**
     * the minimum value for the parameter
     * @var type 
     */
    private $greaterThanOrEqualTo;
    
    /**
     * the maximum value for the parameter
     * @var type 
     */
    private $lessThanOrEqualTo;
    
    /**
     *  the project for which this parameter is
     * @var array 
     */
    private $project;
    
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
    
    public function getName(){
        return $this->name;
    }
    
    public function getType(){
        return $this->type;
    }
    
    public function getDefaultValue(){
        return $this->defaultValue;
    }
    
    public function getPossibleValues(){
        return $this->possibleValues;
    }

    public function getGreaterThan(){
        return $this->greaterThan;
    }
    
    public function getLessThan(){
        return $this->lessThan;
    }
    
    public function getGreaterThanOrEqualTo(){
        return $this->greaterThanOrEqualTo;
    }
    
    public function getLessThanOrEqualTo(){
        return $this->lessThanOrEqualTo;
    }
    
    public function getProject(){
        return $this->project;
    }
    

    public function setName($name){
        $this->name = $name;
    }
    
    public function setType($type){
        $this->type = $type;
    }
    
    public function setDefaultValue($defaultValue){
       $this->defaultValue = $defaultValue;
    }
    
    public function setPossibleValues($possibleValues){
        $this->possibleValues = $possibleValues;
    }

    public function setGreaterThan($greaterThan){
        $this->greaterThan = $greaterThan; 
    }
    
    public function setLessThan($lessThan){
        $this->lessThan = $lessThan;
    }
    
    public function setGreaterThanOrEqualTo($greaterThanOrEqualTo){
        $this->greaterThanOrEqualTo = $greaterThanOrEqualTo;
    }
    
    public function setLessThanOrEqualTo($lessThanOrEqualTo){
        $this->lessThanOrEqualTo = $lessThanOrEqualTo;
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
