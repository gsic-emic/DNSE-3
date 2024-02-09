<?php

/**
 * return the text for a field that shows information 
 * about a date related to another date
 * @param string $date the date from which get the information
 * @param type $now_date the date to compare
 * @return string the text for the field
 */
function getDateFieldText($date, $now_date) {
    $date_field_text = "";
    //get the timestamp value for the date provided
    $date_dt = new DateTime($date, new DateTimeZone('Europe/Madrid'));
    $date_dt->modify('+1 hour'); //important!! add one hour to match the local time
    $date_ts = $date_dt->getTimestamp();

    //get the timestamp value for now
    //get the date in the same format that the date
    //$now_date = date('Y-m-d\TH:i:s\Z', time());
    $now_dt = new DateTime($now_date, new DateTimeZone('Europe/Madrid'));
    $now_ts = $now_dt->getTimestamp();

    //get the difference in seconds between both timestamps
    $diff_seconds = $now_ts - $date_ts;
    if ($diff_seconds < 60) {//less than a minute
        $date_field_text = "Hace menos de un minuto";
    } else if ($diff_seconds < 60 * 60) {//less than an hour
        $minutes = floor($diff_seconds / 60);
        $date_field_text = "Hace " . $minutes;
        ($minutes == 1) ? $date_field_text .= " minuto" : $date_field_text .= " minutos";
    } else if ($diff_seconds < 60 * 60 * 24) {//less than a day
        $hours = floor($diff_seconds / (60 * 60));
        $date_field_text = "Hace " . $hours;
        ($hours == 1) ? $date_field_text .= " hora" : $date_field_text .= " horas";
    } else {
        $date_field_text .= $date_dt->format('d-m-Y');
    }
    return $date_field_text;
}


?>
