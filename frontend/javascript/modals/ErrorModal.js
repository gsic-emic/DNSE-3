/**
 * Manager for the modal dialog that shows an error
 */
var ErrorModal = {
    
    modalId: "error-modal",
    closeButtonId: "error-modal-close",
    
    init: function(){
        
    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    },
    
    resetErrorModal: function(){
        $('#' + this.modalId + " span.modal-title-text").html(" Error");
        $('#' + this.modalId + " div.modal-body").html("Ha ocurrido un error desconocido.");
        $('#' + this.closeButtonId).unbind('click');
        $('#' + this.closeButtonId).click(function(){
            ErrorModal.hideModal();
        });
    },
    
    error: function(title, message, callback_function){
        $('#' + this.modalId + " span.modal-title-text").html(title);
        $('#' + this.modalId + " div.modal-body").html(message);
        $('#' + this.closeButtonId).unbind('click');
        $('#' + this.closeButtonId).click(function(){
            ErrorModal.hideModal();
            callback_function();
        });
        ErrorModal.showModal();
    },
    
    errorMessage: function(title, message){
        $('#' + this.modalId + " span.modal-title-text").html(title);
        $('#' + this.modalId + " div.modal-body").html(message);
        $('#' + this.closeButtonId).unbind('click');
        $('#' + this.closeButtonId).click(function(){
            ErrorModal.hideModal();
        });
        ErrorModal.showModal();
    },
    
    errorDefault: function(){
        ErrorModal.resetErrorModal();
        ErrorModal.showModal();
    }
    
};

