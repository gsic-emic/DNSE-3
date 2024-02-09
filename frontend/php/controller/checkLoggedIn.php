<?php
require_once(dirname(__FILE__)."/../conf/properties.php");
/**
 * Check if the user is logged in and redirect to the login page if necessary
 */
//start the session
//session_start();
if (!isset($_SESSION) || !isset($_SESSION["username"]) || empty($_SESSION["username"]) || !isset($_SESSION["password"]) || empty($_SESSION["password"])){
    header('Location: '. $CONF_PROP["dnse3_root"]. "/php/view/login.php");
}
?>
