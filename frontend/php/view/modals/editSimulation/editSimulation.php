<div class="modal fade" id="edit_simulation_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Editar simulación</h4>
            </div>
            <div class="modal-body">
                <div id="alert-danger-edit-simulation-details" class="alert alert-warning hidden" role="alert"></div>
                <div class="wizard">
                    <!-- Nav tabs -->
                    <ul id="edit-simulation-modal-tabs" class="nav nav-tabs nav-wizard">
                        <li role="presentation" class="active" id="edit-simulation-modal-tabs-t1"><a href="#edit-simulation-step1-details" aria-controls="edit-simulation-step1-details" role="tab" data-toggle="tab">Detalles</a></li>
                        <li role="presentation" class="disabled" id="edit-simulation-modal-tabs-t2"><a href="#edit-simulation-step2-parameters" aria-controls="edit-simulation-step2_parameters" role="tab" data-toggle="tab">Parámetros</a></li>
                        <li role="presentation" class="disabled" id="edit-simulation-modal-tabs-t3"><a href="#edit-simulation-step3-files" aria-controls="edit-simulation-step3_files" role="tab" data-toggle="tab">Ficheros de salida</a></li>
                    </ul>
                    <!-- Tab panes -->
                    <div class="tab-content">
                        <div role="tabpanel" class="tab-pane active" id="edit-simulation-step1-details">
                            <?php
                            require_once(dirname(__FILE__) . "/SimulationDetails.php");
                            ?>
                        </div>
                        <div role="tabpanel" class="tab-pane" id="edit-simulation-step2-parameters">
                            <?php
                            require_once(dirname(__FILE__) . "/ParametersSingle.php");
                            require_once(dirname(__FILE__) . "/ParametersSweep.php");
                            ?>

                        </div>
                        <div role="tabpanel" class="tab-pane" id="edit-simulation-step3-files">
                            <?php
                            require_once(dirname(__FILE__) . "/FileGathering.php");
                            ?>
                        </div>
                    </div>
                </div>

                <?php
                require_once(dirname(__FILE__) . "/SimulationDetails.php");
                ?>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default pull-left" id="edit_simulation_modal_cancel" >Cancelar</button>
                <button type="button" class="btn btn-warning prev-step" style="display:none;">Anterior</button>
                <button type="button" class="btn btn-success next-step">Siguiente</button></li>
                <button type="button" class="btn btn-primary final-step" id="edit_simulation_modal_accept"  style="display:none;">Guardar</button>
            </div>
        </div>
    </div>
</div>