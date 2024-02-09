<?php

/*
 * Enum class for the different types of output files
 */
class OutputFileType{
    
    /**
     * The output file is a tabbed file
     */
    const TABBED_FILE = "TABBED_FILE";
    /**
     * The output file is a trace file
     */
    const TRACE_FILE = "TRACE_FILE";
    /**
     * The output file is a result file
     */
    const RESULT_FILE = "RESULT_FILE";
    
    /**
     * Get the description for an output file
     * @param type $parameter_type
     * @return string 
     */
    public static function getDescription($output_file_type){
        $description = "";
        switch($output_file_type){
            case OutputFileType::TABBED_FILE;
                $description = "Tabla de resultados";
                break;
            case OutputFileType::TRACE_FILE;
                $description = "Traza";
                break;
            case OutputFileType::RESULT_FILE;
                $description = "Resultado";
                break;
        }
        return $description;
    }
}
?>
