<?php
require_once(dirname(__FILE__)."/log_functions.php");

function ldap_authenticate($username, $password) {
    add_info_log("ldap_authenticate: " . $username. "\n");
	if(empty($username) || empty($password)) return false;

    /*
     * ldap_authenticate
     */

    if("OK") {
        return 1;
    } else {
        return 0;
    }
}
?>
