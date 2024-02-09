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
        <link rel="icon" type="image/png" sizes="32x32" href="/images/favicon-32.png">
        <link rel="icon" type="image/png" sizes="16x16" href="/images/favicon-16.png">
        <link rel="apple-touch-icon" sizes="180x180" href="/images/apple-touch-icon.png">
        <link rel="manifest" href="/images/site.webmanifest">

        <!-- Bootstrap -->
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/lib/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet">
        <!-- wizzard -->
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/lib/bootstrap-3.3.7/css/bootstrap.min.css" rel="stylesheet">

        <!-- Custom styles -->
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/css/common.css" rel="stylesheet">
        <link href="<?php echo $CONF_PROP["dnse3_root"];?>/css/main.css" rel="stylesheet">
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
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/mainInit.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/utils/wizard.js"></script>
        <!-- modals scripts -->
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/project/NewProjectModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/project/editProjectModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/project/viewProjectModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/project/DeleteProjectModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/newSimulation/NewSimulationModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/newSimulation/NewSingleSimulation.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/newSimulation/NewParameterSweepSimulation.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/newSimulation/NewSimulationFileGathering.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/InfoModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/ErrorModal.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/modals/SessionExpiredModal.js"></script>
        
        <!-- painters -->
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/painters/ProjectsPainter.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/painters/CurrentSimulationsPainter.js"></script>
        <!-- data model -->
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/User.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/Parameter.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/ParameterResource.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/OutputFileResource.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/OutputFileStructure.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/SimulationStatus.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/OutputFileType.js"></script>
        <script type="text/javascript" src="<?php echo $CONF_PROP["dnse3_root"];?>/javascript/model/ParameterType.js"></script>

    </head>
    <body>
        <div id="sticky-wrapper">
        <?php
        /**
         * add the modals here
         */
        require_once(dirname(__FILE__) . "/modals/project/newProject.php");
        require_once(dirname(__FILE__) . "/modals/project/editProject.php");
        require_once(dirname(__FILE__) . "/modals/project/viewProject.php");
        require_once(dirname(__FILE__) . "/modals/project/deleteProject.php");
        require_once(dirname(__FILE__) . "/modals/newSimulation/newSimulation.php");
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
                    <a class="navbar-brand dnse3-logo" href="<?php echo $CONF_PROP["dnse3_root"];?>">
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
                    <h1>Proyectos</h1>
                </div>
            </div>
        </div>

        <nav class="navbar navbar-default navbar-static-top" role="navigation">
            <div class="container-fluid">
                <ul class="nav navbar-nav navbar-left">
                    <ol class="breadcrumb list-inline bigger-size">
                        <li class="active"><a href="#"><span class="glyphicon glyphicon-home" aria-hidden="true"></span></a></li>
                    </ol>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <!--<div class="btn-group">
                        <button type="button" class="btn btn-primary navbar-btn" onclick="NewProjectModal.newProject()">
                            <span class="glyphicon glyphicon-plus"></span> Nuevo proyecto
                        </button>
                    </div>-->
                    <!--
                    <li class="dropdown">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Menú <span class="caret"></span></a>
                        <ul class="dropdown-menu">
                            <li><a href="#">Editar proyecto</a></li>
                            <li><a href="#">Eliminar proyecto</a></li>
                        </ul>
                    </li>
                    <li>
                    -->
                    <!--
                    <div class="btn-group">
                        <button class="btn btn-default navbar-btn">Editar proyecto</button>
                        <button data-toggle="dropdown" class="btn navbar-btn btn-default dropdown-toggle"><span class="caret"></span></button>
                        <ul class="dropdown-menu">
                            <li><a href="#">Eliminar proyecto</a></li>
                        </ul>
                    </div>
                    -->
                    </li>
                </ul>
                <!--
                <form class="navbar-form navbar-right">
                    <button type="button" class="btn btn-primary">
                        <span class="glyphicon glyphicon-plus"></span> Nuevo proyecto
                    </button>
                </form>
                -->
            </div>
        </nav>

        <div class="container-fluid">

            <div class="row">
                <div class="col-md-12">
                    <div class="table-responsive">
                    <table class="table table-striped table-bordered table-hover <?php if (count($sim_projects) == 0){echo "hidden";}?>" id="simulationProjectsTable">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Simulaciones Individuales</th>
                                <th>Barrido de parámetros</th>
                                <th>Fecha de creación</th>
                                <th>Última modificación</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php                           
                            foreach ($sim_projects as $sim_project) {
                                if ($sim_project->isRemoving()!==true){
                                    $project_creation_date_field = getDateFieldText($sim_project->getCreationDate(), $now_date);
                                    $project_update_date_field = getDateFieldText($sim_project->getUpdateDate(), $now_date);

                                    echo '<tr id="tr_projectId_' . $sim_project->getProjectId() . '">';
                                    echo '<td><a href="' . $CONF_PROP["dnse3_root"]. '/php/controller/simulationProject.php?projectId=' . $sim_project->getProjectId(). '">' . $sim_project->getName(). '</a></td>';
                                    echo "<td>" . $sim_project->getNumSingleSimulations() . "</td>";
                                    echo "<td>" . $sim_project->getNumParameterSweepSimulations() . "</td>";
                                    echo "<td>" . $project_creation_date_field . "</td>";
                                    echo "<td>" . $project_update_date_field . "</td>";
                                    echo '<td class="actions"><div class="btn-group actions-group">
                                            <button class="btn btn-default btn-sm" onclick="ViewProjectModal.viewProject(' . $sim_project->getProjectId() . ')"><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver detalles</button>
                                            <button data-toggle="dropdown" class="btn btn-default btn-sm dropdown-toggle"><span class="caret"></span></button>
                                            <ul class="dropdown-menu">
                                                <li><a href="#" onclick="NewSimulationModal.newSimulation(' . $sim_project->getProjectId() . ')"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Nueva simulación</a></li>
                                                <!--<li><a href="#" onclick="EditProjectModal.editProject(' . $sim_project->getProjectId() . ')"><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar proyecto</a></li>-->
                                                <li><a href="#" onclick="DeleteProjectModal.deleteProject(' . $sim_project->getProjectId() . ')"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar proyecto</a></li>
                                                <li><a href="'. $CONF_PROP["dnse3_root"] . "/api/users/" . $user->getUserName() . "/projects/" . $sim_project->getProjectId() . '/model" download><span class="glyphicon glyphicon-download-alt" aria-hidden="true"></span> Descargar modelo</a></li>
                                            </ul>
                                        </div></td>';
                                    echo "</tr>";
                                }
                            }
                            ?>
                            <!--
                            <tr>
                                <td>un nombre</td>
                                <td>3</td>
                                <td>3</td>
                                <td>Fecha</td>
                                <td>Fecha</td>
                                <td>                          
                                    <a href="#" class="btn btn-success btn-xs"><i class="fa fa-folder"></i><span class="glyphicon glyphicon-search" aria-hidden="true"></span> Ver </a>
                                    <a href="#" class="btn btn-info btn-xs"><i class="fa fa-pencil"></i><span class="glyphicon glyphicon-edit" aria-hidden="true"></span> Editar </a>
                                    <a href="#" class="btn btn-danger btn-xs"><i class="fa fa-trash-o"></i><span class="glyphicon glyphicon-trash" aria-hidden="true"></span> Eliminar </a>
                                </td>
                            </tr>
                            <tr>
                                <td>un nombre</td>
                                <td>3</td>
                                <td>3</td>
                                <td>Fecha</td>
                                <td>Fecha</td>
                                <td><div class="btn-group">
                                        <button class="btn btn-default navbar-btn">Ver detalles</button>
                                        <button data-toggle="dropdown" class="btn navbar-btn btn-default dropdown-toggle"><span class="caret"></span></button>
                                        <ul class="dropdown-menu">
                                            <li><a href="#">Nueva simulación</a></li>
                                            <li><a href="#">Editar proyecto</a></li>
                                            <li><a href="#">Eliminar proyecto</a></li>
                                        </ul>
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td>un nombre</td>
                                <td>3</td>
                                <td>3</td>
                                <td>Fecha</td>
                                <td>Fecha</td>
                                <td><div class="btn-group">
                                        <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                            <span class="glyphicon glyphicon-cog"></span> Menú <span class="caret"></span>
                                        </button>
                                        <ul class="dropdown-menu">
                                            <li><a href="#">Ver detalles</a></li>
                                            <li><a href="#">Nueva simulación</a></li>
                                            <li><a href="#">Editar proyecto</a></li>
                                            <li role="separator" class="divider"></li>
                                            <li><a href="#">Eliminar proyecto</a></li>
                                        </ul>
                                    </div></td>
                            </tr>
                            -->
                    </table>
                    </div>
                    <div id="noSimulationProjectsAlert" class="alert alert-info <?php if (count($sim_projects) > 0){echo "hidden";}?>" role="alert"><span class="glyphicon glyphicon-info-sign" aria-hidden="true"></span><span class="sr-only">Info: </span> No se ha creado todavía ningún proyecto.</div>
                </div>
            </div>
            <!--<button onclick="NewSimulationModal.newSimulation('5')">Test</button>-->
            <!-- Button trigger modal -->
            <!--<button type="button" class="btn btn-primary btn-lg" data-toggle="modal" data-target="#myModal">
              Launch demo modal
            </button>-->
        </div>
        </div>    
        <?php
        require_once(dirname(__FILE__) . "/footer.php");
        ?>
    </body>
</html>
