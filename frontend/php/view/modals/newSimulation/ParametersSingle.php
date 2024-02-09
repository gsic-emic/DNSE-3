<div id="div-form-new-simulation-parameters-single">
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Parámetros de la simulación individual</h3>
        </div>
        <div class="panel-body">
            <div id="alert-danger-panel-parameters-single" class="alert alert-danger hidden" role="alert"></div>
            <!--
            <table class="table table-striped table-bordered table-hover" id="new-simulation-parameters-single-table">
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
            <table class="table table-striped table-bordered table-hover" id="new-simulation-parameters-single-table">
                <thead>
                    <tr>
                        <th>
                            #
                        </th>
                        <th>Nombre</th>
                        <th>Tipo</th>
                        <th>Valor</th>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
            <!--
            <div class="text-right">
                <button id="add-parameter-single" type="button" class="btn btn-primary"><span class="glyphicon glyphicon-plus"></span> Nuevo parámetro</button>
            </div>
            -->
        </div>

    </div>
    <div id="panel-add-parameter-single" class="panel panel-default">
        <div class="panel-heading">
            <h3 id="panel-add-parameter-single-title" class="panel-title">Nuevo parámetro</h3>
        </div>
        <div class="panel-body">
            <div id="alert-danger-panel-add-parameter-single" class="alert alert-danger hidden" role="alert"></div>
            <form id="form-new-simulation-parameters-single">
                <fieldset>
                    <div id="form-group-nsp-name" class="form-group">
                        <label class="control-label" for="new-simulation-ps-name">Nombre</label>
                        <select id="new-simulation-ps-name" class="form-control">
                            <option>Selecciona el nombre del parámetro</option>
                        </select>
                    </div>
                    <!--
                    <div class="form-group">
                        <label>Aleatoriedad</label>
                        <div class="checkbox">
                            <label>
                                <input type="checkbox" id="new-simulation-nsp-random"> Aleatorio
                            </label>
                        </div>
                    </div>
                    -->
                    <div id="form-group-nsp-value-type" class="form-group">
                        <label class="control-label" for="new-simulation-nsp-value-type">Tipo del valor</label>
                        <select id="new-simulation-nsp-value-type" class="form-control">
                            <option value="">-- Selecciona el tipo del valor --</option>
                            <option value="fixed">Valor fijo</option>
                            <option value="random">Valor aleatorio</option>                                                       
                        </select>
                    </div>
                    <div id="form-group-nsp-value" class="form-group">
                        <label class="control-label" for="new-simulation-nsp-value">Valor</label>
                        <input type="text" class="form-control" id="new-simulation-nsp-value" placeholder="Valor">
                    </div>
                </fieldset>
                <div class="text-right">
                    <button id="close-parameter-single" type="button" class="btn btn-default">Cerrar</button>
                    <button id="update-parameter-single" type="button" class="btn btn-primary">Guardar cambios</button>
                    <button id="save-parameter-single" type="button" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>
</div>