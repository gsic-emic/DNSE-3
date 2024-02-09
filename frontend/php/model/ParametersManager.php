<?php

require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../model/Parameter.php");

/**
 * Manager for the parameter discriptions
 */

class ParametersManager{
     
    /**
     *
     * @var string the base URI to the parameter descriptions resource in the API
     */
    private $api_param_descriptions_uri; 
    
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
        $this->api_param_descriptions_uri = $CONF_PROP["api_url"] . "users/". $username. "/projects/" . $projectId . "/parameters/";
        $this->username = $username;
        $this->projectId = $projectId;
    }
    
    public function getParameters(){
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $this->api_param_descriptions_uri);

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
            $param_descriptions = array();
            $json_result = json_decode($out, true);
            foreach($json_result as $parameter){
                $paramDescObj = new Parameter($parameter);      
                array_push($param_descriptions, $paramDescObj);
            }
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $param_descriptions
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

