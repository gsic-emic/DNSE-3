<?php
require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../core/authenticate.php");
require_once(dirname(__FILE__)."/../core/dates.php");

$error_credentials = null;
$unable_to_connect = null;
$unable_to_connect_remote_server = null;
//start the session to save the variables
session_start();
if (isset($_POST)){
    //set the session variables if it is possible
    if (isset($_POST["loginUsername"]) && !empty($_POST["loginUsername"])){
        $_SESSION["username"] = $_POST["loginUsername"];
    }
    if (isset($_POST["loginPassword"]) && !empty($_POST["loginPassword"])){
        $_SESSION["password"] = $_POST["loginPassword"];
    }
    if ( isset($_POST["loginUsername"]) && !empty($_POST["loginUsername"]) && isset($_POST["loginPassword"]) && !empty($_POST["loginPassword"])){
        $authenticated = ldap_authenticate($_SESSION["username"], $_SESSION["password"]);
        if ($authenticated === 1){
           $error_credentials = false;
        }  else {
            // remove all session variables
            session_unset();
            // destroy the session 
            session_destroy();
            if ($authenticated === 0){
                $error_credentials = true;
            }else if ($authenticated === -1){
                $unable_to_connect = true;
            }
        }
    }
}
//make a request to get the users just to check if the remote server is available
require_once(dirname(__FILE__)."/../model/UsersManager.php");
$uc = new UsersManager();
$response = $uc->getUsers();
if ($response["status"] == 0 && ($response["errno"] == 7 || $response["errno"] == 28)){
    $unable_to_connect_remote_server = true;
    $error_credentials = null;
    $unable_to_connect = null;
    require_once(dirname(__FILE__)."/../view/login.php");
}
    
//redirect depending on the login result
if (isset($_SESSION["username"]) && !empty($_SESSION["username"]) && isset($_SESSION["password"]) && !empty($_SESSION["password"]) && !$error_credentials){
    $username = $_SESSION["username"];
    //require_once(dirname(__FILE__)."/../model/UsersManager.php");
    require_once(dirname(__FILE__)."/../model/SimulationProjectsManager.php");
    $uc = new UsersManager();
    $response = $uc->getUser($username);
    if ($response["status"] == 0 && $response["errno"] == 7){
        $unable_to_connect_remote_server = true;
        require_once(dirname(__FILE__)."/../view/login.php");
    }else{
        $user = $response["data"];
        $spc = new SimulationProjectsManager($username);
        $response = $spc->getSimulationProjects();
        $sim_projects = $response["data"];

        $now_date = date('Y-m-d\TH:i:s\Z', time());

        //header('Location: '. $CONF_PROP["dnse3_root"]. "/php/view/main.php");
        require_once(dirname(__FILE__)."/../view/main.php");
    }
}else{
    //header('Location: '. $CONF_PROP["dnse3_root"]. "/php/view/login.php");
    require_once(dirname(__FILE__)."/../view/login.php");
}



?>
