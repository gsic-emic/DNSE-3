<?php

require_once(dirname(__FILE__)."/../../php/model/ParameterResourcesManager.php");
/**
 * Description of ParameterResourceListResource
 */
class ParameterResourceListResource {
    
    private $method;
    private $accept;
    
    private $username;
    private $projectId;
    private $simulationId;
    private $simulationType;

    function __construct($method, $accept, $username, $projectId, $simulationId, $simulationType) {
        $this->method = $method;
        $this->accept = $accept;
        
        $this->username = $username;
        $this->projectId = $projectId;
        $this->simulationId = $simulationId;
        $this->simulationType = $simulationType;
    }

    /**
     * Call for the suitable method according to the REST request
     * @return ResponseData The response to the REST request
     */
    function exec() {
        if (strcmp($this->method, "get") == 0) {
            if (strpos($this->accept, 'application/json') !== false) {
                return $this->getJson();
            }
        }
        $response = new ResponseData(501, '', 'text/html');
        return $response;
    }

    /**
     * GET request to get as a JSON the list of parameter resources set by the username in a simulation of a simulation project
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $param_resources_manager = new ParameterResourcesManager($this->username, $this->projectId, $this->simulationId, $this->simulationType);
        $response = $param_resources_manager->getParameterResources();
        $status = $response["status"];
        if ($status == 200 || $status == 204){
            $param_resources = $response["data"];
            $param_res_props = array();
            foreach ($param_resources as $param_resource) {
                $properties = $param_resource->getProperties();
                array_push($param_res_props, $properties);
            }
            $json_data = $this->generateJson($param_res_props);
            return new ResponseData(200, $json_data, 'application/json');
        }else{
            return new ResponseData($status, '', 'text/html');
        }
    }
    
    /**
     * Convert the data to a JSON
     * @param type $data the content to convert to JSON
     * @return type The content as an JSON of false if there is an error
     */
    private function generateJson($data) {
        $json_data = json_encode($data);
        return $json_data;
    }
}

?>
