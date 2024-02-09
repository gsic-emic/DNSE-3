<?php

require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../model/ParameterResource.php");

/**
 * Manager for the parameter resources (thus is, the parameters set in a simulation)
 */

class ParameterResourcesManager{
     
    /**
     * @var string the base URI to the parameter resources resource for a single simulation in the API
     */
    private $api_single_param_resources_uri; 
    
    /**
     * @var string the base URI to the parameter resources resource for a parameter sweep simulation in the API
     */
    private $api_sweep_param_resources_uri; 
    
    /**
     *
     * @var string the username 
     */
    private $username;
    
    /**
     *
     * @var string the project id
     */
    private $projectId;
    
     /**
     *
     * @var string the simulation id
     */
    private $simulationId;
    
    /**
     *
     * @var string the simulation type (single, parameter_sweep)
     */
    private $simulationType;
    
    public function __construct($username, $projectId, $simulationId, $simulationType) {
        global $CONF_PROP;
        $this->api_single_param_resources_uri = $CONF_PROP["api_url"] . "users/". $username. "/projects/" . $projectId . "/singlesimulations/" . $simulationId . "/parameters/";
        $this->api_sweep_param_resources_uri = $CONF_PROP["api_url"] . "users/". $username. "/projects/" . $projectId . "/parametersweepsimulations/" . $simulationId . "/parameters/";
        $this->username = $username;
        $this->projectId = $projectId;
        $this->simulationId = $simulationId;
        $this->simulationType = $simulationType;
    }
    
    private function getApiParamResourcesUri(){
        if ($this->simulationType == "single"){
            return $this->api_single_param_resources_uri;
        }else if ($this->simulationType == "parameter_sweep"){
            return $this->api_sweep_param_resources_uri;
        }
    }
    
    public function getParameterResources(){
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $this->getApiParamResourcesUri());

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);
        
        //Custom headers
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Accept: application/json; charset=UTF-8'));

        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);

        // perform the HTTP request
        $out = curl_exec($ch);

        // get answer HTTP
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

        // get cURL error code
        $curl_errno = curl_errno($ch);
        
        // free resources
        curl_close($ch);
        
        if (!$curl_errno && ($status == 200 || $status == 204)) {
            $param_resources = array();
            $json_result = json_decode($out, true);
            foreach($json_result as $parameterRes){
                $paramResObj = new ParameterResource($parameterRes);      
                array_push($param_resources, $paramResObj);
            }
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $param_resources
            );
            return $response;
        }else{
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $out
            );
            return $response;
        }
    }
}
?>


