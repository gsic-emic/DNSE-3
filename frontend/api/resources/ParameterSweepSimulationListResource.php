<?php

require_once(dirname(__FILE__)."/../../php/model/ParameterSweepSimulationsManager.php");
/**
 * Description of ParameterListResource
 */
class ParameterSweepSimulationListResource {
    
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
     * GET request to get the list of parameter sweep simulations for the username in a simulation project as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $param_sweep_sims_manager = new ParameterSweepSimulationsManager($this->username, $this->projectId);
        $response = $param_sweep_sims_manager->getParameterSweepSimulations();
        $param_sweep_sims = $response["data"];
        $status = $response["status"];
        if ($status == 200 || $status == 204){
            $param_sweep_sims_props = array();
            foreach ($param_sweep_sims as $param_sweep_sim) {
                $properties = $param_sweep_sim->getProperties();
                array_push($param_sweep_sims_props, $properties);
            }
            $json_data = $this->generateJson($param_sweep_sims_props);
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
     * POST request to create a new parameter sweep simulation
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function post(){
        $param_sweep_sim_man = new ParameterSweepSimulationsManager($this->username, $this->projectId);
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
        $status = $param_sweep_sim_man->createParameterSweepSimulation($name, $numRepetitions, $priority, $output_files, $parameters);
        if ($status == 201){
            return new ResponseData(201, '', 'text/html');
        }else{
            return new ResponseData(500, "There was an error while trying to create the parameter sweep simulation", 'text/html');
        } 
    }
}

?>
