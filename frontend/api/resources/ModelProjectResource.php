<?php
require_once(dirname(__FILE__)."/../../php/model/SimulationProjectsManager.php");

class ModelProjectResource {
    
    private $method;
    private $accept;
    
    private $username;
    private $projectId;

    function __construct($method, $accept, $username, $projectId) {
        $this->method = $method;
        $this->accept = $accept;
        
        $this->username = $username;
        $this->projectId = $projectId;
    }

    function exec() {
        if (strcmp($this->method, "get") == 0) {
            return $this->getZip();
        }
        $response = new ResponseData(501, '', 'text/html');
        return $response;
    }

    function getZip() {
        $projectController = new SimulationProjectsManager($this->username);
        $response = $projectController->getModelProject($this->projectId);
        $file_path = $response["data"]["file_path"];
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