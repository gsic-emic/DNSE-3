/**
 * Manager for the modal dialog that indicates that the session has expired
 */
var SessionExpiredModal = {
    
    modalId: "session-expired-error-modal",
    closeButtonId: "session-expired-error-modal-close",
    
    init: function(){
        $("#" + this.closeButtonId).click(function(){
            //move to the login page
            window.location.href = getBaseUrl() + "php/controller/login.php";
        });
    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    }
};

