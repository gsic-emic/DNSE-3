/**
 * The description of the information for a user
 */
var User = function(parameters){
	
    var data = parameters;
	
    this.getData = function(){
        return data;
    },
    
    this.getUsername = function(){
        return data.username;
    },
    
    this.setUsername = function(username){
        data.username = username;
    },

    this.getCurrentSimulations = function(){
        return data.currentSimulations;
    },
    
    this.setCurrentSimulations = function(currentSimulations){
        data.currentSimulations = currentSimulations;
    },
    
    this.getMaxSimulations = function(){
        return data.maxSimulations;
    },
    
    this.setMaxSimulations = function(maxSimulations){
        data.maxSimulations = maxSimulations;
    }
    
    this.getPercentage = function(decimals){
        var decs = 0;
        if (typeof decimals != 'undefined'){
            decs = decimals;
        }
        //If the user has no available simulations or the limit has been reached (or exceeded) we return the 100
        if (data.maxSimulations == 0 || (data.currentSimulations >= data.maxSimulations)){
            return 100;
        }
        var value = (data.currentSimulations / data.maxSimulations)*100;
        if (decs!=0){
            return Number(Math.round(value + 'e' + decs) + 'e-' + decs);
        }else{
            return Number(Math.round(value));
        }
    }
}
