<div id="div-form-new-simulation-parameters-sweep">
    
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Parámetros de la simulación de barrido de parámetros</h3>
        </div>
        <div class="panel-body">
            <div id="alert-danger-panel-parameters-sweep" class="alert alert-danger hidden" role="alert"></div>
            <!--
            <table class="table table-striped table-bordered table-hover" id="new-simulation-parameters-sweep-table">
                <thead>
                    <tr>
                        <th>
                            #
                        </th>
                        <th>Nombre</th>
                        <th>Acciones</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            -->
            <table class="table table-striped table-bordered table-hover" id="new-simulation-parameters-sweep-table">
                <thead>
                    <tr>
                        <th>
                            #
                        </th>
                        <th>Nombre</th>
                        <th>Tipo</th>
                        <th>Valores</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <!--
            <div class="text-right">
                <button id="add-parameter-sweep" type="button" class="btn btn-primary"><span class="glyphicon glyphicon-plus"></span> Nuevo parámetro</button>
            </div>
            -->
        </div>
    </div>
    
    <div id="panel-add-parameter-sweep" class="panel panel-default">
        <div class="panel-heading">
            <h3 id="panel-add-parameter-sweep-title" class="panel-title">Nuevo parámetro</h3>
        </div>
        <div class="panel-body">
            <div id="alert-danger-panel-add-parameter-sweep" class="alert alert-danger hidden" role="alert"></div>
            <form id="form-new-simulation-parameters-sweep">
                <fieldset>
                    <div id="form-group-nsp-sweep-name" class="form-group">
                        <label class="control-label" for="new-simulation-nsp-sweep-name">Nombre</label>
                        <select id="new-simulation-sweep-name" class="form-control">
                            <option>Selecciona el nombre del parámetro</option>
                        </select>
                    </div>
                    <div id="form-group-nsp-sweep-value-type" class="form-group">
                        <label class="control-label" for="new-simulation-nsp-sweep-value-type">Tipo del valor</label>
                        <select id="new-simulation-nsp-sweep-value-type" class="form-control">
                            <option value="">-- Selecciona el tipo de los valores --</option>
                            <option value="range">Rango de valores</option>
                            <option value="fixed">Valores fijos</option>
                            <option value="random">Valores aleatorios</option>                                                    
                        </select>
                    </div>
                    <div id="form-group-nsp-sweep-range" class="row">
                        <div id="form-group-nsp-sweep-range-min" class="form-group col-sm-6 col-md-3">
                            <label class="control-label" for="new-simulation-nsp-sweep-range-min">Mínimo</label>
                            <input type="text" class="form-control" id="new-simulation-nsp-sweep-range-min" placeholder="Mínimo">
                        </div>
                        <div id="form-group-nsp-sweep-range-max" class="form-group col-sm-6 col-md-3">
                            <label class="control-label" for="new-simulation-nsp-sweep-range-max">Máximo</label>
                            <input type="text" class="form-control" id="new-simulation-nsp-sweep-range-max" placeholder="Máximo">
                        </div>
                        <div id="form-group-nsp-sweep-range-step" class="form-group col-sm-6 col-md-3">
                            <label class="control-label" for="new-simulation-nsp-sweep-range-max">Incremento</label>
                            <input type="text" class="form-control" id="new-simulation-nsp-sweep-range-step" placeholder="Incremento">
                        </div>
                        <div id="form-group-nsp-sweep-range-units" class="form-group col-sm-6 col-md-3">
                            <label class="control-label" for="new-simulation-nsp-sweep-range-units">Unidades</label>
                            <input type="text" class="form-control" id="new-simulation-nsp-sweep-range-units" placeholder="Unidades">
                        </div>
                    </div>
                    <!--
                    <div id="form-group-nsp-sweep-value" class="form-group">
                        <label class="control-label" for="new-simulation-nsp-sweep-value">Valor</label>
                        <input type="text" class="form-control" id="new-simulation-nsp-sweep-value" placeholder="Valor">
                    </div>-->
                    
                    <div id="form-group-nsp-sweep-fixed" class="form-group">
                        <label class="control-label">Valores</label>
                        <div class="row">
                            <div id="form-group-nsp-sweep-fixed-values">
                                <!--
                                <div id="form-group-nsp-sweep-value-0"  class="form-group col-md-6">
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="new-simulation-nsp-sweep-value-0" name="new-simulation-nsp-sweep-value[]" placeholder="Valor">
                                        <span class="input-group-btn">                                   
                                            <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                <div id="form-group-nsp-sweep-value-1" class="form-group col-md-6">
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="new-simulation-nsp-sweep-value-1" name="new-simulation-nsp-sweep-value[]" placeholder="Valor">
                                        <span class="input-group-btn">                                   
                                            <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                <div id="form-group-nsp-sweep-value-2" class="form-group col-md-6">
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="new-simulation-nsp-sweep-value-2" name="new-simulation-nsp-sweep-value[]" placeholder="Valor">
                                        <span class="input-group-btn">                                   
                                            <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></button>
                                            <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                -->
                            </div>
                            <div id="form-group-nsp-sweep-fixed-button" class="col-md-6">                                 
                                <button id="nsp-sweep-fixed-values-add" class="btn btn-default" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Nuevo valor</button>
                            </div>
                        </div>
                    </div>
                </fieldset>
                <div class="text-right">
                    <button id="close-parameter-sweep" type="button" class="btn btn-default">Cerrar</button>
                    <button id="update-parameter-sweep" type="button" class="btn btn-primary">Guardar cambios</button>
                    <button id="save-parameter-sweep" type="button" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>
    
</div>
