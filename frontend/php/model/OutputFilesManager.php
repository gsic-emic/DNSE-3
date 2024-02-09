<?php

require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../model/OutputFile.php");

/**
 * Manager for the output files
 */

class OutputFilesManager{
     
    /**
     *
     * @var string the base URI to the output file resources in the API
     */
    private $api_output_files_uri; 
    
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
        $this->api_output_files_uri = $CONF_PROP["api_url"] . "users/". $username. "/projects/" . $projectId . "/outputfiles/";
        $this->username = $username;
        $this->projectId = $projectId;
    }
    
    public function getOutputFiles(){
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $this->api_output_files_uri);

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
            $output_files = array();
            $json_result = json_decode($out, true);
            foreach($json_result as $outputFile){
                $outputFileObj = new OutputFile($outputFile);      
                array_push($output_files, $outputFileObj);
            }
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $output_files
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
    
    public function getOutputFile($output_file_name){
        $api_output_file_uri = $this->api_output_files_uri . $output_file_name;
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $api_output_file_uri);

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
            $outputFileObj = new OutputFile($json_result);      
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $outputFileObj
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
}
?>


