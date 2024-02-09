<?php

require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../model/OutputFileStructure.php");

/**
 * Manager for the output file structures
 */

class OutputFileStructuresManager{
     
    /**
     *
     * @var string the base URI to the output file structure resources in the API
     */
    private $api_output_file_sctructures_uri; 
    
    /**
     *
     * @var type string the username 
     */
    private $username;
    
    /**
     *
     * @var type string the project id
     */
    private $projectId;
    
    public function __construct($username, $projectId) {
        global $CONF_PROP;
        $this->api_output_file_sctructures_uri = $CONF_PROP["api_url"] . "users/". $username. "/projects/" . $projectId . "/outputfilestructures/";
        $this->username = $username;
        $this->projectId = $projectId;
    }
    
    public function getOutputFileStructures(){
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $this->api_output_file_sctructures_uri);

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
            $output_file_structures = array();
            $json_result = json_decode($out, true);
            foreach($json_result as $outputFileStructure){
                $outputFileStrObj = new OutputFileStructure($outputFileStructure);      
                array_push($output_file_structures, $outputFileStrObj);
            }
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $output_file_structures
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
    
    public function getOutputFileStructure($output_file_structure_name){
        $api_output_file_structure_uri = $this->api_output_file_sctructures_uri . $output_file_structure_name;
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $api_output_file_structure_uri);

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
            $json_result = json_decode($out, true);
            $outputFileStrObj = new OutputFileStructure($json_result);      
            return $outputFileStrObj;
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


