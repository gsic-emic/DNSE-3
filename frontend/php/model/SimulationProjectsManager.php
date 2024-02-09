<?php
require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../model/SimulationProject.php");

/**
 * Manager for the simulation projects
 */
class SimulationProjectsManager{
    
    /**
     *
     * @var the base URI to the simulation projects resource in the API
     */
    private $api_sim_projects_uri; 
    
    private $username;
    
    public function __construct($username) {
        global $CONF_PROP;
        $this->api_sim_projects_uri = $CONF_PROP["api_url"] . "users/". $username. "/projects/";
        $this->username = $username;
    }
    
    public function getSimulationProjects($include_removing = false){
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $this->api_sim_projects_uri);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);
        
        //Custom headers
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Accept: application/json; charset=UTF-8'));

        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);

        // perform the HTTP request
        $out = curl_exec($ch);

        // get answer HTTP
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

        // get cURL error code
        $curl_errno = curl_errno($ch);
        
        // free resources
        curl_close($ch);
        
        if (!$curl_errno && ($status == 200 || $status == 204)) {
            $sim_projects = array();
            if ($status == 200){
                $json_result = json_decode($out, true);
                foreach($json_result as $simulationProject){
                    $simProjObj = new SimulationProject($simulationProject); 
                    if (!$simProjObj->isRemoving() || $include_removing){
                        array_push($sim_projects, $simProjObj);
                    }
                }
            }
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $sim_projects
            );
            return $response;
        }else{
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $out
            );
            return $response;
        }
        
    }
    
    public function getSimulationProject($project_id){
        /*$api_sim_project_url = $this->api_sim_projects_uri . $project_id. "/";
        $result = file_get_contents($api_sim_project_url);
        $json_result = json_decode($result, true);
        $sim_project = new SimulationProject($json_result);
        return $sim_project;*/
        
        $api_sim_project_url = $this->api_sim_projects_uri . $project_id. "/";
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $api_sim_project_url);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);
        
        //Custom headers
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Accept: application/json; charset=UTF-8'));

        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);

        // perform the HTTP request
        $out = curl_exec($ch);

        // get answer HTTP
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

        // get cURL error code
        $curl_errno = curl_errno($ch);
        
        // free resources
        curl_close($ch);
        
        if (!$curl_errno && ($status == 200)) {
            $json_result = json_decode($out, true);
            $sim_project = new SimulationProject($json_result);
            //return $sim_project;
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $sim_project
            );
            return $response;
        }else{
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $out
            );
            return $response;
        }
    }
    
    public function updateSimulationProject($project_id, $name, $description) {
        $api_sim_project_url = $this->api_sim_projects_uri . $project_id . "/";
        //$updated = false;

        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "PUT");

        curl_setopt($ch, CURLOPT_URL, $api_sim_project_url);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);

        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);

        $data = array("name" => $name,
                      "description" => $description);
        $data_string = json_encode($data);
        
        curl_setopt($ch, CURLOPT_HTTPHEADER, array(
            'Content-Type: application/json',
            'Content-Length: ' . strlen($data_string))
        );
        curl_setopt($ch, CURLOPT_POSTFIELDS, $data_string);

        // perform the HTTP request
        $out = curl_exec($ch);

        // get answer HTTP
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

        // get cURL error code
        $curl_errno = curl_errno($ch);
        // free resources
        curl_close($ch);
        return $status;
    }
    
    public function createSimulationProject($target_file) {
        $file_full_path = realpath($target_file);
        
        // cURL handler creation
        $ch = curl_init();
        
        //URL
        curl_setopt($ch, CURLOPT_URL, $this->api_sim_projects_uri);
        // HTTP method
        curl_setopt($ch, CURLOPT_POST, TRUE);
        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);
        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
        //set the post fields
        $file = new CurlFile($file_full_path, 'application/zip', 'file.zip');
        //the following line is for PHP versions before 5.5
        //$data = array("file" => '@' . $file_full_path . ';filename=file.zip');
        $data = array("file" => $file);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $data);

        // perform the HTTP request
        $out = curl_exec($ch);

        // get answer HTTP code
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        // get cURL error code
        $curl_errno = curl_errno($ch);
        /*if (!$curl_errno && ($status == 200 || $status == 204)) {
            $updated = true;
        }*/
        // free resources
        curl_close($ch);
        //return $updated;
        return $status;
    }
    
    public function deleteSimulationProject($project_id){
        $api_sim_project_url = $this->api_sim_projects_uri . $project_id . "/";
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "DELETE");

        curl_setopt($ch, CURLOPT_URL, $api_sim_project_url);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);

        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
        
        // perform the HTTP request
        $out = curl_exec($ch);

        // get answer HTTP
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

        // get cURL error code
        $curl_errno = curl_errno($ch);
        /*if (!$curl_errno && ($status == 200 || $status == 204)) {
            $updated = true;
        }*/
        // free resources
        curl_close($ch);
        return $status;
    }

    public function getModelProject($project_id){
        $api_sim_project_url = $this->api_sim_projects_uri . $project_id . "/model";

        $upload_dir = dirname(dirname(dirname(__FILE__))) . DIRECTORY_SEPARATOR . 'api' . DIRECTORY_SEPARATOR. 'tmp' . DIRECTORY_SEPARATOR;
        do {
            $model_path = $upload_dir . $this->username . "_project_" . $project_id . "_model_" . time() . ".zip";
        } while (file_exists($model_path));
        $headers_file_path = substr($model_path, 0, strlen($model_path) - strlen(".zip"));

        $model_file = fopen($model_path, 'w');
        $headers_file = fopen($headers_file_path, 'w');

        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $api_sim_project_url);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);
        
        //Custom headers
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Accept:  application/zip;'));

        // set the handler for delivering answers in strings, instead of being directly printed on page
        //curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
        curl_setopt($ch, CURLOPT_FILE, $model_file);
        curl_setopt($ch, CURLOPT_WRITEHEADER, $headers_file);

        // perform the HTTP request
        $out = curl_exec($ch);
        
        fclose($headers_file);
        if(!curl_errno($ch)) {
          $headers = file_get_contents($headers_file_path);
          if(preg_match('/Content-disposition: .*filename=([^;]+)/', $headers, $matches)){
            $file_name = $matches[1];
          }
          else{
            if(preg_match('/Content-Disposition: .*filename=([^;]+)/', $headers, $matches)){
                $file_name = $matches[1];
            }
            else{
                $file_name = "modelo.zip";
            }
          }
        }

        // get answer HTTP
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

        // get cURL error code
        $curl_errno = curl_errno($ch);
        
        // free resources
        curl_close($ch);
        fclose($model_file);
        
        unlink($headers_file_path);
        
        if (!$curl_errno && ($status == 200)) {
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"   => array("file_path" =>$model_path,
                                  "file_name" =>$file_name)
            );
            return $response;
        }
        else {
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $out
            );
            return $response;
        }

    }
}
?>