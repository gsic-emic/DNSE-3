<div id="div-form-new-simulation-file-gathering">
    
    <div class="panel panel-default">
        <div class="panel-heading">
            <h3 class="panel-title">Ficheros de salida de la simulación</h3>
        </div>
        <div class="panel-body">
            <div id="alert-danger-panel-output-files" class="alert alert-danger hidden" role="alert"></div>
            <!--
            <table class="table table-striped table-bordered table-hover" id="new-simulation-file-gathering-table">
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
            <table class="table table-striped table-bordered table-hover" id="new-simulation-file-gathering-table">
                <thead>
                    <tr>
                        <th>
                            <input type="checkbox" id="new-simulation-file-gathering-ofs-all" value="yes">
                        </th>
                        <th>
                            #
                        </th>
                        <th>Nombre</th>
                        <th>Tipo</th>
                        <th>Variables</th>
                    </tr>
                </thead>
                <tbody>
                    <!--
                    <tr>
                        <td><input type="checkbox" name="new-simulation-file-gathering-ofs" id="new-simulation-file-gathering-ofs-0" value="yes"></td>
                        <td>1</td>
                        <td>nombre</td>
                        <td>tipo</td>
                        <td>T, Q, Z</td>
                    </tr>
                    -->
                </tbody>
            </table>
            <!--
            <div class="text-right">
                <button id="file-gathering-add-file" type="button" class="btn btn-primary"><span class="glyphicon glyphicon-plus"></span> Nuevo fichero</button>
            </div>
            -->
        </div>
    </div>
    
    <div id="panel-file-gathering-add-file" class="panel panel-default">
        <div class="panel-heading">
            <h3 id="panel-file-gathering-add-file-title" class="panel-title">Nuevo fichero</h3>
        </div>
        <div class="panel-body">
            <div id="alert-danger-panel-file-gathering-add-file" class="alert alert-danger hidden" role="alert"></div>
            <form id="form-file-gathering-add-file">
                <fieldset>
                    <div id="form-group-fgaf-structure" class="form-group">
                        <label class="control-label" for="file-gathering-af-structure">Estructura del fichero</label>
                        <select id="file-gathering-af-structure" class="form-control">
                            <option value="">-- Selecciona la estructura del fichero --</option>                                                      
                        </select>
                    </div>
                    
                    <div id="form-group-fgaf-multiline" class="form-group">
                        <label class="control-label">Varias lineas</label>
                        <div>
                            <label class="radio-inline disabled">
                                <input type="radio" name="file-gathering-af-multiline" id="file-gathering-af-multiline-multiline" checked disabled="disabled" value="multiline"> Sí
                            </label>
                            <label class="radio-inline disabled">
                                <input type="radio" name="file-gathering-af-multiline" id="file-gathering-af-multiline-singleline" disabled="disabled" value="singleline"> No
                            </label>
                        </div>
                    </div>
                    
                    <div id="form-group-fgaf-output-variables" class="form-group">
                        <label class="control-label">Variables</label>
                        <div class="row">
                            <div id="form-group-fgaf-output-variables-names">
                                <div id="form-group-fgaf-output-variables-name-0"  class="form-group col-md-6">
                                    <input type="text" class="form-control" id="new-simulation-output-variables-name-0" name="new-simulation-output-variables-name[]" disabled="disabled" placeholder="Nombre">
                                </div>
                                <div id="form-group-fgaf-output-variables-name-1" class="form-group col-md-6">
                                    <input type="text" class="form-control" id="new-simulation-output-variables-name-1" name="new-simulation-output-variables-name[]" disabled="disabled" placeholder="Nombre">
                                </div>
                            </div>
                            <!--
                            <div id="form-group-fgaf-output-variables-button" class="col-md-6">                                 
                                <button id="fgaf-output-variables-names-add" class="btn btn-default" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Nuevo nombre</button>
                            </div>
                            -->
                        </div>
                    </div>
                                                          
                    <div id="form-group-fgaf-output-files" class="form-group">
                        <label class="control-label">Nombres de los ficheros</label>
                        <div class="row">
                            <div id="form-group-fgaf-output-files-names">
                                <div id="form-group-fgaf-output-files-name-0"  class="form-group col-md-6">
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="new-simulation-output-files-name-0" name="new-simulation-output-files-name[]" placeholder="Nombre">
                                        <span class="input-group-btn">                                   
                                            <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                                <div id="form-group-fgaf-output-files-name-0" class="form-group col-md-6">
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="new-simulation-output-files-name-0" name="new-simulation-output-files-name[]" placeholder="Nombre">
                                        <span class="input-group-btn">                                   
                                            <button class="btn btn-default" type="button"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></button>
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div id="form-group-fgaf-output-files-button" class="col-md-6">                                 
                                <button id="fgaf-output-files-names-add" class="btn btn-default" type="button"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Nuevo nombre</button>
                            </div>
                        </div>
                    </div>
                    
                </fieldset>
                <div class="text-right">
                    <button id="close-add-file" type="button" class="btn btn-default">Cerrar</button>
                    <button id="update-add-file" type="button" class="btn btn-primary">Guardar cambios</button>
                    <button id="save-add-file" type="button" class="btn btn-primary">Guardar</button>
                </div>
            </form>
        </div>
    </div>
</div>