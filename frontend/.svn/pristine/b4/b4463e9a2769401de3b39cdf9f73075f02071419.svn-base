<?php

require_once(dirname(__FILE__)."/../../php/model/SingleSimulationsManager.php");
/**
 * Description of UserResource
 */
class ResultsResource {
    
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
            //if (strpos($this->accept, 'application/zip') !== false) {
                return $this->getZip();
            //}
        }
        $response = new ResponseData(501, '', 'text/html');
        return $response;
    }

    /**
     * GET request to get the results as a ZIP file
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getZip() {
        if ($this->simulationType == "single"){
            $single_sim_controller = new SingleSimulationsManager($this->username, $this->projectId);
            $response = $single_sim_controller->getSingleSimulationResults($this->simulationId);
        }else if ($this->simulationType == "parameter_sweep"){
            $param_sweep_sim_controller = new ParameterSweepSimulationsManager($this->username, $this->projectId);
            $response =  $param_sweep_sim_controller->getParameterSweepSimulationResults($this->simulationId);
        }
        $file_path = $response["data"]["file_path"];
        //$filename = basename($file_path);
        $filename = $response["data"]["file_name"];

        if (file_exists($file_path)) {
            header('Content-Description: File Transfer');
            header('Content-Type: application/octet-stream');
            header('Content-Disposition: attachment; filename=' . $filename);
            header('Content-Transfer-Encoding: binary');
            header('Expires: 0');
            header('Cache-Control: must-revalidate');
            header('Pragma: public');
            header('Content-Length: ' . filesize($file_path));
            ob_clean();
            flush();
            readfile($file_path);
            //We no longer need the file, so we delete it
            unlink($file_path);
            exit;
        }
        return new ResponseData(500, '', 'text/html');
        
    }
}

?>
