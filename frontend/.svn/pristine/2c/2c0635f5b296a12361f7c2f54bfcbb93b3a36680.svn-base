/**
 * Manager for the modal dialog that deletes a simulation
 */
var InfoModal = {
    
    modalId: "info-modal",
    closeButtonId: "info-modal-close",
    
    init: function(){
        
    },
    
    showModal: function(){
        $('#' + this.modalId).modal('show');
    },
    
    hideModal: function(){
        $('#' + this.modalId).modal('hide');
    },
    
    resetInfoModal: function(){
        $('#' + this.modalId + " span.modal-title-text").html(" Info");
        $('#' + this.modalId + " div.modal-body").html("La acción se ha ejecutado correctamente.");
        $('#' + this.closeButtonId).unbind('click');
        $('#' + this.closeButtonId).click(function(){
            InfoModal.hide();
        });
    },
    
    info: function(title, message, callback_function){
        $('#' + this.modalId + " span.modal-title-text").html(title);
        $('#' + this.modalId + " div.modal-body").html(message);
        $('#' + this.closeButtonId).unbind('click');
        $('#' + this.closeButtonId).click(function(){
            InfoModal.hideModal();
            callback_function();
        });
        InfoModal.showModal();
    },
    
    infoMessage: function(title, message){
        $('#' + this.modalId + " span.modal-title-text").html(title);
        $('#' + this.modalId + " div.modal-body").html(message);
        $('#' + this.closeButtonId).unbind('click');
        $('#' + this.closeButtonId).click(function(){
            InfoModal.hideModal();
        });
        InfoModal.showModal();
    },
    
    infoDefault: function(){
        InfoModal.resetInfoModal();
        InfoModal.showModal();
    }
    
};

