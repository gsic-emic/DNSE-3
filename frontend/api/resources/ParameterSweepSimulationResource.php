<?php

require_once(dirname(__FILE__)."/../../php/model/ParameterSweepSimulationsManager.php");
/**
 * Description of SimulationProjectResource
 */
class ParameterSweepSimulationResource {
    
    private $method;
    private $accept;
    
    private $username;
    private $projectId;
    private $simulationId;
    private $request_vars;

    function __construct($method, $accept, $username, $projectId, $simulationId, $request_vars = null) {
        $this->method = $method;
        $this->accept = $accept;
        
        $this->username = $username;
        $this->projectId = $projectId;
        $this->simulationId = $simulationId;
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
     * GET request to get a parameter sweep simulation as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $param_sweep_sim_controller = new ParameterSweepSimulationsManager($this->username, $this->projectId);
        $response = $param_sweep_sim_controller->getParameterSweepSimulation($this->simulationId);
        $param_sweep_simulation = $response["data"];
        $status = $response["status"];
        if ($status == 200){
            $properties = $param_sweep_simulation->getProperties();
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
    
    public function post(){
        $param_sweep_sim_controller = new ParameterSweepSimulationsManager($this->username, $this->projectId);
        $response = $param_sweep_sim_controller->getParameterSweepSimulation($this->simulationId);
        $operation = $this->request_vars["operation"];
        $param_sweep_simulation = $response["data"];
        
        switch($operation){
            case SimulationOperation::START:  
                                                if (strcmp($param_sweep_simulation->getStatus(), SimulationStatus::WAITING)==0 ||
                                                    strcmp($param_sweep_simulation->getStatus(), SimulationStatus::PAUSED)==0){
                                                    $status = $param_sweep_sim_controller->startParameterSweepSimulation($this->simulationId);
                                                    if ($status == 202){
                                                        return new ResponseData(202, '', 'text/html');
                                                    }
                                                }else {
                                                    return new ResponseData(405, 'The ' . SimulationOperation::START . ' operation is not allowed for the ' . $param_sweep_simulation->getStatus() . ' status of the simulation', 'text/html');
                                                }
                                                break;
            case SimulationOperation::PAUSE:   if (strcmp($param_sweep_simulation->getStatus(), SimulationStatus::PROCESSING)==0){
                                                    $status = $param_sweep_sim_controller->pauseParameterSweepSimulation($this->simulationId);
                                                    if ($status == 202){
                                                        return new ResponseData(202, '', 'text/html');
                                                    }
                                                }else{
                                                    return new ResponseData(405, 'The ' . SimulationOperation::PAUSE . ' operation is not allowed for the ' . $param_sweep_simulation->getStatus() . ' status of the simulation', 'text/html');
                                                }
                                                break;
            case SimulationOperation::STOP:   if (strcmp($param_sweep_simulation->getStatus(), SimulationStatus::PROCESSING)==0){
                                                    $status = $param_sweep_sim_controller->stopParameterSweepSimulation($this->simulationId);
                                                    if ($status == 202){
                                                        return new ResponseData(202, '', 'text/html');
                                                    }
                                                }else{
                                                    return new ResponseData(405, 'The ' . SimulationOperation::STOP . ' operation is not allowed for the ' . $param_sweep_simulation->getStatus() . ' status of the simulation', 'text/html');
                                                }
                                                break;
            default:                            new ResponseData(400, 'The ' . $operation . ' operation is not a valid operation for the simulation', 'text/html');
        }
        return new ResponseData(500, '', 'text/html');
    }
    
    public function put() {
        $param_sweep_sim_controller = new ParameterSweepSimulationsManager($this->username, $this->projectId);
        $name = $this->request_vars["name"];
        $numRepetitions = $this->request_vars["numRepetitions"];
        $priority = $this->request_vars["priority"];
        
        
        if (isset($this->request_vars["outputFiles"]) && isset($this->request_vars["parameters"])){
            $outputFiles = $this->request_vars["outputFiles"];
            $parameters = $this->request_vars["parameters"];
            $status = $param_sweep_sim_controller->updateAllParameterSweepSimulation($this->simulationId, $name, $numRepetitions, $priority, $outputFiles, $parameters);
        }else{
            $status = $param_sweep_sim_controller->updateParameterSweepSimulation($this->simulationId, $name, $numRepetitions, $priority);
        }
        if ($status == 200 || $status == 204){
            return new ResponseData(204, '', 'text/html');
        }
        return new ResponseData(500, '', 'text/html');
    }
    
    public function delete(){
        $param_sweep_sim_controller = new ParameterSweepSimulationsManager($this->username, $this->projectId);
        $status = $param_sweep_sim_controller->deleteParameterSweepSimulation($this->simulationId);
        if ($status == 202){
            return new ResponseData(202, '', 'text/html');
        }
        return new ResponseData(500, '', 'text/html');
    }
}

?>