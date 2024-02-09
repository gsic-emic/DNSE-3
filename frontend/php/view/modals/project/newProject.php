<div class="modal fade" id="new_project_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Nuevo proyecto</h4>
            </div>
            <div class="modal-body">
                <div id="alert-danger-new_project_modal" class="alert alert-danger hidden" role="alert"></div>
                <form id="form-new_project_modal" method="post" enctype="multipart/form-data">
                    <div id="form-group-projectFile" class="form-group" >
                        <label class="control-label" for="projectFile">Fichero</label>
                        <input type="file" id="projectFile">
                        <p class="help-block">Selecciona un fichero en formato ZIP que contenga el modelo de la simulación.</p>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" id="new_project_modal_cancel" class="btn btn-default">Cancelar</button>
                <button type="button" id="new_project_modal_accept" class="btn btn-primary">Guardar cambios</button>
            </div>
        </div>
    </div>
</div>
