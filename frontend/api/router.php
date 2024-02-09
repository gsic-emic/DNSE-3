<?php

/* Copyright (C) 2015 Intelligent & Cooperative Systems Research Group/Education,
  Media, Computing & Culture (GSIC-EMIC). University of Valladolid(UVA).
  Valladolid, Spain. https://www.gsic.uva.es/

  This file is part of Web Collage.

  Web Collage is free software: you can redistribute it and/or modify
  it under the terms of the GNU Affero General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  Web Collage is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Affero General Public License for more details.

  You should have received a copy of the GNU Affero General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>. */
require_once("rest.php");
require_once("resources/UserListResource.php");
require_once("resources/UserResource.php");
require_once("resources/ResultsResource.php");
require_once("resources/SimulationProjectListResource.php");
require_once("resources/SimulationProjectResource.php");
require_once("resources/ParameterListResource.php");
require_once("resources/ParameterResourceListResource.php");
require_once("resources/OutputFileListResource.php");
require_once("resources/SimulationOutputFileListResource.php");
require_once("resources/OutputFileStructureListResource.php");
require_once("resources/SingleSimulationListResource.php");
require_once("resources/SingleSimulationResource.php");
require_once("resources/ParameterSweepSimulationListResource.php");
require_once("resources/ParameterSweepSimulationResource.php");
require_once("resources/ModelProjectResource.php");

session_start();
if (isset($_SESSION) && isset($_SESSION["username"]) && !empty($_SESSION["username"]) && isset($_SESSION["password"]) && !empty($_SESSION["password"])){
    //it is an already existing session (the user is log in)
    $username = $_SESSION["username"];
}else if (isset($_SERVER['PHP_AUTH_USER'])) {//the request is using basic authentication
    //check if the credentials are right
    $authenticated = ldap_authenticate($_SERVER['PHP_AUTH_USER'], $_SERVER['PHP_AUTH_PW']);
    if ($authenticated === 1){
        $username = $_SERVER['PHP_AUTH_USER'];
    }
}
if (!isset($username)){
    RestUtils::sendResponse(401, "Authentication required", 'text/html');
    exit;
}


$data = RestUtils::processRequest();
if (isset($_REQUEST["_route_"])) {
    $route = "/" . $_REQUEST["_route_"];
    $request_vars = $data->getRequestVars();
    $route_array = explode("/", $route);
    array_shift($route_array);
    if (strcmp($route_array[count($route_array) - 1], "") == 0) {
        array_pop($route_array);
    }
    if (strcmp($route_array[0], "users") == 0) {
        if (count($route_array)== 1){
            $method = $data->getMethod();
            $accept = $data->getHttpAccept();
            $user_list_resource = new UserListResource($method, $accept);
            $response = $user_list_resource->exec();
            RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
        }
        if (count($route_array)== 2){
            $method = $data->getMethod();
            $accept = $data->getHttpAccept();
            $user_resource = new UserResource($method, $accept, $username, $request_vars);
            $response = $user_resource->exec();
            RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
        }
        if (count($route_array)>= 3 && strcmp($route_array[2], "projects") == 0){
            if (count($route_array) == 3) {
                //echo "/DNSE3/api/users/{username}/projects/";
                $method = $data->getMethod();
                $accept = $data->getHttpAccept();
                //$username = $route_array[1];
                $sim_project_list_resource = new SimulationProjectListResource($method, $accept, $username);
                $response = $sim_project_list_resource->exec();
                RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
            }
            if (count($route_array) == 4) {
                //echo "/DNSE3/api/users/{username}/projects/{projectId}/";
                $method = $data->getMethod();
                $accept = $data->getHttpAccept();
                //$username = $route_array[1];
                $projectId = $route_array[3];
                $sim_project_resource = new SimulationProjectResource($method, $accept, $username, $projectId, $request_vars);
                $response = $sim_project_resource->exec();
                RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
            }
            if (count($route_array) == 5 && strcmp($route_array[4], "model") == 0) {
                //echo "DNSE3/api/users/{username}/projects/{projectId}/model
                $method = $data->getMethod();
                $accept = $data->getHttpAccept();
                $projectId = $route_array[3];
                $results_resource = new ModelProjectResource($method, $accept, $username, $projectId);
                $response = $results_resource->exec();
                RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
            }
            if (count($route_array) >= 5 && strcmp($route_array[4], "parameters") == 0) {
                if (count($route_array) == 5){
                    //echo "DNS3/api/users/{username}/projects/{projectId}/parameters/
                    $method = $data->getMethod();
                    $accept = $data->getHttpAccept();
                    //$username = $route_array[1];
                    $projectId = $route_array[3];
                    $param_desc_list_resource = new ParameterListResource($method, $accept, $username, $projectId);
                    $response = $param_desc_list_resource->exec();
                    RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                    
                }
            }
            if (count($route_array) >= 5 && strcmp($route_array[4], "outputfiles") == 0) {
                if (count($route_array) == 5){
                    //echo "DNS3/api/users/{username}/projects/{projectId}/outputfiles/
                    $method = $data->getMethod();
                    $accept = $data->getHttpAccept();
                    //$username = $route_array[1];
                    $projectId = $route_array[3];
                    $output_file_list_resource = new OutputFileListResource($method, $accept, $username, $projectId);
                    $response = $output_file_list_resource->exec();
                    RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                }
            }
            if (count($route_array) >= 5 && strcmp($route_array[4], "outputfilestructures") == 0) {
                if (count($route_array) == 5){
                    //echo "DNS3/api/users/{username}/projects/{projectId}/outputfilestructures/
                    $method = $data->getMethod();
                    $accept = $data->getHttpAccept();
                    //$username = $route_array[1];
                    $projectId = $route_array[3];
                    $output_file_strc_list_resource = new OutputFileStructureListResource($method, $accept, $username, $projectId);
                    $response = $output_file_strc_list_resource->exec();
                    RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                    
                }
            }
            if (count($route_array) >= 5 && strcmp($route_array[4], "singlesimulations") == 0) {
                if (count($route_array) == 5){
                    //echo "DNS3/api/users/{username}/projects/{projectId}/singlesimulations/
                    $method = $data->getMethod();
                    $accept = $data->getHttpAccept();
                    //$username = $route_array[1];
                    $projectId = $route_array[3];
                    $single_sim_list_resource = new SingleSimulationListResource($method, $accept, $username, $projectId, $request_vars);
                    $response = $single_sim_list_resource->exec();
                    RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                }
                if (count($route_array) == 6) {
                    //echo "DNS3/api/users/{username}/projects/{projectId}/singlesimulations/{simulationId}
                    $method = $data->getMethod();
                    $accept = $data->getHttpAccept();
                    $projectId = $route_array[3];
                    $simulationId = $route_array[5];
                    $single_sim_resource = new SingleSimulationResource($method, $accept, $username, $projectId, $simulationId, $request_vars);
                    $response = $single_sim_resource->exec();
                    RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                }
                if (count($route_array) == 7) {
                    if (strcmp($route_array[6], "parameters") == 0) {
                        //echo "DNS3/api/users/{username}/projects/{projectId}/singlesimulations/{simulationId}/parameters/
                        $method = $data->getMethod();
                        $accept = $data->getHttpAccept();
                        $projectId = $route_array[3];
                        $simulationId = $route_array[5];
                        $param_res_list_resource = new ParameterResourceListResource($method, $accept, $username, $projectId, $simulationId, "single");
                        $response = $param_res_list_resource->exec();
                        RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                    } else if (strcmp($route_array[6], "outputfiles") == 0) {
                        //echo "DNS3/api/users/{username}/projects/{projectId}/singlesimulations/{simulationId}/outputfiles/
                        $method = $data->getMethod();
                        $accept = $data->getHttpAccept();
                        $projectId = $route_array[3];
                        $simulationId = $route_array[5];
                        $sim_output_file_list_resource = new SimulationOutputFileListResource($method, $accept, $username, $projectId, $simulationId, "single");
                        $response = $sim_output_file_list_resource->exec();
                        RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                    }else if (strcmp($route_array[6], "results") == 0) {
                        //echo "DNS3/api/users/{username}/projects/{projectId}/singlesimulations/{simulationId}/results
                        $method = $data->getMethod();
                        $accept = $data->getHttpAccept();
                        $projectId = $route_array[3];
                        $simulationId = $route_array[5];
                        $results_resource = new ResultsResource($method, $accept, $username, $projectId, $simulationId, "single");
                        $response = $results_resource->exec();
                        RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                    }
                }
            }
            if (count($route_array) >= 5 && strcmp($route_array[4], "parametersweepsimulations") == 0) {
                if (count($route_array) == 5){
                    //echo "DNS3/api/users/{username}/projects/{projectId}/parametersweepsimulations/
                    $method = $data->getMethod();
                    $accept = $data->getHttpAccept();
                    //$username = $route_array[1];
                    $projectId = $route_array[3];
                    $param_sweep_sim_list_resource = new ParameterSweepSimulationListResource($method, $accept, $username, $projectId, $request_vars);
                    $response = $param_sweep_sim_list_resource->exec();
                    RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                }
                
                if (count($route_array) == 6) {
                    //echo "DNS3/api/users/{username}/projects/{projectId}/parametersweepsimulations/{simulationId}
                    $method = $data->getMethod();
                    $accept = $data->getHttpAccept();
                    $projectId = $route_array[3];
                    $simulationId = $route_array[5];
                    $param_sweep_sim_resource = new ParameterSweepSimulationResource($method, $accept, $username, $projectId, $simulationId, $request_vars);
                    $response = $param_sweep_sim_resource->exec();
                    RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                }
                
                if (count($route_array) == 7) {
                    if (strcmp($route_array[6], "parameters") == 0) {
                        //echo "DNS3/api/users/{username}/projects/{projectId}/parametersweepsimulations/{simulationId}/parameters/
                        $method = $data->getMethod();
                        $accept = $data->getHttpAccept();
                        $projectId = $route_array[3];
                        $simulationId = $route_array[5];
                        $param_res_list_resource = new ParameterResourceListResource($method, $accept, $username, $projectId, $simulationId, "parameter_sweep");
                        $response = $param_res_list_resource->exec();
                        RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                    } else if (strcmp($route_array[6], "outputfiles") == 0) {
                        //echo "DNS3/api/users/{username}/projects/{projectId}/parametersweepsimulations/{simulationId}/outputfiles/
                        $method = $data->getMethod();
                        $accept = $data->getHttpAccept();
                        $projectId = $route_array[3];
                        $simulationId = $route_array[5];
                        $sim_output_file_list_resource = new SimulationOutputFileListResource($method, $accept, $username, $projectId, $simulationId, "parameter_sweep");
                        $response = $sim_output_file_list_resource->exec();
                        RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                    } else if (strcmp($route_array[6], "results") == 0) {
                        //echo "DNS3/api/users/{username}/projects/{projectId}/parametersweepsimulations/{simulationId}/results/
                        $method = $data->getMethod();
                        $accept = $data->getHttpAccept();
                        $projectId = $route_array[3];
                        $simulationId = $route_array[5];
                        $results_resource = new ResultsResource($method, $accept, $username, $projectId, $simulationId, "parameter_sweep");
                        $response = $results_resource->exec();
                        RestUtils::sendResponse($response->getStatus(), $response->getBody(), $response->getContentType());
                    }
                }
            }
        }
    }
    RestUtils::sendResponse(404, '', 'text/html');
} else {
    RestUtils::sendResponse(404, '', 'text/html');
}
?>