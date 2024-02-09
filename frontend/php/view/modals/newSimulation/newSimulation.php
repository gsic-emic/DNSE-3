<div class="modal fade" id="new_simulation_modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">Nueva simulación</h4>
            </div>
            <div class="modal-body">
                <div class="wizard">
                    <!-- Nav tabs -->
                    <ul id="new-simulation-modal-tabs" class="nav nav-tabs nav-wizard">
                        <li role="presentation" class="active" id="new-simulation-modal-tabs-t1"><a href="#step1-details" aria-controls="step1_details" role="tab" data-toggle="tab">Detalles</a></li>
                        <li role="presentation" class="disabled" id="new-simulation-modal-tabs-t2"><a href="#step2-parameters" aria-controls="step2_parameters" role="tab" data-toggle="tab">Parámetros</a></li>
                        <li role="presentation" class="disabled" id="new-simulation-modal-tabs-t3"><a href="#step3-files" aria-controls="step3_files" role="tab" data-toggle="tab">Ficheros de salida</a></li>
                    </ul>
                    <!-- Tab panes -->
                    <div class="tab-content">
                        <div role="tabpanel" class="tab-pane active" id="step1-details">
                            <?php
                                require_once(dirname(__FILE__) . "/SimulationDetails.php");
                            ?>
                        </div>
                        <div role="tabpanel" class="tab-pane" id="step2-parameters">
                            <?php
                                require_once(dirname(__FILE__) . "/ParametersSingle.php");
                                require_once(dirname(__FILE__) . "/ParametersSweep.php");
                            ?>
                            <!--
                            <div class="container-fluid">
                                <div class="row clearfix">
                            <!--<div class="col-md-12 column">-->
                            <!--
                            <table class="table table-bordered table-hover" id="tab_logic">
                                <thead>
                                    <tr >
                                        <th class="text-center">
                                            #
                                        </th>
                                        <th class="text-center">
                                            Nombre
                                        </th>
                                        <th class="text-center">
                                            Mail
                                        </th>
                                        <th class="text-center">
                                            Mobile
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr id='addr0'>
                                        <td>
                                            1
                                        </td>
                                        <td>
                                            <input type="text" name='name0'  placeholder='Name' class="form-control"/>
                                        </td>
                                        <td>
                                            <input type="text" name='mail0' placeholder='Mail' class="form-control"/>
                                        </td>
                                        <td>
                                            <input type="text" name='mobile0' placeholder='Mobile' class="form-control"/>
                                        </td>
                                    </tr>
                                    <tr id='addr1'></tr>
                                </tbody>
                            </table>
                        </div>
                            <!--</div>-->
                            <!--<a id="add_row" class="btn btn-default pull-left">Add Row</a><a id='delete_row' class="pull-right btn btn-default">Delete Row</a>
                        </div>
                            -->
                            <!--
                            <div id="div-form-new-simulation-parameters-sweep">
                                <form id="form-new-simulation-parameters-sweep">
                                    <fieldset>
                                        <div id="form-group-nsp-sweep-name" class="form-group">
                                            <label class="control-label" for="form-group-nsp-sweep-name">Nombre</label>
                                            <select id="form-group-nsp-sweep-name" class="form-control">
                                                <option>-- Selecciona el nombre del parámetro --</option>
                                            </select>
                                        </div>
                                        <div id="form-group-nsp-sweep-value-type" class="form-group">
                                            <label class="control-label" for="form-group-nsp-sweep-value-type">Tipo de valores</label>
                                            <select id="form-group-nsp-sweep-value-type" class="form-control">
                                                <option>-- Selecciona el tipo de tipo de valores --</option>
                                                <option>Rango de valores</option>
                                                <option>Valores aleatorios</option>
                                                <option>Indicar los valores</option>
                                            </select>
                                        </div>

                                        <div class="container-fluid">
                                            <div class="row clearfix">
                                                <!--<div class="col-md-12 column">-->
                                                <!--
                                                <table class="table table-bordered table-hover" id="tab_logic">
                                                    <thead>
                                                        <tr >
                                                            <th class="text-center">
                                                                Mínimo
                                                            </th>
                                                            <th class="text-center">
                                                                Máximo
                                                            </th>
                                                            <th class="text-center">
                                                                Incremento
                                                            </th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <tr id='addr0'>
                                                            <td>
                                                                <input type="text" name='name0'  placeholder='Name' class="form-control"/>
                                                            </td>
                                                            <td>
                                                                <input type="text" name='mail0' placeholder='Mail' class="form-control"/>
                                                            </td>
                                                            <td>
                                                                <input type="text" name='mobile0' placeholder='Mobile' class="form-control"/>
                                                            </td>
                                                        </tr>
                                                        <tr id='addr1'></tr>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </div>
                                    </fieldset>
                                </form>
                            </div>
                            -->
                        </div>
                        <div role="tabpanel" class="tab-pane" id="step3-files">
                            <?php
                                require_once(dirname(__FILE__) . "/FileGathering.php");
                            ?>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default pull-left" id="new_simulation_modal_cancel" >Cancelar</button>
                    <button type="button" class="btn btn-warning prev-step" style="display:none;">Anterior</button>
                    <button type="button" class="btn btn-success next-step">Siguiente</button></li>
                    <button type="button" class="btn btn-primary final-step" id="new_simulation_modal_accept"  style="display:none;">Guardar</button>
                </div>
            </div>
        </div>
    </div>
</div>