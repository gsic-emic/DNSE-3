function dateToTimestamp(date){
    //get the timestamp in seconds instead of milliseconds
    return Math.round(date.getTime()/1000);
}

/**
 * return the date with this format: dd-mm-YYYY
 */
function dateFormat(date){
    var dd = date.getDate(); 
    var mm = date.getMonth()+1;//the first month is 0
    var yyyy = date.getFullYear(); 
    var hh = date.getHours();
    var ii = date.getMinutes();
    if(dd < 10){
        dd = '0' + dd;
    } 
    if(mm < 10){
        mm = '0' + mm;
    }
    if (hh < 10){
        hh = '0' + hh;
    }
    if (ii < 10){
        ii = '0' + ii;
    }
    return dd + "-" + mm + "-" + yyyy;
}

/**
 * return the date with this format: dd-mm-YYYY HH:ii
 */
function dateTimeFormat(date){
    var dd = date.getDate(); 
    var mm = date.getMonth()+1;//the first month is 0
    var yyyy = date.getFullYear(); 
    var hh = date.getHours();
    var ii = date.getMinutes();
    if(dd < 10){
        dd = '0' + dd;
    } 
    if(mm < 10){
        mm = '0' + mm;
    }
    if (hh < 10){
        hh = '0' + hh;
    }
    if (ii < 10){
        ii = '0' + ii;
    }
    return dd + "-" + mm + "-" + yyyy + " " + hh + ":" + ii;
}

/**
 * return the text for a field that shows information 
 * about a date related to another date
 * @param string date the date from which get the information
 * @param string now_date the date to compare
 * @return string the text for the field
 */
function getDateFieldText(date, now_date) {
    var date_field_text = "";
    var date_dt = new Date(date);
    //date_dt.setHours(date_dt.getHours()+1);//important!! add one hour to match the local time
    var date_ts = dateToTimestamp(date_dt);
            
    //var now_date = new Date();
    var now_ts = dateToTimestamp(now_date);
            
    //get the difference in seconds between both timestamps
    var diff_seconds = now_ts - date_ts;  
    if (diff_seconds < 60){//less than a minute
        date_field_text = "Hace menos de un minuto";
    }else if(diff_seconds < 60*60){//less than an hour
        var minutes = Math.floor(diff_seconds/60);
        date_field_text = "Hace " + minutes;
        (minutes == 1) ? date_field_text += " minuto" : date_field_text += " minutos";
    }else if (diff_seconds < 60*60*24){//less than a day
        var hours = Math.floor(diff_seconds/(60*60));
        date_field_text = "Hace " + hours;
        (hours == 1) ? date_field_text += " hora" : date_field_text += " horas";
    }else{
        date_field_text += dateFormat(date_dt);
    }
    return date_field_text;
}