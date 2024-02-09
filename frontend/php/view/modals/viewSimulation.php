<div class="modal fade" id="view-simulation-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Detalles de la simulación</h4>
            </div>
            <div class="modal-body">

                <!-- Nav tabs -->
                <ul id="view-simulation-details-tabs" class="nav nav-tabs" role="tablist">
                    <li role="presentation" class="active" id="view-simulation-details-tabs-t1"><a href="#view-simulation-details-tp-desc" aria-controls="sim-simulation-details-tp-desc" role="tab" data-toggle="tab">Descripción</a></li>
                    <li role="presentation" id=view-simulation-details-tabs-t2"><a href="#view-simulation-details-tp-params" aria-controls="view-simulation-details-tp-params" role="tab" data-toggle="tab">Parámetros</a></li>
                    <li role="presentation" id=view-simulation-details-tabs-t3"><a href="#view-simulation-details-tp-files" aria-controls="view-simulation-details-tp-files" role="tab" data-toggle="tab">Ficheros a recoger</a></li>
                </ul>
                <!-- Tab panes -->
                <div class="tab-content">
                    <div role="tabpanel" class="tab-pane active" id="view-simulation-details-tp-desc">
                    </div>
                    <div role="tabpanel" class="tab-pane" id="view-simulation-details-tp-params">               
                    </div>
                    <div role="tabpanel" class="tab-pane" id="view-simulation-details-tp-files">
                    </div>
                </div>

            </div>
            <div class="modal-footer">
                <button type="button" id="view-simulation-modal-close" class="btn btn-default" data-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>
