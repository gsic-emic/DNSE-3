<?php

/**
 * The values provided for a parameter in a simulation
 */
class ParameterResource {
    
    /**
     * @var string the parameter name 
     */
    private $name;
    /**
     *
     * @var string the parameter value 
     */
    private $value;
    
    /**
     *
     * @var array the values for the parameter (in a parameter sweep simulation)
     */
    private $values;
    /**
     * @var integer the minimum value for a range
     */
    private $minValue;
    /**
     * @var integer the maximum value for a range
     */
    private $maxValue;
    /**
     * @var  integer the step for a range
     */
    private $step;
    /**
     * @var string the units used by the parameter
     */
    private $units;
    /**
     *
     * @var boolean whether the parameter has a random value
     */
    private $random;

    /**
     *  the simulation for which this parameter has been set
     * @var array 
     */
    private $simulation;

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

    public function getName() {
        return $this->name;
    }

    public function setName($name) {
        $this->name = $name;
    }
    
    public function getValue() {
        return $this->value;
    }

    public function setValue($value) {
        $this->value = $value;
    }
    
    public function getValues() {
        return $this->values;
    }

    public function setValues($values) {
        $this->values = $values;
    }
    
    public function getMinValue() {
        return $this->minValue;
    }

    public function setMinValue($minValue) {
        $this->minValue = $minValue;
    }
    
    public function getMaxValue() {
        return $this->maxValue;
    }

    public function setMaxValue($maxValue) {
        $this->maxValue = $maxValue;
    }
    
    public function getStep() {
        return $this->step;
    }

    public function setStep($step) {
        $this->step = $step;
    }
    
    public function getUnits() {
        return $this->units;
    }

    public function setUnits($units) {
        $this->units = $units;
    }
    
    public function getRandom() {
        return $this->random;
    }

    public function setRandom($random) {
        $this->random = $random;
    }
    
    public function getSimulation() {
        return $this->simulation;
    }

    public function setSimulation($simulation) {
        $this->simulation = $simulation;
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