/**
 * The description of the information for a single simulation
 */
var SingleSimulation = function(parameters){
    
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
    
    this.getCreationDate = function(){
        return data.creationDate;
    },
    
    this.setCreationDate = function(creationDate){
        data.creationDatee = creationDate;
    },
    
    this.getUpdateDate = function(){
        return data.updateDate;
    },
    
    this.setUpdateDate = function(updateDate){
        data.updateDate = updateDate;
    },
    
    this.getStatus = function(){
        return data.status;
    },
    
    this.setStatus = function(status){
        data.status = status;
    },
    
    this.getCompletedSimulations = function(){
        return data.completedSimulations;
    },
    
    this.setCompletedSimulations = function(completedSimulations){
        data.completedSimulations = completedSimulations;
    },
    
    this.getTotalSimulations = function(){
        return data.totalSimulations;
    },
    
    this.setTotalSimulations = function(totalSimulations){
        data.totalSimulations = totalSimulations;
    },
    
    this.getStartDate = function(){
        return data.startDate;
    },
    
    this.setStartDate = function(startDate){
        data.startDate = startDate;
    },
    
    this.getFinishedDate = function(){
        return data.finishedDate;
    },
    
    this.setFinishedDate = function(finishedDate){
        data.finishedDate = finishedDate;
    },
    
    this.getNumRepetitions = function(){
        return data.numRepetitions;
    },
    
    this.setNumRepetitions = function(numRepetitions){
        data.numRepetitions = numRepetitions;
    },

    this.getPriority = function(){
        return data.priority;
    },

    this.setPriority = function(priority){
        data.priority = priority;
    },
    
    this.getPercentageCompleted = function(decimals){
        var decs = 0;
        if (typeof decimals != 'undefined'){
            decs = decimals;
        }
        if (data.totalSimulations == 0 || (data.completedSimulations >= data.totalSimulations)){
            return 100;
        }
        var value = (data.completedSimulations / data.totalSimulations)*100;
        if (decs!=0){
            return Number(Math.round(value + 'e' + decs) + 'e-' + decs);
        }else{
            return Number(Math.round(value));
        }
    }
    
};


