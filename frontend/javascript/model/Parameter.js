/**
 * The description of a parameter available for a simulation project
 */
var Parameter = function(parameters){
	
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
    
    this.getType = function(){
        return data.type;
    },
    
    this.setType = function(type){
        data.type = type;
    },
    
    this.getDefaultValue = function(){
        return data.defaultValue;
    },
    
    this.setDefaultValue = function(defaultValue){
        data.defaultValue = defaultValue;
    },
    
    this.getPossibleValues = function(){
        return data.possibleValues;
    },
    
    this.setPossibleValues = function(possibleValues){
        data.possibleValues = possibleValues;
    },
    
    this.getGreaterThan = function(){
        return data.greaterThan;
    },
    
    this.setGreaterThan = function(greaterThan){
        data.greaterThan = greaterThan;
    },
    
    this.getLessThan = function(){
        return data.lessThan;
    },
    
    this.setLessThan = function(lessThan){
        data.lessThan = lessThan;
    },
    
    this.getGreaterThanOrEqualTo = function(){
        return data.greaterThanOrEqualTo;
    },
    
    this.setGreaterThanOrEqualTo = function(greaterThanOrEqualTo){
        data.greaterThanOrEqualTo = greaterThanOrEqualTo;
    },
    
    this.getLessThanOrEqualTo = function(){
        return data.lessThanOrEqualTo;
    },
    
    this.setLessThanOrEqualTo = function(lessThanOrEqualTo){
        data.lessThanOrEqualTo = lessThanOrEqualTo;
    }
    
}
