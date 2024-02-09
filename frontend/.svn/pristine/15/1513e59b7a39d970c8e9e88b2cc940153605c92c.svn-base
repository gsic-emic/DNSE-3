/**
 * The values provided for a parameter in a simulation
 */
var ParameterResource = function(parameters){
	
    var data = parameters;
	
    this.getData = function(){
        return data;
    },
    
    this.getName = function(){
        return data.name;
    },
    
    this.setName = function(name){
        data.name = name;
    },
    
    this.getValueType = function(){
        return data.valueType;
    },
    
    this.setValueType = function(valueType){
        data.valueType = valueType;
    },
    
    this.getValue = function(){
        return data.value;
    },
    
    this.setValue = function(value){
        data.value = value;
    },
    
    this.getValues = function(){
        return data.values;
    },
    
    this.setValues = function(values){
        data.values = values;
    },
    
    this.getMinValue = function(){
        return data.minValue;
    },
    
    this.setMinValue = function(minValue){
        data.minValue = minValue;
    },
    
    this.getMaxValue = function(){
        return data.maxValue;
    },
    
    this.setMaxValue = function(maxValue){
        data.maxValue = maxValue;
    },
    
    this.getStep = function(){
        return data.step;
    },
    
    this.setStep = function(step){
        data.step = step;
    },
    
    this.getUnits = function(){
        return data.units;
    },
    
    this.setUnits = function(units){
        data.units = units;
    },
    
    this.getRamdom = function(){
        return data.random;
    },
    
    this.setRandom = function(random){
        data.random = random;
    }
    
    /**
     * set the value by using a string with the following accepted formats:
     * - one value. Example: value
     * - several values(comma separated values). Example: value1, value2, value3 
     * - range of values(min:step:max). Example: 1:3:9
     */
    this.setValueDnse3Format = function(value, simulation_type){
        if (typeof(data) !== 'undefined'){
            if (typeof(data.minValue) !== 'undefined') {
                delete data.minValue;
            }
            if (typeof(data.step) !== 'undefined') {
                delete data.step;
            }
            if (typeof(data.maxValue) !== 'undefined') {
                delete data.maxValue;
            }
            if (typeof(data.value) !== 'undefined') {
                delete data.value;
            }
            if (typeof(data.values) !== 'undefined') {
                delete data.values;
            }
            if (typeof(data.random) !== 'undefined') {
                delete data.random;
            }
        }
        
        //lets assume that the string is valid
        var valueDnse3Format = value.replace(new RegExp(' ', 'g'), '');//remove the black spaces
        if (valueDnse3Format.indexOf(",")!=-1 && valueDnse3Format.indexOf(":")==-1){//several values separated by commas
            this.setValues(valueDnse3Format.split(","));
        }else if (valueDnse3Format.indexOf(":")!=-1){//a range of values
            var range = valueDnse3Format.split(":"); 
            //min:step:max
            if (range.length==3){
                this.setMinValue(range[0]);
                this.setStep(range[1]);
                this.setMaxValue(range[2])
            }
        }else{//only one value
            if (simulation_type=="single"){
                this.setValue(value);
            }else if (simulation_type=="sweep"){
                var values = new Array();
                values.push(value);
                this.setValues(values);
            }
        }
        if (value=="random"){
            this.setRandom(true);
        }else{
            this.setRandom(false);
        }
    }
    
    this.getValueDnse3Format = function(simulation_type){
        var valueDnse3Format = "";
        if (simulation_type=="single" && this.getValue()!=null){
            valueDnse3Format = this.getValue();
        }else if (simulation_type == "sweep"){
            if (this.getValues()!=null){
                for (var i = 0; i < this.getValues().length; i++)
                {
                    valueDnse3Format += this.getValues()[i];
                    if (i < (this.getValues().length-1)){
                        valueDnse3Format += ",";
                    }
                }
            }else if (this.getMinValue()!=null && this.getStep()!=null && this.getMaxValue()!=null){
                valueDnse3Format += this.getMinValue() + ":" + this.getStep() + ":" + this.getMaxValue(); 
            }
        }
        if ((valueDnse3Format == "" || valueDnse3Format == null) && this.getRamdom() == true){
            valueDnse3Format = "random";
        }
        return valueDnse3Format;
    }
    
}
