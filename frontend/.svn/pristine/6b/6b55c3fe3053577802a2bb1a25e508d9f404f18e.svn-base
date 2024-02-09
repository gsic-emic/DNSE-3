
function getBaseApiUrl(){
    //set the base path to the API URI
    var BaseApiUrl = "/api";
    if (window.location.href.indexOf("/DNSE3/") !==-1){
        BaseApiUrl  = "/DNSE3" + BaseApiUrl;
    }else if (window.location.href.indexOf("/dnse3/") !==-1){
        BaseApiUrl  = "/dnse3" + BaseApiUrl;
    }
    return BaseApiUrl ;
}

function getBaseUrl(){
    //set the base path
    var base_url = "/";
    if (window.location.href.indexOf("/DNSE3/") !==-1){
        base_url  = "/DNSE3" + base_url;
    }else if (window.location.href.indexOf("/dnse3/") !==-1){
        base_url  = "/dnse3" + base_url;
    }
    return base_url;
}

/**
 * Given the name of a parameter of the current URL location of the browser, the function returns its value
 * @param name The name of the parameter of the current URL location of the browser whose value we want to get
 * @returns {String} The value of the URL parameter
 */
function getLocationParameter(parameter_name) {
    parameter_name = parameter_name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + parameter_name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(window.location.href);
    if (results == null)
        return "";
    else
        return results[1];
}

/**
 * Given the name of a parameter and a URL location, the function returns its value
 * @param name The name of the parameter of the current URL location of the browser whose value we want to get
 * @returns {String} The value of the URL parameter
 */
function getUrlParameter(href, parameter_name) {
    parameter_name = parameter_name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
    var regexS = "[\\?&]" + parameter_name + "=([^&#]*)";
    var regex = new RegExp(regexS);
    var results = regex.exec(href);
    if (results == null)
        return "";
    else
        return results[1];
}


