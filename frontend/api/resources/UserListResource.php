<?php

require_once(dirname(__FILE__)."/../../php/model/UsersManager.php");
/**
 * Description of UserListResource
 */
class UserListResource {
    
    private $method;
    private $accept;

    function __construct($method, $accept) {
        $this->method = $method;
        $this->accept = $accept;
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
     * GET request to get the list of users as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $user_controller = new UsersManager();
        $response = $user_controller->getUsers();
        $users= $response["data"];
        $status = $response["status"];
        if ($status == 200 || $status == 204){
            $users_props = array();
            foreach ($users as $user) {
                $properties = $user->getProperties();
                array_push($users_props, $properties);
            }
            $json_data = $this->generateJson($users_props);
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
