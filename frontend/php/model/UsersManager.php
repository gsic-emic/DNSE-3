<?php
require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../model/User.php");

/**
 * Manager for the users
 */
class UsersManager{
    
    private $api_users_url;
    
    public function __construct() {
        global $CONF_PROP;
        $this->api_users_url = $CONF_PROP["api_url"] . "users/";
    }

    public function getUsers(){        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $this->api_users_url);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 10);
        
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
            $users = array();
            if ($status == 200){
                $json_result = json_decode($out, true);
                foreach($json_result as $user){
                    $userObj = new User($user);
                    array_push($users, $userObj);
                }
            }
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $users
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
    
    
    public function getUser($username){
        $api_user_url = $this->api_users_url . $username. "/";
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $api_user_url);

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
        
        if (!$curl_errno && ($status == 200)) {
            $json_result = json_decode($out, true);
            $user = new User($json_result);
            //return $sim_project;
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $user
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
