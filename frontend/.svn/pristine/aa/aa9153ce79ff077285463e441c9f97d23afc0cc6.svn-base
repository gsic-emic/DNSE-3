var ParameterType = {
    /**
     * The value for the parameter is a string
     */
    STRING_VALUE: "STRING_VALUE",
    /**
     * The value for the parameter is an integer value
     */
    INTEGER_VALUE: "INTEGER_VALUE",
    /**
     * The value for the parameters is a decimal value
     */
    RATIONAL_VALUE: "RATIONAL_VALUE",
    /**
     * The value for the parameter is a random value
     */
    SEED: "SEED",
    
    getDescription: function(parameter_type){
        var description = "";
        switch(parameter_type){
            case ParameterType.STRING_VALUE:
                description = "Palabra";
                break;
            case ParameterType.INTEGER_VALUE:
                description = "Número entero";
                break;
            case ParameterType.RATIONAL_VALUE:
                description = "Número decimal";
                break;
            case ParameterType.SEED:
                description = "Semilla";
                break;
        }
        return description;
    }
};
