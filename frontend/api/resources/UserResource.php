<?php

require_once(dirname(__FILE__)."/../../php/model/UsersManager.php");
/**
 * Description of UserResource
 */
class UserResource {
    
    private $method;
    private $accept;
    
    private $username;
    private $request_vars;

    function __construct($method, $accept, $username, $request_vars = null) {
        $this->method = $method;
        $this->accept = $accept;
        
        $this->username = $username;
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
        $response = new ResponseData(501, '', 'text/html');
        return $response;
    }

    /**
     * GET request to get a user as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $user_controller = new UsersManager();
        $response = $user_controller->getUser($this->username);
        $status = $response["status"];
        if ($status == 200){
            $user = $response["data"];
            $properties = $user->getProperties();
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
}

?>
