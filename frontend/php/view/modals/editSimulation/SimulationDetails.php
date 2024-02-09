<div class="panel panel-default">
    <div class="panel-heading">
        <h3 class="panel-title">Detalles de la simulación</h3>
    </div>
    <div class="panel-body">
        <div id="alert-danger-edit-simulation-details" class="alert alert-danger hidden" role="alert"></div>
        <form id="form-edit-simulation-details">
            <div id="form-group-esd-name" class="form-group">
                <label class="control-label" for="edit-simulation-name">Nombre</label>
                <input type="text" class="form-control" id="edit-simulation-name" placeholder="Nombre de la simulación" required autofocus>
            </div>
            <div id="form-group-esd-repetitions" class="form-group">
                <label class="control-label" for="edit-simulation-repetitions">Número de repeticiones</label>
                <input type="number" class="form-control" id="edit-simulation-repetitions" placeholder="Número de repeticiones de la simulación" min="1" required>
            </div>
            <div id="form-group-esd-priority" class="form-group">
                <label class="control-label" for="edit-simulation-priority">Prioridad de la simulación</label>
                <!--<span id="valueOfRangeEdit" style="padding-left: 1%;"></span>
                <input type="range" class="form-control" id="edit-simulation-priority" value="50" min="1" max="99" required onchange="document.getElementById('valueOfRangeEdit').innerHTML = this.value">-->
                <select name="edit-simulation-priority" id="edit-simulation-priority" style="margin-left: 2%;">
                    <option value="1">1 - Prioridad mínima</option>
                    <option value="10">10 - Prioridad muy baja</option>
                    <option value="30">30 - Prioridad baja</option>
                    <option value="50">50 - Prioridad media</option>
                    <option value="70">70 - Prioridad alta</option>
                    <option value="90">90 - Prioridad muy alta</option>
                    <option value="100">100 - Prioridad máxima</option>
                </select>
            </div>
            <div id="form-group-esd-simulation-type" class="form-group">
                <label class="control-label">Tipo de simulación</label>
                <div>
                    <label class="radio-inline disabled">
                        <input type="radio" name="edit-simulation-type" id="edit-simulation-type-single" value="single"> Individual
                    </label>
                    <label class="radio-inline disabled">
                        <input type="radio" name="edit-simulation-type" id="edit-simulation-type-parameter-sweep" value="parameter-sweep"> Barrido de parámetros
                    </label>
                </div>
            </div>
        </form>
    </div>
</div>

