<?php

require_once(dirname(__FILE__)."/../../php/model/OutputFileStructuresManager.php");
/**
 * Description of ParameterListResource
 */
class OutputFileStructureListResource {
    
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
     * GET request to get the list of output file structures for the username in a simulation project as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $output_file_str_manager = new OutputFileStructuresManager($this->username, $this->projectId);
        $response =  $output_file_str_manager->getOutputFileStructures();
        $status = $response["status"];
        if ($status == 200 || $status == 204){ 
            $output_file_structures = $response["data"];
            $output_file_str_props = array();
            foreach ($output_file_structures as $output_file_structure) {
                $properties = $output_file_structure->getProperties();
                array_push($output_file_str_props, $properties);
            }
            $json_data = $this->generateJson($output_file_str_props);
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
