<?php
require_once(dirname(__FILE__)."/../conf/properties.php");
/**
 * user logout
 */
// start the session
session_start();
// remove all session variables
session_unset();
// destroy the session 
session_destroy();
//redirect to the login page
header('Location: '. $CONF_PROP["dnse3_root"]. "/php/view/login.php");
?>