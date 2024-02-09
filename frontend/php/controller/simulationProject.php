<?php
require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../model/UsersManager.php");
require_once(dirname(__FILE__)."/../model/SimulationProjectsManager.php");
require_once(dirname(__FILE__)."/../model/ParametersManager.php");
require_once(dirname(__FILE__)."/../model/OutputFileStructuresManager.php");
require_once(dirname(__FILE__)."/../model/OutputFilesManager.php");
require_once(dirname(__FILE__)."/../model/SingleSimulationsManager.php");
require_once(dirname(__FILE__)."/../model/ParameterSweepSimulationsManager.php");
require_once(dirname(__FILE__)."/../model/SimulationStatus.php");
require_once(dirname(__FILE__)."/../model/ParameterType.php");
require_once(dirname(__FILE__)."/../model/OutputFileType.php");

require_once(dirname(__FILE__)."/../core/dates.php");

//start the session to save the variables
session_start();
if (isset($_GET)){
    //check if the user has a simulation project with that id
    if (isset($_GET["projectId"]) && !empty($_GET["projectId"])){
        $project_id = $_GET["projectId"];
        $username = $_SESSION["username"];
        $sim_projects_man = new SimulationProjectsManager($username);
        $response = $sim_projects_man->getSimulationProject($project_id);
        //check what happens if the project doesn't 
        if ($response["status"] == 200){
            $sim_project = $response["data"];
            $_SESSION["projectId"] = $project_id;
        }else if ($response["status"] == 404){//the user is not the owner of a project with that id. unset the session var if it exists
            if (isset($_SESSION["projectId"])){
                unset($_SESSION["projectId"]);
            }
        }
    }
}
if (isset($_SESSION["projectId"]) && !empty($_SESSION["projectId"])){
    $username = $_SESSION["username"];
    $project_id = $_SESSION["projectId"];
    $uc = new UsersManager();
    $response = $uc->getUser($username);
    $user = $response["data"];
    
    $param_desc_manager = new ParametersManager($username, $project_id);
    $response = $param_desc_manager->getParameters();
    $param_descriptions = $response["data"];
    
    $out_file_manager = new OutputFilesManager($username, $project_id);
    $response = $out_file_manager->getOutputFiles();
    $out_files = $response["data"];
    
    $single_sims_manager = new SingleSimulationsManager($username, $project_id);
    $response = $single_sims_manager->getSingleSimulations();
    $single_sims = $response["data"];
    
    $sweep_sims_manager = new ParameterSweepSimulationsManager($username, $project_id);
    $response = $sweep_sims_manager->getParameterSweepSimulations();
    $param_sweep_sims = $response["data"];      
    
    $now_date = date('Y-m-d\TH:i:s\Z', time());
    
    require_once(dirname(__FILE__)."/../view/simulationProject.php");
}else{
    //require_once(dirname(__FILE__)."/../view/main.php");
    header('Location: '. $CONF_PROP["dnse3_root"]. "/php/controller/login.php");
}
?>