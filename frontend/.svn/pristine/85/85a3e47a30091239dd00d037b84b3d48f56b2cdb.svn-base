<?php
require_once(dirname(__FILE__)."/../conf/properties.php");

function add_info_log($message){
    file_put_contents(LOG_INFO_FILE, date("Y-m-d H:i:s"). ". ". $message . PHP_EOL, FILE_APPEND);
}

function add_error_log($message){
    file_put_contents(LOG_ERROR_FILE, date("Y-m-d H:i:s"). ". ". $message. PHP_EOL, FILE_APPEND);
}
?>
