<?php

require_once(dirname(__FILE__)."/../../php/model/OutputFilesManager.php");
/**
 * Description of OutputFileListResource
 */
class OutputFileListResource {
    
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
     * GET request to get the list of output files for the username in a simulation project as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $output_files_manager = new OutputFilesManager($this->username, $this->projectId);
        $response =  $output_files_manager->getOutputFiles();
        $status = $response["status"];
        if ($status == 200 || $status == 204){
            $output_files = $response["data"];
            $output_file_props = array();
            foreach ($output_files as $output_file) {
                $properties = $output_file->getProperties();
                array_push($output_file_props, $properties);
            }
            $json_data = $this->generateJson($output_file_props);
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
