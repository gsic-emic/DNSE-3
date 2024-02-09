<?php

require_once(dirname(__FILE__)."/../../php/model/SingleSimulationsManager.php");
/**
 * Description of ParameterListResource
 */
class SingleSimulationListResource {
    
    private $method;
    private $accept;
    
    private $username;
    private $projectId;
    private $request_vars;

    function __construct($method, $accept, $username, $projectId, $request_vars) {
        $this->method = $method;
        $this->accept = $accept;
        
        $this->username = $username;
        $this->projectId = $projectId;
        $this->request_vars = $request_vars;
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
        if (strcmp($this->method, "post") == 0){
            return $this->post();
        }
        $response = new ResponseData(501, '', 'text/html');
        return $response;
    }

    /**
     * GET request to get the list of single simulations for the username in a simulation project as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $single_sims_manager = new SingleSimulationsManager($this->username, $this->projectId);
        $response = $single_sims_manager->getSingleSimulations();
        $single_sims = $response["data"];
        $status = $response["status"];
        if ($status == 200 || $status == 204){
            $single_sims_props = array();
            foreach ($single_sims as $single_sim) {
                $properties = $single_sim->getProperties();
                array_push($single_sims_props, $properties);
            }
            $json_data = $this->generateJson($single_sims_props);
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
    
    /**
     * POST request to create a new single simulation
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function post(){
        $single_sim_man = new SingleSimulationsManager($this->username, $this->projectId);
        $name = $this->request_vars["name"];
        $numRepetitions = $this->request_vars["numRepetitions"];
        $priority = $this->request_vars["priority"];
        
        if (isset($this->request_vars["outputFiles"])){
            $output_files = $this->request_vars["outputFiles"];
        }else{
            $output_files = array();
        }
        if (isset($this->request_vars["parameters"])){
            $parameters = $this->request_vars["parameters"];
        }else{
            $parameters = array();
        }
        $status = $single_sim_man->createSingleSimulation($name, $numRepetitions, $priority, $output_files, $parameters);
        if ($status == 201){
            return new ResponseData(201, '', 'text/html');
        }else{
            return new ResponseData(500, "There was an error while trying to create the simulation project", 'text/html');
        } 
    }
}

?>
