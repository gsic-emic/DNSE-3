<?php

require_once(dirname(__FILE__)."/../conf/properties.php");
require_once(dirname(__FILE__)."/../model/SingleSimulation.php");
require_once(dirname(__FILE__)."/../model/SimulationOperation.php");

/**
 * Manager for the single simulations
 */

class SingleSimulationsManager{
     
    /**
     *
     * @var string the base URI to the single simulation resources in the API
     */
    private $api_single_simulations_uri; 
    
    /**
     *
     * @var type string the username 
     */
    private $username;
    
    /**
     *
     * @var type string the project id
     */
    private $projectId;
    
    public function __construct($username, $projectId) {
        global $CONF_PROP;
        $this->api_single_simulations_uri = $CONF_PROP["api_url"] . "users/". $username. "/projects/" . $projectId . "/singlesimulations/";
        $this->username = $username;
        $this->projectId = $projectId;
    }
    
    public function getSingleSimulations(){
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $this->api_single_simulations_uri);

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
            $single_simulations = array();
            if ($status == 200){
                $json_result = json_decode($out, true);
                foreach($json_result as $singleSimulation){
                    $singleSimObj = new SingleSimulation($singleSimulation);      
                    array_push($single_simulations, $singleSimObj);
                }
            }
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $single_simulations
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
    
    public function getSingleSimulation($simulation_id){
        
        $api_single_simulation_url = $this->api_single_simulations_uri . $simulation_id. "/";
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $api_single_simulation_url);

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
            $single_simulation = new SingleSimulation($json_result);
            //return $sim_project;
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"    => $single_simulation
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
    
    public function updateSingleSimulation($simulation_id, $name, $num_repetitions, $priority) {
        $api_single_simulation_url = $this->api_single_simulations_uri . $simulation_id. "/";

        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "PUT");

        curl_setopt($ch, CURLOPT_URL, $api_single_simulation_url);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);

        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);

        $data = array("name" => $name,
                      "numRepetitions" => $num_repetitions,
                      "priority" => $priority);
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
    
    public function updateAllSingleSimulation($simulation_id, $name, $num_repetitions, $priority, $output_files, $parameters) {
        $api_single_simulation_url = $this->api_single_simulations_uri . $simulation_id. "/";

        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "PUT");

        curl_setopt($ch, CURLOPT_URL, $api_single_simulation_url);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);

        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);

        $data = array("name" => $name,
                      "numRepetitions" => $num_repetitions,
                      "priority" => $priority,
                      "outputFiles" => $output_files,
                      "parameters" => $parameters);
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
    
    public function createSingleSimulation($name, $numRepetitions, $priority, $output_files, $parameters) {
        // cURL handler creation
        $ch = curl_init();
        
        //URL
        curl_setopt($ch, CURLOPT_URL, $this->api_single_simulations_uri);
        // HTTP method
        curl_setopt($ch, CURLOPT_POST, TRUE);
        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);
        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
        
        //set the post fields
        $data = array(
            "name" => $name,
            "numRepetitions" => $numRepetitions,
            "priority" => $priority,
            "outputFiles" => $output_files,
            "parameters" => $parameters
        );
        $data_string = json_encode($data);
        curl_setopt($ch, CURLOPT_HTTPHEADER, array(
            'Content-Type: application/json',
            'Content-Length: ' . strlen($data_string))
        );
        curl_setopt($ch, CURLOPT_POSTFIELDS, $data_string);

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
    
    public function deleteSingleSimulation($simulation_id){
        $api_single_simulation_url = $this->api_single_simulations_uri . $simulation_id. "/";
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "DELETE");

        curl_setopt($ch, CURLOPT_URL, $api_single_simulation_url);

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
        
        // free resources
        curl_close($ch);
        return $status;
    }
    
    private function postOperation($simulation_id, $operation){
        $api_single_simulation_url = $this->api_single_simulations_uri . $simulation_id. "/";
        
        // cURL handler creation
        $ch = curl_init();
        
        //URL
        curl_setopt($ch, CURLOPT_URL, $api_single_simulation_url);
        // HTTP method
        curl_setopt($ch, CURLOPT_POST, TRUE);
        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);
        // set the handler for delivering answers in strings, instead of being directly printed on page
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
        
        //set the post fields
        $data = array(
            "operation" => $operation
        );
        $data_string = json_encode($data);
        curl_setopt($ch, CURLOPT_HTTPHEADER, array(
            'Content-Type: application/json',
            'Content-Length: ' . strlen($data_string))
        );
        curl_setopt($ch, CURLOPT_POSTFIELDS, $data_string);

        // perform the HTTP request
        $out = curl_exec($ch);

        // get answer HTTP code
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        // get cURL error code
        $curl_errno = curl_errno($ch);
        // free resources
        curl_close($ch);
        //return $updated;
        return $status;
    }
    
    public function startSingleSimulation($simulation_id){
        return $this->postOperation($simulation_id, SimulationOperation::START);
    }
    
    public function pauseSingleSimulation($simulation_id){
        return $this->postOperation($simulation_id, SimulationOperation::PAUSE);
    }
    
    public function stopSingleSimulation($simulation_id){
        return $this->postOperation($simulation_id, SimulationOperation::STOP);     
    }
    
    public function getSingleSimulationResults($simulation_id){
        $api_single_simulation_results_url = $this->api_single_simulations_uri . $simulation_id . "/results";
        //the directory where the tmp files are created
        $upload_dir = dirname(dirname(dirname(__FILE__))) . DIRECTORY_SEPARATOR . 'api' . DIRECTORY_SEPARATOR. 'tmp' . DIRECTORY_SEPARATOR;
        do {
            $results_file_path = $upload_dir . $this->username . "_single_" . $simulation_id . "_results_" . time() . ".zip";
        } while (file_exists($results_file_path));
        $headers_file_path = substr($results_file_path, 0, strlen($results_file_path)- strlen(".zip"));
        
        
        $results_file = fopen($results_file_path, 'w');
        $headers_file = fopen($headers_file_path, 'w');
        
        // cURL handler creation
        $ch = curl_init();

        // HTTP method
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, "GET");

        curl_setopt($ch, CURLOPT_URL, $api_single_simulation_results_url);

        // set the timeout limit
        curl_setopt($ch, CURLOPT_TIMEOUT, 30);
        
        //Custom headers
        curl_setopt($ch, CURLOPT_HTTPHEADER, array('Accept:  application/zip;'));

        // set the handler for delivering answers in strings, instead of being directly printed on page
        //curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
        curl_setopt($ch, CURLOPT_FILE, $results_file);
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
                $file_name = "resultados.zip";
            }
          }
        }

        // get answer HTTP
        $status = curl_getinfo($ch, CURLINFO_HTTP_CODE);

        // get cURL error code
        $curl_errno = curl_errno($ch);
        
        // free resources
        curl_close($ch);
        fclose($results_file);
        
        unlink($headers_file_path);
        
        if (!$curl_errno && ($status == 200)) {
            $response = array(
                "status" => $status,
                "errno"  => $curl_errno,
                "data"   => array("file_path" =>$results_file_path,
                                  "file_name" =>$file_name)
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
}
?>


