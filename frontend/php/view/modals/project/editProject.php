<div class="modal fade" id="edit_project_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Editar proyecto</h4>
            </div>
            <div class="modal-body">
                <div id="alert-danger-projectName" class="alert alert-danger hidden" role="alert"></div>
                <form>
                    <input type="hidden" id="projectId">
                    <div id="form-group-projectName" class="form-group">
                        <label class="control-label" for="projectName">Nombre</label>
                        <input type="text" class="form-control" id="projectName" placeholder="Nombre del proyecto" required autofocus>
                    </div>
                    <div id="form-group-projectDescription" class="form-group">
                        <label for="projectDescription">Descripción</label>
                        <textarea class="form-control" id="projectDescription" rows="4" maxlength="1023" placeholder="Descripción del proyecto"></textarea>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" id="edit_project_modal_cancel" class="btn btn-default">Cancelar</button>
                <button type="button" id="edit_project_modal_accept" class="btn btn-primary">Guardar cambios</button>
            </div>
        </div>
    </div>
</div>