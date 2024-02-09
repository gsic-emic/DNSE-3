
function manageRequestError(xhr, status){
    switch(xhr.status){
        case 401: //the session has expired or the user is not logged in
            SessionExpiredModal.showModal();
            break;
        case 404: //the resource doesn't exist
                  ErrorModal.errorMessage(" Error", "El recurso no existe");
                  G_VARS["refresh_painters"] = false;//Important!! Since the resource doesn't exist we can not update its info
                  break;
        default:
            ErrorModal.errorDefault();
    }
}


