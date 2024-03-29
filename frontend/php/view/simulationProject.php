<?php
require_once(dirname(__FILE__)."/../controller/checkLoggedIn.php");
?>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>DNSE3</title>

        <!-- Bootstrap -->
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/lib/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet">

        <!-- Custom styles -->
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/css/common.css" rel="stylesheet">
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/css/main.css" rel="stylesheet">
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/css/simulationProject.css" rel="stylesheet">
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/utils/wizard.css" rel="stylesheet">

        <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
        <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
        <!--[if lt IE 9]>
          <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
          <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
        <![endif]-->

        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <!-- Include all compiled plugins (below), or include individual files as needed -->
        <script src="<?php echo $CONF_PROP["dnse3_root"];?>/lib/bootstrap-3.3.7/js/bootstrap.min.js"></script>   

        <!-- application's scripts -->
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/utils/requests_manager.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/utils/url.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/utils/dates.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/utils/sections.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/simulationProjectInit.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/utils/wizard.js"></script>
        
        <!-- controller scripts -->
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/controller/SingleSimulationController.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/controller/ParameterSweepSimulationController.js"></script>
        
        <!-- modals scripts -->
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/project/editProjectModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/project/DeleteProjectModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/newSimulation/NewSimulationModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/newSimulation/NewSingleSimulation.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/newSimulation/NewParameterSweepSimulation.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/newSimulation/NewSimulationFileGathering.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/editSimulation/EditSimulationModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/editSimulation/EditSingleSimulation.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/editSimulation/EditParameterSweepSimulation.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/editSimulation/EditSimulationFileGathering.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/ViewSingleSimulationModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/ViewParameterSweepSimulationModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/DeleteSimulationModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/InfoModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/ErrorModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/SessionExpiredModal.js"></script>
        
        <!-- painters -->
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/painters/ProjectsPainter.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/painters/SimulationPainter.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/painters/SingleSimulationsPainter.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/painters/ParameterSweepSimulationsPainter.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/painters/CurrentSimulationsPainter.js"></script>
        
        <!-- data model -->
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/User.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/Parameter.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/ParameterResource.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/SingleSimulation.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/ParameterSweepSimulation.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/OutputFileResource.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/OutputFileStructure.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/SimulationStatus.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/OutputFileType.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/ParameterType.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/SimulationOperation.js"></script>

    </head>
    <body>
        <div id="sticky-wrapper">
        <?php
        /**
         * add the modals here
         */
        require_once(dirname(__FILE__) . "/modals/project/newProject.php");
        require_once(dirname(__FILE__) . "/modals/project/editProject.php");
        require_once(dirname(__FILE__) . "/modals/project/deleteProject.php");
        require_once(dirname(__FILE__) . "/modals/newSimulation/newSimulation.php");
        require_once(dirname(__FILE__) . "/modals/editSimulation/editSimulation.php");
        require_once(dirname(__FILE__) . "/modals/viewSimulation.php");
        require_once(dirname(__FILE__) . "/modals/deleteSimulation.php");
        require_once(dirname(__FILE__) . "/modals/infoModal.php");
        require_once(dirname(__FILE__) . "/modals/errorModal.php");
        require_once(dirname(__FILE__) . "/modals/sessionExpired.php");
        ?>
        <nav class="navbar navbar-default">
            <div class="container-fluid">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar-collapse-items" aria-expanded="false">
                        <span class="sr-only">Toggle navigation</span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <!-- the brand logo -->
                    <a class="navbar-brand dnse3-logo" href="#">
                        <!--DNSE3-->
                        <img alt="DNSE3" src="<?php echo $CONF_PROP["dnse3_root"];?>/images/dnse3.svg">
                    </a>
                </div>
                <!-- Collect the nav items for toggling -->
                <div class="collapse navbar-collapse" id="navbar-collapse-items">
                    <ul class="nav navbar-nav navbar-right">
                        <p class="navbar-text sim-ejec">Simulaciones en ejecución</p>
                        <div class="nav navbar-text progress" id="navbar-progress-bar">
                            <?php
                            if ($user->getPercentage() <= 50) {
                                $progress_bar_type = "progress-bar-success";
                            } else if ($user->getPercentage() > 50 && $user->getPercentage() <= 75) {
                                $progress_bar_type = "progress-bar-warning";
                            } else {
                                $progress_bar_type = "progress-bar-danger";
                            }
                            ?>
                            <?php
                            $active_css = "";
                            if ($user->getCurrentSimulations() > 0){
                                $active_css = "active";
                            }
                            ?>
                            <div class="progress-bar <?php echo $progress_bar_type ?> progress-bar-striped <?php echo $active_css ?>" role="progressbar" aria-valuenow="<?php echo $user->getPercentage(2); ?>" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: <?php echo $user->getPercentage(2); ?>%;">
                              <!--<span class="sr-only">45% Complete</span>-->
                                <?php echo $user->getPercentage(2); ?>%
                            </div>
                        </div>
                        <p id="navbar-progress-bar-info" class="navbar-text sim-current-max"><?php echo "(" . $user->getCurrentSimulations() . "/" . $user->getMaxSimulations() . ")"; ?></p>
                        <li class="dropdown">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
                                <span class="glyphicon glyphicon-user" aria-hidden="true"></span>
                                <!--<i class="fa fa-user fa-fw"></i> <i class="fa fa-caret-down"></i>-->
                                <?php
                                //echo $_SESSION["username"];
                                echo $user->getUserName();
                                ?>
                                <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu dropdown-user">
                                <li><a href="<?php echo $CONF_PROP["dnse3_root"];?>/php/controller/logout.php"><!--<i class="fa fa-sign-out fa-fw"></i>--> 
                                        <span class="glyphicon glyphicon-log-out" aria-hidden="true"></span> Cerrar sesión</a>
                                </li>
                            </ul>
                            <!-- /.dropdown-user -->
                        </li>
                    </ul>
                </div><!-- /.navbar-collapse -->
            </div><!-- /.container-fluid -->
        </nav>

        <div class="container-fluid">
            <div class="row">
                <div class="col-md-12">
                    <!--<div class="page-header">-->
                    <h1 id="simulation-project-name">Proyecto: <?php echo $sim_project->getName();?></h1>
                </div>
            </div>
        </div>

        <nav class="navbar navbar-default navbar-static-top" role="navigation">
            <div class="container-fluid">
                <ul class="nav navbar-nav navbar-left">
                    <ol class="breadcrumb list-inline bigger-size">
                        <li><a href="<?php echo $CONF_PROP["dnse3_root"];?>/php/controller/login.php"><span class="glyphicon glyphicon-home" aria-hidden="true"></span></a></li>
                        <li class="active"><?php echo $sim_project->getName();?></li>
                    </ol>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <div class="btn-group">
                        <button type="button" class="btn btn-primary navbar-btn" onclick="NewSimulationModal.newSimulation('<?php echo $sim_project->getProjectId(); ?>');">
                            <span class="glyphicon glyphicon-plus"></span> Nueva simulación
                        </button>
                    </div>
                    <div class="btn-group">
                        <!--<button class="btn btn-default btn-sm" ><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver detalles</button>-->
                        <button class="btn btn-default" onclick="EditProjectModal.editProject('<?php echo $sim_project->getProjectId(); ?>')"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar proyecto</button>
                        <button data-toggle="dropdown" class="btn btn-default dropdown-toggle"><span class="caret"></span></button>
                        <ul class="dropdown-menu">
                            <!--<li><a href="#" onclick="DeleteProjectModal.deleteProject('<?php echo $sim_project->getProjectId(); ?>')"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar proyecto</a></li>-->
                            <?php echo '<li><a href="'. $CONF_PROP["dnse3_root"] . "/api/users/" . $user->getUserName() . "/projects/" . $sim_project->getProjectId() . '/model" download><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Descargar modelo</a></li>' ?>
                        </ul>
                    </div>
                    </li>
                </ul>
            </div>
        </nav>

        <div class="container-fluid">
            <div class="row">
                <div id="sim-project-details" class="col-sm-12 col-md-3">
                    <!-- Nav tabs -->
                    <ul id="sim-project-details-tabs" class="nav nav-tabs" role="tablist">
                        <li role="presentation" class="active" id="sim-project-details-tabs-t1"><a href="#sim-project-details-tp-desc" aria-controls="sim-project-details-tp-desc" role="tab" data-toggle="tab">Descripción</a></li>
                        <li role="presentation" id=sim-project-details-tabs-t2"><a href="#sim-project-details-tp-params" aria-controls="sim-project-details-tp-params" role="tab" data-toggle="tab">Parámetros</a></li>
                        <li role="presentation" id=sim-project-details-tabs-t3"><a href="#sim-project-details-tp-files" aria-controls="sim-project-details-tp-files" role="tab" data-toggle="tab">Ficheros de resultados</a></li>
                    </ul>
                    <!-- Tab panes -->
                    <div class="tab-content">
                        <div role="tabpanel" class="tab-pane active" id="sim-project-details-tp-desc">
                            <div class="information-panel-content">
                                <?php
                                   $project_creation_date_field = getDateFieldText($sim_project->getCreationDate(), $now_date);
                                   $project_update_date_field = getDateFieldText($sim_project->getUpdateDate(), $now_date);
                                ?>
                                
                                <span class="title">Nombre</span>
                                <?php echo $sim_project->getName();?>
                                <span class="title">Descripción</span>
                                <?php echo $sim_project->getDescription();?>
                                <div class="row">
                                    <div class="col-sm-6 col-lg-6"><span class="title">Fecha de creación</span></div>
                                    <div class="col-sm-6 col-lg-6 content-value"><?php echo $project_creation_date_field;?></div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-6 col-lg-6"><span class="title">Última modificación</span></div>
                                    <div class="col-sm-6 col-lg-6 content-value"><?php echo $project_update_date_field;?></div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-6 col-lg-6"><span class="title">Simulaciones individuales</span></div>
                                    <div class="col-sm-6 col-lg-6 content-value"><?php echo $sim_project->getNumSingleSimulations();?></div>
                                </div>
                                <div class="row">
                                    <div class="col-sm-6 col-lg-6"><span class="title">Barrido de parámetros</span></div>
                                    <div class="col-sm-6 col-lg-6 content-value"><?php echo $sim_project->getNumParameterSweepSimulations();?></div>
                                </div>
                            </div>
                        </div>
                        <div role="tabpanel" class="tab-pane" id="sim-project-details-tp-params">
                            <?php 
                            foreach($param_descriptions as $param_desc){
                                echo '<div class="information-panel">';
                                echo '<div class="information-panel-title expandable">';
                                echo '<span class="title">'. $param_desc->getName() . '<span class="glyphicon glyphicon-triangle-bottom pull-right" aria-hidden="true"></span></span>';
                                echo '</div>';
                                echo '<div class="information-panel-content">';
                                /*echo '<span class="title">Nombre</span>';
                                echo $param_desc->getName();*/
                                
                                echo '<div class="row">';
                                echo '<div class="col-sm-6 col-lg-6"><span class="title">Tipo de parámetro</span></div>';
                                echo '<div class="col-sm-6 col-lg-6 content-value">' . ParameterType::getDescription($param_desc->getType()) . '</div>';
                                echo '</div>';
                                
                                $pv = $param_desc->getPossibleValues();
                                if (!is_null($pv)){
                                    echo '<div class="row">';
                                    echo '<div class="col-sm-6 col-lg-6"><span class="title">Posibles valores</span></div>';
                                    echo '<div class="col-sm-6 col-lg-6 content-value">';
                                    for($i=0; $i< count($pv); $i++){
                                        echo $pv[$i];
                                        if ($i < (count($pv)-1)){
                                            echo ', ';
                                        }
                                    }
                                    echo '</div>';
                                    echo '</div>';
                                }
                                
                                $pg = $param_desc->getGreaterThan();
                                $pge = $param_desc->getGreaterThanOrEqualTo();
                                $pl = $param_desc->getLessThan();
                                $ple = $param_desc->getLessThanOrEqualTo();
                                
                                if (!is_null($pg) || !is_null($pge) || !is_null($pl) || !is_null($ple)){
                                    echo '<div class="row">';
                                    echo '<div class="col-sm-6 col-lg-6"><span class="title">Rango</span></div>';
                                    echo '<div class="col-sm-6 col-lg-6 content-value">';
                                    if (!is_null($pg)){
                                        echo $pg.' < ';
                                    }else if (!is_null($pge)){
                                        echo $pge.' <= ';
                                    }
                                    echo '#';
                                    if (!is_null($pl)){
                                        echo ' < '.$pl;
                                    }else if (!is_null($ple)){
                                        echo ' <= '.$ple;
                                    }
                                    echo '</div>';
                                    echo '</div>';
                                }
                                echo '<div class="row">';
                                echo '<div class="col-sm-6 col-lg-6"><span class="title">Valor por defecto</span></div>';
                                echo '<div class="col-sm-6 col-lg-6 content-value">';
                                if ($param_desc->getDefaultValue()!=null){
                                    echo $param_desc->getDefaultValue();
                                }else{
                                    echo 'random';
                                }
                                echo '</div>';
                                echo '</div>';
                                
                                
                                echo '</div>';
                                echo '</div>';
                            }
                            ?>
                        </div>
                        <div role="tabpanel" class="tab-pane" id="sim-project-details-tp-files">
                            <?php
                            foreach($out_files as $out_file){
                                $out_file_struct = new OutputFileStructure($out_file->getOutputFileStructure());
                                echo '<div class="information-panel">';
                                echo '<div class="information-panel-title expandable">';
                                echo '<span class="title">'. $out_file->getOutputFileName() . '<span class="glyphicon glyphicon-triangle-bottom pull-right" aria-hidden="true"></span></span>';
                                echo '</div>';
                                echo '<div class="information-panel-content">';
                                
                                echo '<div class="row">';
                                echo '<div class="col-sm-6 col-lg-6"><span class="title">Tipo</span></div>';
                                echo '<div class="col-sm-6 col-lg-6 content-value">' . OutputFileType::getDescription($out_file->getType()) . '</div>';
                                echo '</div>';
                                
                                echo '<div class="row">';
                                echo '<div class="col-sm-6 col-lg-6"><span class="title">Multilínea</span></div>';
                                echo '<div class="col-sm-6 col-lg-6 content-value">';
                                if($out_file->getType() === "TRACE_FILE"){
                                    echo 'No';
                                }else{
                                    if ($out_file_struct->isMultiline()){
                                        echo 'Sí';
                                    }else{
                                        echo 'No';
                                    }
                                }
                                echo '</div>';
                                echo '</div>';
                                
                                echo '<div class="row">';
                                echo '<div class="col-sm-6 col-lg-6"><span class="title">Variables de salida</span></div>';
                                echo '<div class="col-sm-6 col-lg-6 content-value">';
                                if($out_file->getType() === "TRACE_FILE"){
                                    echo '-';
                                }else{
                                    $output_vars = $out_file_struct->getOutputVariables();
                                    for ($i = 0; $i < count($output_vars); $i++) {
                                        echo $output_vars[$i];
                                        if ($i < (count($output_vars) - 1)) {
                                            echo ', ';
                                        }
                                    }
                                }
                                echo '</div>';
                                echo '</div>';
                                
                                echo '</div>';
                                echo '</div>';
                            }
                            ?>
                            </div>
                        </div>
                    </div>                      
                <div id="sim-project-simulations" class="col-sm-12 col-md-9">
                    <!-- Nav tabs -->
                    <!--<ul id="sim-project-tabs" class="nav nav-tabs" role="tablist">
                        <li role="presentation" class="active" id="sim-project-tabs-t1"><a href="#sim-project-tp-single-sim" aria-controls="sim-project-tp-single-sim" role="tab" data-toggle="tab">Simulaciones individuales</a></li>
                        <li role="presentation" id="sim-project-tabs-t2"><a href="#sim-project-tp-sweep-sim" aria-controls="sim-project-tp-sweep-sim" role="tab" data-toggle="tab">Barrido de parámetros</a></li>
                    </ul>-->
                    <!-- Tab panes -->
                    <!--<div class="tab-content">
                        <div role="tabpanel" class="tab-pane active" id="sim-project-tp-single-sim">-->
                            <div><h4>Simulaciones Individuales</h4></dvi>
                            <div class="table-responsive">
                            <table class="table table-striped table-bordered table-hover <?php if (count($single_sims) == 0){echo "hidden";}?>" id="singleSimulationsTable">
                                <thead>
                                    <tr>
                                        <th>Nombre</th>
                                        <th>Nº repeticiones</th>
                                        <th>Prioridad</th>
                                        <th>Fecha de creación</th>
                                        <th>Última modificación</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php
                                        foreach ($single_sims as $single_sim) {
                                            
                                            $single_sim_creation_date_field = getDateFieldText($single_sim->getCreationDate(), $now_date);
                                            $single_sim_update_date_field = getDateFieldText($single_sim->getUpdateDate(), $now_date);
                                            
                                            echo "<tr>";
                                            echo '<td>' . $single_sim->getName() . '</td>';
                                            echo "<td>" . $single_sim->getNumRepetitions() . "</td>";
                                            echo "<td>" . $single_sim->getPriority() . "</td>";
                                            echo "<td>" . $single_sim_creation_date_field . "</td>";
                                            echo "<td>" . $single_sim_update_date_field . "</td>";
                                            $status = $single_sim ->getStatus();
                                            $status_description = SimulationStatus::getDescription($status);
                                            echo "<td class=\"status\">";
                                            echo "<div>";
                                            switch($status){
                                                case SimulationStatus::WAITING :   echo '<span class="glyphicon glyphicon-pause" aria-hidden="true"></span>';
                                                                                     echo '<span class="glyphicon glyphicon-cog" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' . $status_description . '"></span><span class="sr-only">' . $status_description. '</span>';
                                                                                     
                                                                                     break;
                                                case SimulationStatus::PREPARING :     $progress_bar_type = "progress-bar-warning";
                                                                                     echo '<div class="progress">';
                                                                                     echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="100" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                     echo '</div>';
                                                                                     echo '<span class="glyphicon glyphicon-time" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description .'"></span><span class="sr-only">'. $status_description. '</span>';

                                                                                     break;
                                                case SimulationStatus::PROCESSING :  $progress_bar_type = "";
                                                                                     echo '<div class="progress">';
                                                                                     echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped active" role="progressbar" aria-valuenow="' . $single_sim->getPercentageCompleted(2) . '" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: ' . $single_sim->getPercentageCompleted(0) . '%;">' .
                                                                                     $single_sim->getPercentageCompleted(2) . '%' .
                                                                                     '</div>';
                                                                                     echo '</div>';
                                                                                     echo '<span class="sr-only">' . $single_sim->getPercentageCompleted(2). '% Completed</span>';
                                                                                     echo '<span class="glyphicon glyphicon-play" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' . $status_description. '"></span><span class="sr-only">' . $status_description. '</span>';   
                                                                                     break;

                                                case SimulationStatus::PAUSED :      $progress_bar_type = "";
                                                                                     echo '<div class="progress">';
                                                                                     echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="' . $single_sim->getPercentageCompleted(2) . '" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: ' . $single_sim->getPercentageCompleted(0) . '%;">' .
                                                                                     $single_sim->getPercentageCompleted(2) . '%' .
                                                                                     '</div>';
                                                                                     echo '</div>';
                                                                                     echo '<span class="sr-only">' . $single_sim->getPercentageCompleted(2). '% Completed</span>';
                                                                                     echo '<span class="glyphicon glyphicon-pause" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description. '"></span><span class="sr-only">'. $status_description. '</span>';
                                                                                     break;

                                                case SimulationStatus::CLEANING :    $progress_bar_type = "progress-bar-info";
                                                                                     echo '<div class="progress">';
                                                                                     echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                     echo '</div>';
                                                                                     echo '<span class="glyphicon glyphicon-erase" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description. '"></span><span class="sr-only">'. $status_description. '</span>';                                                                              
                                                                                     break;
                                                case SimulationStatus::ERROR :       $progress_bar_type = "progress-bar-danger";
                                                                                     echo '<div class="progress">';
                                                                                     echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                     echo '</div>';
                                                                                     echo '<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description. '"></span><span class="sr-only">'. $status_description. '</span>';
                                                                                     break;

                                                case SimulationStatus::REPORTING :   $progress_bar_type = "progress-bar-info";
                                                                                     echo '<div class="progress">';
                                                                                     echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                     echo '</div>';
                                                                                     echo '<span class="glyphicon glyphicon-hourglass" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description. '"></span><span class="sr-only">'. $status_description. '</span>';
                                                                                     break;

                                                case SimulationStatus::FINISHED :    $progress_bar_type = "progress-bar-success";
                                                                                     echo '<div class="progress">';
                                                                                     echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                     echo '</div>';
                                                                                     echo '<span class="glyphicon glyphicon-ok" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description. '"></span><span class="sr-only">'. $status_description. '</span>';
                                                                                     break;

                                                case SimulationStatus::REMOVING :    $progress_bar_type = "progress-bar-danger";
                                                                                     echo '<div class="progress">';
                                                                                     echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                     echo '</div>';
                                                                                     echo '<span class="glyphicon glyphicon-ban-circle" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description. '"></span><span class="sr-only">'. $status_description. '</span>';
                                                                                     break;

                                                default:        echo $status;
                                            }
                                            echo "</div>";
                                            echo "</td>";
                                            //echo "<td>" . $single_sim->getStatus() . "</td>";
                                            $disabled_actions = "";
                                            if ($status == SimulationStatus::REMOVING){
                                                $disabled_actions = 'disabled="disabled"';
                                            }
                                            echo '<td class="actions"><div class="btn-group actions-group">
                                                <button class="btn btn-default btn-sm" ' . $disabled_actions . ' onclick="ViewSingleSimulationModal.viewSingleSimulation(' . $single_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver detalles</button>
                                                <button data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle" ' . $disabled_actions . '><span class="caret"></span></button>
                                                <ul class="dropdown-menu">
                                                    <li><a href="#" onclick="EditSimulationModal.editSimulation(' . $single_sim->getSimulationId() . ',\'single\')"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar simulación</a></li>';
                                            switch($status){
                                                case SimulationStatus::WAITING :  //the simulation can be started
                                                                                    echo '<li><a href="#" onclick="SingleSimulationController.startSimulation(' . $single_sim->getSimulationId() . ');"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> Ejecutar simulación</a></li>';
                                                                                    break;
                                                case SimulationStatus::PREPARING :    //no operations are allowed
                                                                                    break;
                                                case SimulationStatus::PROCESSING : //the simulation can be paused or stopped
                                                                                    echo '<li><a href="#" onclick="SingleSimulationController.pauseSimulation(' . $single_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-pause" aria-hidden="true"></span> Pausar simulación</a></li>';
                                                                                    echo '<li><a href="#" onclick="SingleSimulationController.stopSimulation(' . $single_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-stop" aria-hidden="true"></span> Detener simulación</a></li>';
                                                                                    break;
                                                case SimulationStatus::PAUSED :     //the simulation can be resumed or stopped
                                                                                    echo '<li><a href="#" onclick="SingleSimulationController.startSimulation(' . $single_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> Reanudar simulación</a></li>';
                                                                                    //echo '<li><a href="#" onclick="SingleSimulationController.stopSimulation(' . $single_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-stop" aria-hidden="true"></span> Detener simulación</a></li>';
                                                                                    break;
                                                case SimulationStatus::CLEANING : //no operations are allowed
                                                                                  break;
                                                case SimulationStatus::ERROR : //no operations are allowed
                                                                                  break;
                                                case SimulationStatus::REPORTING : //no operations are allowed
                                                                                  break;
                                                case SimulationStatus::FINISHED : echo '<li><a href="'. $CONF_PROP["dnse3_root"] . "/api/users/". $username. "/projects/". $project_id. '/singlesimulations/' . $single_sim->getSimulationId(). '/results" download><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Obtener resultados</a></li>';
                                                                                  /*echo '<li><a href="#" onclick="SingleSimulationController.getResultsRequest(' . $single_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> Ver resultados</a></li>';*/
                                                                                  break;
                                                case SimulationStatus::REMOVING : //no operations are allowed
                                                                                  break;                                                                          
                                            }
                                            echo '<li><a href="#" onclick="DeleteSimulationModal.deleteSimulation(' . $single_sim->getSimulationId() . ',\'single\')"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar simulación</a></li>
                                                </ul>
                                            </div></td>';
                                            echo "</tr>";
                                        }
                                    ?>
                            </table>
                            </div>
                            <div id="noSingleSimulationsAlert" class="alert alert-info <?php if (count($single_sims) > 0){echo "hidden";}?>" role="alert"><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span><span class="sr-only">Info: </span> No se ha creado todavía ninguna simulación individual.</div>
                        <!--</div>
                        <div role="tabpanel" class="tab-pane" id="sim-project-tp-sweep-sim">-->
                            <div><h4>Simulaciones de barrido de parámetro</h4></div>
                            <div class="table-responsive">
                            <table class="table table-striped table-bordered table-hover <?php if (count($param_sweep_sims) == 0){echo "hidden";}?>" id="paramSweepSimulationsTable">
                                <thead>
                                    <tr>
                                        <th>Nombre</th>
                                        <th>Nº repeticiones</th>
                                        <th>Prioridad</th>
                                        <th>Fecha de creación</th>
                                        <th>Última modificación</th>
                                        <th>Estado</th>
                                        <th>Acciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <?php
                                    foreach ($param_sweep_sims as $param_sweep_sim) {
                                        
                                        $param_sweep_sim_creation_date_field = getDateFieldText($param_sweep_sim->getCreationDate(), $now_date);
                                        $param_sweep_sim_update_date_field = getDateFieldText($param_sweep_sim->getUpdateDate(), $now_date);
                                
                                        echo "<tr>";
                                        echo '<td>' . $param_sweep_sim->getName() . '</td>';
                                        echo "<td>" . $param_sweep_sim->getNumRepetitions() . "</td>";
                                        echo "<td>" . $param_sweep_sim->getPriority() . "</td>";
                                        echo "<td>" . $param_sweep_sim_creation_date_field . "</td>";
                                        echo "<td>" . $param_sweep_sim_update_date_field . "</td>";
                                        $status = $param_sweep_sim->getStatus();
                                        $status_description = SimulationStatus::getDescription($status);
                                        echo "<td class=\"status\">";
                                        echo "<div>";
                                        switch($status){
                                            case SimulationStatus::WAITING :   echo '<span class="glyphicon glyphicon-pause" aria-hidden="true"></span>';
                                                                                 echo '<span class="glyphicon glyphicon-cog" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' . $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';                                                                              
                                                                                 break;
                                            case SimulationStatus::PREPARING :     $progress_bar_type = "progress-bar-warning";
                                                                                 echo '<div class="progress">';
                                                                                 echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped active" role="progressbar" aria-valuenow="100" aria-valuemin="100" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                 echo '</div>';
                                                                                 echo '<span class="glyphicon glyphicon-time" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="' . $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';                                                                              
                                                                                 break;
                                            case SimulationStatus::PROCESSING :  $progress_bar_type = "";
                                                                                 echo '<div class="progress">';
                                                                                 echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped active" role="progressbar" aria-valuenow="' . $param_sweep_sim->getPercentageCompleted(2) . '" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: ' . $param_sweep_sim->getPercentageCompleted(0) . '%;">' .
                                                                                 $param_sweep_sim->getPercentageCompleted(2) . '%' .
                                                                                 '</div>';
                                                                                 echo '</div>';
                                                                                 echo '<span class="sr-only">' . $param_sweep_sim->getPercentageCompleted(2). '% Completed</span>';
                                                                                 echo '<span class="glyphicon glyphicon-play" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';   
                                                                                 break;
                                            case SimulationStatus::PAUSED :      $progress_bar_type = "";
                                                                                 echo '<div class="progress">';
                                                                                 echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="' . $param_sweep_sim->getPercentageCompleted(2) . '" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: ' . $param_sweep_sim->getPercentageCompleted(0) . '%;">' .
                                                                                 $param_sweep_sim->getPercentageCompleted(2) . '%' .
                                                                                 '</div>';
                                                                                 echo '</div>';
                                                                                 echo '<span class="sr-only">' . $param_sweep_sim->getPercentageCompleted(2). '% Completed</span>';
                                                                                 echo '<span class="glyphicon glyphicon-pause" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';                                                                              
                                                                                 break;
                                            case SimulationStatus::CLEANING :    $progress_bar_type = "progress-bar-info";
                                                                                 echo '<div class="progress">';
                                                                                 echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                 echo '</div>';
                                                                                 echo '<span class="glyphicon glyphicon-erase" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';                                                                              
                                                                                 break;
                                            case SimulationStatus::ERROR :       $progress_bar_type = "progress-bar-danger";
                                                                                 echo '<div class="progress">';
                                                                                 echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                 echo '</div>';
                                                                                 echo '<span class="glyphicon glyphicon-exclamation-sign" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';                                                                              
                                                                                 break;
                                            case SimulationStatus::REPORTING :   $progress_bar_type = "progress-bar-info";
                                                                                 echo '<div class="progress">';
                                                                                 echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                 echo '</div>';
                                                                                 echo '<span class="glyphicon glyphicon-hourglass" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';                                                                              
                                                                                 break;
                                            case SimulationStatus::FINISHED :    $progress_bar_type = "progress-bar-success";
                                                                                 echo '<div class="progress">';
                                                                                 echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                 echo '</div>';
                                                                                 echo '<span class="glyphicon glyphicon-ok" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';                                                                              
                                                                                 break;
                                            case SimulationStatus::REMOVING :    $progress_bar_type = "progress-bar-danger";
                                                                                 echo '<div class="progress">';
                                                                                 echo '<div class="progress-bar ' . $progress_bar_type . ' progress-bar-striped" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="min-width: 3em; width: 100%;"></div>';
                                                                                 echo '</div>';
                                                                                 echo '<span class="glyphicon glyphicon-ban-circle" aria-hidden="true" data-toggle="tooltip" data-placement="top" title="'. $status_description . '"></span><span class="sr-only">'. $status_description . '</span>';                                                                              
                                                                                 break;
                                            default:                             echo $status;
                                        }
                                        echo "</div>";
                                        echo "</td>";
                                        //echo "<td>" . $status . "</td>";
                                        $disabled_actions = "";
                                        if ($status == SimulationStatus::REMOVING){
                                            $disabled_actions = 'disabled="disabled"';
                                        }
                                        echo '<td class="actions"><div class="btn-group actions-group">
                                                <button class="btn btn-default btn-sm" ' . $disabled_actions . ' onclick="ViewParameterSweepSimulationModal.viewParameterSweepSimulation(' . $param_sweep_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver detalles</button>
                                                <button data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle" ' . $disabled_actions . '><span class="caret"></span></button>
                                                <ul class="dropdown-menu">
                                                    <li><a href="#" onclick="EditSimulationModal.editSimulation(' .$param_sweep_sim->getSimulationId() . ',\'sweep\')"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar simulación</a></li>';
                                            
                                            switch($status){
                                                case SimulationStatus::WAITING :  //the simulation can be started
                                                                                    echo '<li><a href="#" onclick="ParameterSweepSimulationController.startSimulation(' . $param_sweep_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> Ejecutar simulación</a></li>';
                                                                                    break;
                                                case SimulationStatus::PREPARING :    //no operations are allowed
                                                                                    break;
                                                case SimulationStatus::PROCESSING : //the simulation can be paused or stopped
                                                                                    echo '<li><a href="#" onclick="ParameterSweepSimulationController.pauseSimulation(' . $param_sweep_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-pause" aria-hidden="true"></span> Pausar simulación</a></li>';
                                                                                    echo '<li><a href="#" onclick="ParameterSweepSimulationController.stopSimulation(' . $param_sweep_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-stop" aria-hidden="true"></span> Detener simulación</a></li>';
                                                                                    break;
                                                case SimulationStatus::PAUSED :     //the simulation can be resumed or stopped
                                                                                    echo '<li><a href="#" onclick="ParameterSweepSimulationController.startSimulation(' . $param_sweep_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-play" aria-hidden="true"></span> Reanudar simulación</a></li>';
                                                                                    //echo '<li><a href="#" onclick="ParameterSweepSimulationController.stopSimulation(' . $param_sweep_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-stop" aria-hidden="true"></span> Detener simulación</a></li>';
                                                                                    break;
                                                case SimulationStatus::CLEANING : //no operations are allowed
                                                                                  break;
                                                case SimulationStatus::ERROR : //no operations are allowed
                                                                                  break;
                                                case SimulationStatus::REPORTING : //no operations are allowed
                                                                                  break;
                                                case SimulationStatus::FINISHED : echo '<li><a href="'. $CONF_PROP["dnse3_root"] . "/api/users/". $username. "/projects/". $project_id. '/parametersweepsimulations/' . $param_sweep_sim->getSimulationId() . '/results" download><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Obtener resultados</a></li>';
                                                                                  /*echo '<li><a href="#" onclick="ParameterSweepSimulationController.showResults(' . $param_sweep_sim->getSimulationId() . ')"><span class="glyphicon glyphicon-list-alt" aria-hidden="true"></span> Ver resultados</a></li>';*/
                                                                 break;
                                                case SimulationStatus::REMOVING : //no operations are allowed
                                                                                  break;                                                                             
                                            }
                                                    
                                            echo '<li><a href="#" onclick="DeleteSimulationModal.deleteSimulation(' . $param_sweep_sim->getSimulationId() . ',\'sweep\')"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar simulación</a></li>
                                                </ul>
                                            </div></td>';
                                        echo "</tr>";
                                    }
                                    ?>
                            </table>
                            </div>
                            <div id="noParamSweepSimulationsAlert" class="alert alert-info <?php if (count($param_sweep_sims) > 0){echo "hidden";}?>" role="alert"><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span><span class="sr-only">Info: </span> No se ha creado todavía ninguna simulación de barrido de parámetros.</div>                    
                        <!--</div>
                    </div>-->
                </div>
            </div>           
        </div>
        </div>
        <?php
        require_once(dirname(__FILE__) . "/footer.php");
        ?>
    </body>
</html>
