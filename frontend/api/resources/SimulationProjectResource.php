<?php

require_once(dirname(__FILE__)."/../../php/model/SimulationProjectsManager.php");
/**
 * Description of SimulationProjectResource
 */
class SimulationProjectResource {
    
    private $method;
    private $accept;
    
    private $username;
    private $projectId;
    private $request_vars;

    function __construct($method, $accept, $username, $projectId, $request_vars = null) {
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
        if (strcmp($this->method, "put") == 0){
            return $this->put();
        }
        if (strcmp($this->method, "delete") == 0){
            return $this->delete();
        }
        $response = new ResponseData(501, '', 'text/html');
        return $response;
    }

    /**
     * GET request to get a simulation project as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $sim_proj_controller = new SimulationProjectsManager($this->username);
        $response = $sim_proj_controller->getSimulationProject($this->projectId);
        $status = $response["status"];
        if ($status == 200){
            $sim_project = $response["data"];
            $properties = $sim_project->getProperties();
            $json_data = $this->generateJson($properties);
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
    
    public function put() {
        $sim_proj_controller = new SimulationProjectsManager($this->username);
        $name = $this->request_vars["name"];
        $description = $this->request_vars["description"];
        $status = $sim_proj_controller->updateSimulationProject($this->projectId, $name, $description);
        if ($status == 200 || $status == 204){
            return new ResponseData(204, '', 'text/html');
        }
        return new ResponseData(500, '', 'text/html');
    }
    
    public function delete(){
        $sim_proj_controller = new SimulationProjectsManager($this->username);
        $status = $sim_proj_controller->deleteSimulationProject($this->projectId);
        if ($status == 202){
            return new ResponseData(202, '', 'text/html');
        }
        return new ResponseData(500, '', 'text/html');
    }
}

?>
