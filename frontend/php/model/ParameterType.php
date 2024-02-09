<?php

/*
 * Enum class for the different types of parameters
 */
class ParameterType{
    
    /**
     * The value for the parameter is a string
     */
    const STRING_VALUE = "STRING_VALUE";
    /**
     * The value for the parameter is an integer value
     */
    const INTEGER_VALUE = "INTEGER_VALUE";
    /**
     * The value for the parameters is a decimal value
     */
    const RATIONAL_VALUE = "RATIONAL_VALUE";
    /**
     * The value for the parameter is a random value
     */
    const SEED = "SEED";
    
    /**
     * Get the description for a parameter type
     * @param type $parameter_type
     * @return string 
     */
    public static function getDescription($parameter_type){
        $description = "";
        switch($parameter_type){
            case ParameterType::STRING_VALUE:
                $description = "Palabra";
                break;
            case ParameterType::INTEGER_VALUE;
                $description = "Número entero";
                break;
            case ParameterType::RATIONAL_VALUE;
                $description = "Número decimal";
                break;
            case ParameterType::SEED;
                $description = "Semilla";
                break;
        }
        return $description;
    }
}
?>
