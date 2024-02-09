<?php

require_once(dirname(__FILE__)."/../../php/model/SimulationProjectsManager.php");
/**
 * Description of SimulationProjectResource
 */
class SimulationProjectListResource {
    
    private $method;
    private $accept;
    
    private $upload_dir;
    private $max_file_size;
    private $allowed_extensions;

    function __construct($method, $accept, $username, $request_vars = null) {
        $this->method = $method;
        $this->accept = $accept;
        
        $this->username = $username;
        $this->request_vars = $request_vars;
        $this->upload_dir = dirname(dirname(__FILE__)) . DIRECTORY_SEPARATOR . 'tmp' . DIRECTORY_SEPARATOR;
        $this->max_file_size = 5*1024*1024;//5MB
        $this->allowed_extensions =  array('zip');
    }

    /**
     * Call for the suitable method according to the REST request
     * @return ResponseData The response to the REST request
     */
    function exec() {
        if (strcmp($this->method, "get") == 0) {
            if (strpos($this->accept, 'application/json') !== false) {
                return $this->getJson();
            }
        }
        if (strcmp($this->method, "post") == 0){
            return $this->post();
        }
        $response = new ResponseData(501, '', 'text/html');
        return $response;
    }

    /**
     * GET request to get the list of simulation projects for the user as a JSON
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function getJson() {
        $sim_proj_controller = new SimulationProjectsManager($this->username);
        $response = $sim_proj_controller->getSimulationProjects();
        $status = $response["status"];
        if ($status == 200 || $status == 204){
            $sim_projects = $response["data"];
            $sim_proj_props = array();
            foreach ($sim_projects as $sim_project){
                $properties = $sim_project->getProperties();
                array_push($sim_proj_props, $properties);
            }
            $json_data = $this->generateJson($sim_proj_props);
            return new ResponseData(200, $json_data, 'application/json');
        }else{
            return new ResponseData($status, '', 'text/html');
        }
    }
    
    /**
     * POST request to create a new simulation project
     * @return ResponseData The response containing the status code. If a error is produced, the response includes the reason of the error
     */
    function post(){
        if (isset($_FILES["projectFile"])){
            $name_file = basename($_FILES['projectFile']['name']);
            do{
                $target_file = $this->upload_dir . $this->username . "_" . time() . "_" . $name_file;
            }while(file_exists($target_file));
            // Check file extension
            $extension = pathinfo($target_file, PATHINFO_EXTENSION);
            if(!in_array($extension, $this->allowed_extensions) ) {
                //return new ResponseData(400, "The file has a no allowed extension", 'text/html');
                unlink($target_file);
                return new ResponseData(400, "El fichero tiene una extensión no permitida", 'text/html');
            }
            // Check file size (in bytes)
            if ($_FILES["projectFile"]["size"] > $this->max_file_size) {
                unlink($target_file);
                //return new ResponseData(400, "The file is too large (max size " . $this->max_file_size/1024/1024 . 'MB)', 'text/html');
                return new ResponseData(400, "El fichero es muy grande (tamaño máximo " . $this->max_file_size/1024/1024 . 'MB)', 'text/html');
            }
            if (move_uploaded_file($_FILES['projectFile']['tmp_name'], $target_file) == false) {
                unlink($target_file);
                //return new ResponseData(500, "There was an error while trying to save the file", 'text/html');
                return new ResponseData(500, "Error al guardar el fichero en el servidor", 'text/html');
            }
            $sim_proj_controller = new SimulationProjectsManager($this->username);
            $status = $sim_proj_controller->createSimulationProject($target_file);
            unlink($target_file);
            if ($status == 201){
                return new ResponseData(201, '', 'text/html');
            }else{
                if ($status == 100){
                    return new ResponseData(100, '', 'text/html');
                }else{
                    return new ResponseData(500, "Error al crear el proyecto de simulación. Comprueba que es un fichero válido", 'text/html');
                    //return new ResponseData(500, "There was an error while trying to create the simulation project", 'text/html');
                }
            } 
            //delete the file
            //unlink($target_file);
            return new ResponseData(201, '', 'text/html');
        }else{
            //return new ResponseData(400, "The file has not been provided", 'text/html');
            return new ResponseData(400, "No se ha proporcionado un fichero", 'text/html');
        }
    }
    
    /**
     * Convert the data to a JSON
     * @param type $data the content to convert to JSON
     * @return type The content as an JSON of false if there is an error
     */
    private function generateJson($data) {
        $json_data = json_encode($data);
        return $json_data;
    }
}

?>
