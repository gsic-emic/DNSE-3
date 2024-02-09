<div class="modal fade" id="view-project-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Detalles del proyecto</h4>
            </div>
            <div class="modal-body">

                <!-- Nav tabs -->
                <ul id="view-project-details-tabs" class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="active" id="view-project-details-tabs-t1"><a href="#view-project-details-tp-desc" aria-controls="sim-project-details-tp-desc" role="tab" data-toggle="tab">Descripción</a></li>
                    <li role="presentation" id=view-project-details-tabs-t2"><a href="#view-project-details-tp-params" aria-controls="view-project-details-tp-params" role="tab" data-toggle="tab">Parámetros</a></li>
                    <li role="presentation" id=view-project-details-tabs-t3"><a href="#view-project-details-tp-files" aria-controls="view-project-details-tp-files" role="tab" data-toggle="tab">Ficheros de resultados</a></li>
                </ul>
                <!-- Tab panes -->
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="view-project-details-tp-desc">
                    </div>
                    <div role="tabpanel" class="tab-pane" id="view-project-details-tp-params">               
                    </div>
                    <div role="tabpanel" class="tab-pane" id="view-project-details-tp-files">
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <button type="button" id="view-project-modal-close" class="btn btn-default" data-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>
