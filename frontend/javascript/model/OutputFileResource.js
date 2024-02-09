/**
 * The description of a output file resource.
 * A resource of type file that contains some output information
 * gathered from the simulation
 */
var OutputFileResource = function(parameters){
    	
    var data = parameters;
	
    this.getData = function(){
        return data;
    },
    
    /**
     * get the name of the output file
     */
    this.getOutputFileName = function(){
        return data.outputFileName;
    },
    
    /**
     * set the name for the output files
     */
    this.setOutputFileName = function(outputFileName){
        data.outputFileName = outputFileName;
    },
    
    /**
     * get the structure of the output file
     * @return the OutputFileStructure object containing the structure of the output file
     */
    this.getOutputFileStructure = function(){
        return data.outputFileStructure;
    },
    
    /**
     * set the structure for the output file
     * @param outputFileStructure the structure of the output file
     */
    this.setOutputFileStructure = function(outputFileStructure){
        data.outputFileStructure = outputFileStructure;
    }
    
     /**
     * get the type of the output file
     * @return the type of output file
     */
    this.getType = function(){
        return data.type;
    },
    
    /**
     * set the type for the output file
     * @param type the type for the output file
     */
    this.setType = function(type){
        data.type = type;
    }
}

