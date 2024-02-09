package dnse3.orchestration.controller;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.entity.ContentType;

import dnse3.common.CloudManager;
import dnse3.orchestration.auxiliar.ParameterMapper;
import dnse3.orchestration.auxiliar.project.Filestructure;
import dnse3.orchestration.auxiliar.project.Integertype;
import dnse3.orchestration.auxiliar.project.Rationaltype;
import dnse3.orchestration.auxiliar.project.Resultfile;
import dnse3.orchestration.auxiliar.project.Seedtype;
import dnse3.orchestration.auxiliar.project.Simulationpackage;
import dnse3.orchestration.auxiliar.project.Stringtype;
import dnse3.orchestration.auxiliar.project.Tabbedfile;
import dnse3.orchestration.jpa.controller.JpaController;
import dnse3.orchestration.jpa.model.project.OutputFile;
import dnse3.orchestration.jpa.model.project.OutputFileStructure;
import dnse3.orchestration.jpa.model.project.OutputFileType;
import dnse3.orchestration.jpa.model.project.ParameterDescription;
import dnse3.orchestration.jpa.model.project.ParameterTypeEnum;
import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.jpa.model.simulation.Parameter;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.Simulation;
import dnse3.orchestration.jpa.model.simulation.SimulationStatus;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;
import dnse3.orchestration.jpa.model.user.User;

public class DataController {

    private JpaController jpaController;
    private ExecutionController executionController;
    
    //Eliminación de almacenamiento
    private LinkedList<Long> simulationsToClean;
    private LinkedList<Integer> projectsToClean; //Para eliminar proyectos completos...
    private ArrayList<Long> parameterSweepsToClean;
    private ArrayList<Long> simulationsRemainingToClean; //Comprobación cruzada
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();
    private ExecutorService removingPool;
    
    private HashMap<Integer, ArrayList<Long>> projectsAndSimulationsRemoving;
    private ArrayList<Integer> projectsRemainingToClean;

    public DataController(JpaController jpaController) {
        this.jpaController = jpaController;
        
        this.simulationsToClean = new LinkedList<>();
        this.projectsToClean = new LinkedList<>();
        this.parameterSweepsToClean = new ArrayList<>();
        this.simulationsRemainingToClean =  new ArrayList<>();
        this.removingPool = Executors.newFixedThreadPool(4); //De momento fijo en 4 los hilos de eliminación, se puede poner variable
        for(int i = 0; i<4; i++){
            FileRemoval removal = new FileRemoval(this);
            removingPool.execute(removal);
        }
        
        this.projectsAndSimulationsRemoving = new HashMap<>();
        this.projectsRemainingToClean = new ArrayList<>();
    }
    
    public void setExecutionController(ExecutionController executionController){
        this.executionController = executionController;
    }

    /**
     * Pasarela para comprobar si un usuario existe en el sistema
     * @param username Idententificador del usuario en el sistema
     */
    public boolean hasUser(String username) {
        try {
            jpaController.getUserController().getUser(username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Pasarela para crear a un usuario en el sistema
     * @param username Idententificador del usuario en el sistema
     * @throws Exception Se lanza una excepción si el usuario ya exista en el sistema
     */
    public void createUser(String username) throws Exception {
        User user = new User(username);
        jpaController.getUserController().createUser(user);
    }

    /**
     * Pasarela para crear a un usuario en el sistema
     * @param username Idententificador del usuario en el sistema
     * @param maxSimulations Número máximo de simulaciones que podrá lanzar el usuario
     * @throws Exception Se lanza una excepción si el usuario ya exista en el sistema
     */
    public void createUser(String username, int maxSimulations) throws Exception {
        User user = new User(username, maxSimulations);
        jpaController.getUserController().createUser(user);
    }

    /**
     * Pasarela para modificar un usuario del sistema
     * @param username Idententificador del usuario en el sistema
     * @param maxSimulations Número máximo de simulaciones que podrá lanzar el usuario
     * @throws Exception Se lanza una excepción si el usuario no existe en el sistema
     */
    public void updateUser(String username, int maxSimulations) throws Exception {
        User user = jpaController.getUserController().getUser(username);
        user.setSimulationLimit(maxSimulations);
        jpaController.getUserController().updateUser(user);
    }
    
    /**
     * Pasarela para eliminar un usuario del sistema
     * @param username Idententificador del usuario en el sistema
     * @throws Exception Se lanza una excepción si el usuario no existe en el sistema
     */
    public void deleteUser(String username) throws Exception{
        //Eliminamos primero los proyectos asociados al usuario
        List<Project> projects = jpaController.getProjectController().getProjects(username);
        if(!projects.isEmpty()){
            for(Project p : projects){
                for(ParameterSweepSimulation sweep : jpaController.getParameterSweepSimulationController().getParameterSweepSimulations(p.getId())){
                    jpaController.getParameterSweepSimulationController().deleteParameterSweepSimulation(sweep);
                }
                for(SingleSimulation single : jpaController.getSingleSimulationController().getSingleSimulations(p.getId())){
                    jpaController.getSingleSimulationController().deleteSingleSimulation(single);
                }
                jpaController.getProjectController().deleteProject(p);
            }
        }
        projects = null;
        
        //Se elimina al usuario
        User user = jpaController.getUserController().getUser(username);
        jpaController.getUserController().deleteUser(user);
    }

    /**
     * Pasarela para crear el proyecto de simulación
     * @param simulationpackage Valores de los parámetros a ejecutar
     * @param path Ruta temporal donde se encuentra tanto el ZIP como el ejecutable
     * @param packageFile Fichero ZIP
     * @param username Identificador del usuario en el sistema
     * @return Identificador del proyecto del usuario
     * @throws Exception Se puede lanzar una excepción cuando no se ha conseguido pasar la 
     * verificación del paquete de simulación o cuando no se consigue almacenar los 
     * resultados en el Servicio de Almacenamiento.
     */
    public int createProject(Simulationpackage simulationpackage, File path, File packageFile, String username) throws Exception{
        if(!simulationpackage.verify())
            throw new Exception("No se ha conseguido pasar la verificación.");
        
        //Compilación del código fuente
        Files.copy(new File("Makefile").toPath(), new File(path,"Makefile").toPath());

        ProcessBuilder pb = new ProcessBuilder("make");
        pb.directory(path);
        System.out.println("Compilation start");
        pb.redirectErrorStream(true);
        pb.inheritIO();
        Process simulator = pb.start();

        int status = simulator.waitFor(); // Wait until it has compiled
        if(status != 0)
            throw new Exception();
        
        File executable = new File(path,"sim");
        executable.createNewFile(); //Placeholder
        
        //Creación del proyecto
        Project project = new Project(simulationpackage.getDescription().getTitle(), simulationpackage.getDescription().getTextualdescription());
        jpaController.getProjectController().createProject(project, username);
        System.out.println("Ruta del ejecutable: "+executable.getPath());
        String sourceURI = CloudManager.uploadFile("users/"+username+"/"+project.getId(), executable, ContentType.create("application/x-executable"));
        System.out.println("Ruta del zip: " + packageFile.getPath());
        String packageURI = CloudManager.uploadFile("users/"+username+"/"+project.getId(), packageFile, ContentType.create("application/zip"));
        if(sourceURI==null || packageURI==null){ //No se han podido cargar los ficheros
            CloudManager.deletePath("users/"+username+"/"+project.getId()+"/");
            jpaController.getProjectController().deleteProject(project);
            throw new Exception("No se ha conseguido almacenar el ejecutable o el fichero ZIP");
        }
        project.setSourceURI(sourceURI);
        project.setPackageURI(packageURI);
        
        //Creación de los parámetros
        for(Stringtype parameter : simulationpackage.getParametertypes().getStringtype()){
        	System.out.println(parameter.getDefaultvalue() + " " + parameter.getPossiblevalue());
            ParameterDescription parameterDescription = new ParameterDescription(parameter.getId(), ParameterTypeEnum.STRING_VALUE);
            parameterDescription.setDefaultValue(parameter.getDefaultvalue());
            parameterDescription.setValues(parameter.getPossiblevalue());
            project.addParameterDescription(parameterDescription);
            jpaController.getParameterDescriptionController().createParameterDescription(parameterDescription, project.getId());
        }
        
        for(Integertype parameter : simulationpackage.getParametertypes().getIntegertype()){
            ParameterDescription parameterDescription = new ParameterDescription(parameter.getId(), ParameterTypeEnum.INTEGER_VALUE);
            parameterDescription.setDefaultValue(parameter.getDefaultvalue().toString());
            if(parameter.getGreaterthan()!=null)
                parameterDescription.setGreater(parameter.getGreaterthan().toString());
            else if(parameter.getGreaterthanorequalto()!=null)
                parameterDescription.setGreaterEqual(parameter.getGreaterthanorequalto().toString());
            if(parameter.getLessthan()!=null)
                parameterDescription.setLess(parameter.getLessthan().toString());
            else if(parameter.getLessthanorequalto()!=null)
                parameterDescription.setLessEqual(parameter.getLessthanorequalto().toString());
            project.addParameterDescription(parameterDescription);
            jpaController.getParameterDescriptionController().createParameterDescription(parameterDescription, project.getId());
        }
        
        for(Rationaltype parameter : simulationpackage.getParametertypes().getRationaltype()){
            ParameterDescription parameterDescription = new ParameterDescription(parameter.getId(), ParameterTypeEnum.RATIONAL_VALUE);
            parameterDescription.setDefaultValue(parameter.getDefaultvalue().toString());
            if(parameter.getGreaterthan()!=null)
                parameterDescription.setGreater(parameter.getGreaterthan().toString());
            else if(parameter.getGreaterthanorequalto()!=null)
                parameterDescription.setGreaterEqual(parameter.getGreaterthanorequalto().toString());
            if(parameter.getLessthan()!=null)
                parameterDescription.setLess(parameter.getLessthan().toString());
            else if(parameter.getLessthanorequalto()!=null)
                parameterDescription.setLessEqual(parameter.getLessthanorequalto().toString());
            project.addParameterDescription(parameterDescription);
            jpaController.getParameterDescriptionController().createParameterDescription(parameterDescription, project.getId());
        }
        
        for(Seedtype parameter : simulationpackage.getParametertypes().getSeedtype()){
            ParameterDescription parameterDescription = new ParameterDescription(parameter.getId(), ParameterTypeEnum.SEED);
            project.addParameterDescription(parameterDescription);
            jpaController.getParameterDescriptionController().createParameterDescription(parameterDescription, project.getId());
        }
        
        //Creación de las estructuras de ficheros
        HashMap<String, OutputFileStructure> outputFileStructures = new HashMap<>();
        for(Filestructure fileStructure : simulationpackage.getOutputfilestructures().getFilestructure()){
            OutputFileStructure outputFileStructure = new OutputFileStructure(fileStructure.getId(), fileStructure.getOutputvar(), fileStructure.isIsmultilinefile());
            project.addOutputFileStructure(outputFileStructure);
            outputFileStructures.put(outputFileStructure.getName(), outputFileStructure);
            jpaController.getOutputFileStructureController().createOutputFileStructure(outputFileStructure, project.getId());
        }
        
        for(Resultfile file : simulationpackage.getOutputfiles().getResultfile()){
            OutputFile outputFile = new OutputFile(file.getValue(), outputFileStructures.get(file.getFilestructureref().getId()), OutputFileType.RESULT_FILE);
            project.addOutputFile(outputFile);
            jpaController.getOutputFileController().createOutputFile(outputFile, file.getFilestructureref().getId(), project.getId());
        }
        
        for(Tabbedfile file : simulationpackage.getOutputfiles().getTabbedfile()){
            OutputFile outputFile = new OutputFile(file.getValue(), outputFileStructures.get(file.getFilestructureref().getId()), OutputFileType.TABBED_FILE);
            project.addOutputFile(outputFile);
            jpaController.getOutputFileController().createOutputFile(outputFile, file.getFilestructureref().getId(), project.getId());
        }
        
        for(String file : simulationpackage.getOutputfiles().getTracefile()){
            OutputFile outputFile = new OutputFile(file, OutputFileType.TRACE_FILE);
            project.addOutputFile(outputFile);
            jpaController.getOutputFileController().createOutputFile(outputFile, project.getId());
        }
        
        jpaController.getProjectController().updateProject(project);
        
        return project.getId();
    }
    
    /**
     * Pasarela de actualización de un proyecto
     * @param name Nombre del proyecto en el sistema
     * @param description Descripción del proyecto en el sistema
     * @param projectId Identificador del proyecto en el sistema
     * @throws Exception Puede lanzar una excepción cuando el proyecto no existe en el sistema 
     */
    public void updateProject(String name, String description, int projectId) throws Exception{
        Project project = jpaController.getProjectController().getProject(projectId);
        if(name!=null)
            project.setName(name);
        if(description!=null)
            project.setDescription(description);
        jpaController.getProjectController().updateProject(project);
    }

    /**
     * Método para comprobar si un proyecto pertenece a un usuario
     * @param projectId Identificador del proyecto en el sistema
     * @param username Identificador del usuario en el sistema
     * @return Devolverá verdadero si el proyecto pertenece al usuario o falso en el caso contrario
     */
    public boolean hasProject(int projectId, String username) {
        try{
            Project project = jpaController.getProjectController().getProject(projectId);
            return project.getUser().getUsername().equals(username);
        }catch(Exception e){
            return false;
        }
    }

    /**
     * Método para determinar si existe un fichero de salida de un proyecto específico
     * @param outputFileName Identificador del fichero de salida
     * @param projectId Identificador del proyecto
     * @return Devolverá true si el fichero de salida existe en el proyecto o false ni no existe
     */
    public boolean hasOutputFile(String outputFileName, int projectId) {
        try{
            jpaController.getOutputFileController().getOutputFile(outputFileName, projectId);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    /**
     * Comprueba la existencia dentro de un proyecto de una simulación específica
     * @param simulationId Identificador de la simulación
     * @param projectId Identificador del proyecto
     * @param type Tipo de proyecto
     * @return Devuelve verdadero si la simulación pertenece al proyecto o falso si no le pertenece.
     */
    public boolean hasSimulation(long simulationId, int projectId, SimulationTypeEnum type){
        try{
            switch(type){
            case SINGLE_SIMULATION:
            {
                SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
                return simulation.getProject().getId() == projectId;
            }
            case PARAMETER_SWEEP_SIMULATION:
            {
                ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
                return simulation.getProject().getId() == projectId;
            }
            default:
                return false;
            }
        }
        catch(Exception e){
            return false;
        }
    }
    
    /**
     * Método para comprobar la existencia de un fichero de salida asignado a una simulación, que a su vez está asignada a un proyecto.
     * @param outputFileName Identificador del fichero de salida
     * @param simulationId Identificador de la simulación
     * @param projectId Identificador del proyecto
     * @param type Tipo de simulación
     * @return Devuelve verdadero si el fichero de salida existe en la simulación que pertenece al proyecto indicado
     */
    public boolean hasOutputFile(String outputFileName, long simulationId, int projectId, SimulationTypeEnum type){
        try{
            OutputFile outputFile = jpaController.getOutputFileController().getOutputFile(outputFileName, projectId);
            Simulation simulation = null;
            switch(type){
            case SINGLE_SIMULATION:
                simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
                break;
            case PARAMETER_SWEEP_SIMULATION:
                simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
                break;
            default:
                return false;
            }
            return simulation.getOutputFiles().contains(outputFile);
        }
        catch (Exception e){
            return false;
        }
    }
    
    /**
     * Método para agregar un fichero de salida a una simulación
     * @param outputFileName Identificador del fichero de salida
     * @param simulationId Identificador de la simulación
     * @param projectId Identificador del proyecto
     * @param type Tipo de simulación
     * @return Devuelve verdadero si se consigue añadir el fichero de salida
     * @throws Exception
     */
    public boolean addOutputFile(String outputFileName, long simulationId, int projectId, SimulationTypeEnum type) throws Exception{
        OutputFile outputFile = jpaController.getOutputFileController().getOutputFile(outputFileName, projectId);
        switch(type){
        case SINGLE_SIMULATION:
        {
            SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
            if(simulation.getOutputFiles().contains(outputFile))
                return false;
            simulation.addOutputFile(outputFile);
            jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
            return true;
        }
        case PARAMETER_SWEEP_SIMULATION:
        {
            ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
            if(simulation.getOutputFiles().contains(outputFile))
                return false;
            simulation.addOutputFile(outputFile);
            jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
            return true;
        }
        default:
            throw new Exception("Tipo de simulación no soportada");
        }
    }
    
    /**
     * Método para eliminar un fichero de salida
     * @param outputFileName Identificador del fichero de salida a eliminar
     * @param simulationId Identificador de la simulación
     * @param projectId Identificador del proyecto
     * @param type Tipo de simulación
     * @return Verdadero cuando se consiga eliminar la simulación. Falso en el caso contrario
     * @throws Exception
     */
    public boolean removeOutputFile(String outputFileName, long simulationId, int projectId, SimulationTypeEnum type) throws Exception{
        OutputFile outputFile = jpaController.getOutputFileController().getOutputFile(outputFileName, projectId);
        switch(type){
        case SINGLE_SIMULATION:
        {
            SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
            if(simulation.getOutputFiles().size()==1)
                return false;
            if(simulation.getOutputFiles().contains(outputFile)){
                simulation.getOutputFiles().remove(outputFile);
                jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
            }
            break;
        }
        case PARAMETER_SWEEP_SIMULATION:
        {
            ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
            if(simulation.getOutputFiles().size()==1)
                return false;
            if(simulation.getOutputFiles().contains(outputFile)){
                simulation.getOutputFiles().remove(outputFile);
                jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
            }
            break;
        }
        default:
            throw new Exception("Tipo de simulación no soportada");
        }
        return true;
    }

    /**
     * Método para crear una simulacion de tipo individual
     * @param simulationName Idenfitificación de la simulación
     * @param numRepetitions Número de repeticiones que se va a realizar la simulación
     * @param priority Prioridad del grupo de la simulación
     * @param outputFiles Ficheros de resultados
     * @param parameters Parámetros que intervienen en la simulación
     * @param projectId Identificador del proyecto al que va asignada la simulación
     * @return Identificador de la simulación
     * @throws Exception
     */
    public long createSingleSimulation(String simulationName, int numRepetitions, int priority, ArrayList<String> outputFiles,HashMap<String, ParameterMapper> parameters, int projectId) throws Exception{
        
        SingleSimulation simulation = new SingleSimulation(simulationName, numRepetitions, priority);
        
        //Comprobación de los outputFiles
        
        for(String outputFileName : outputFiles){
            OutputFile outputFile = jpaController.getOutputFileController().getOutputFile(outputFileName, projectId);
            if(numRepetitions>1)
                if(!(outputFile.getType().equals(OutputFileType.RESULT_FILE) || (outputFile.getType().equals(OutputFileType.TABBED_FILE) && !outputFile.getOutputFileStructure().isMultiLine())))
                    throw new Exception();
            simulation.addOutputFile(outputFile);
        }
        
        for(Entry<String, ParameterMapper> entry : parameters.entrySet()){
            ParameterDescription parameterDescription = jpaController.getParameterDescriptionController().getParameterDescription(entry.getKey(), projectId);
            if(entry.getValue().isRandom()){
                Parameter parameter = new Parameter(true);
                parameter.setParameterDescription(parameterDescription);
                simulation.addParameter(parameter);
            }
            else if(!entry.getValue().getValues().isEmpty()){
                if(!parameterDescription.isValid(entry.getValue().getValues().get(0))){
                    throw new Exception();
                }
                Parameter parameter = new Parameter(entry.getValue().getValues());
                parameter.setParameterDescription(parameterDescription);
                simulation.addParameter(parameter);
            }
            else{
                Parameter parameter = new Parameter();
                parameter.setParameterDescription(parameterDescription);
                ArrayList<String> values = new ArrayList<>();
                values.add(parameterDescription.getDefaultValue());
                parameter.setValues(values);
                simulation.addParameter(parameter);
            }
        }
        
        jpaController.getSingleSimulationController().createSingleSimulation(simulation, projectId);
        
        return simulation.getId();
    }
    
    /**
     * Método para agregar una simulación de barrido
     * @param simulationName Identificador de la simulación
     * @param numRepetitions Número de repeticiones
     * @param priority Prioridad de las tareas de la simulación
     * @param outputFiles Ficheros de salida
     * @param parameters Parámetros
     * @param projectId Identificador del proyecto
     * @return Identificador de la simulación
     * @throws Exception
     */
    public long createParameterSweepSimulation(String simulationName, int numRepetitions, int priority, ArrayList<String> outputFiles,HashMap<String, ParameterMapper> parameters, int projectId) throws Exception{
        ParameterSweepSimulation simulation = new ParameterSweepSimulation(simulationName, numRepetitions, priority);
        
        //Comprobación de los outputFiles
        
        for(String outputFileName : outputFiles){
            OutputFile outputFile = jpaController.getOutputFileController().getOutputFile(outputFileName, projectId);
            if(!(outputFile.getType().equals(OutputFileType.RESULT_FILE) || (outputFile.getType().equals(OutputFileType.TABBED_FILE) && !outputFile.getOutputFileStructure().isMultiLine()))) //Solo pueden ser ficheros de resultados
                throw new Exception();
            simulation.addOutputFile(outputFile);
        }
        
        for(Entry<String, ParameterMapper> entry : parameters.entrySet()){
            ParameterDescription parameterDescription = jpaController.getParameterDescriptionController().getParameterDescription(entry.getKey(), projectId);
            if(entry.getValue().isRandom()){
                Parameter parameter = new Parameter(true);
                parameter.setParameterDescription(parameterDescription);
                simulation.addParameter(parameter);
            }
            else if(!entry.getValue().getValues().isEmpty()){
                if(!parameterDescription.isValid(entry.getValue().getValues())){
                    throw new Exception();
                }
                Parameter parameter = new Parameter(entry.getValue().getValues());
                parameter.setParameterDescription(parameterDescription);
                simulation.addParameter(parameter);
            }
            else if(entry.getValue().getMinValue()!=null && entry.getValue().getMaxValue()!=null && entry.getValue().getStep()!=null){
                if(!parameterDescription.isValid(entry.getValue().getMinValue(), entry.getValue().getMaxValue())){
                    throw new Exception();
                }
                Parameter parameter = new Parameter(entry.getValue().getMinValue(), entry.getValue().getMaxValue(), entry.getValue().getStep());
                parameter.setParameterDescription(parameterDescription);
                simulation.addParameter(parameter);
            }
            else{
                Parameter parameter = new Parameter();
                parameter.setParameterDescription(parameterDescription);
                ArrayList<String> values = new ArrayList<>();
                values.add(parameterDescription.getDefaultValue());
                parameter.setValues(values);
                simulation.addParameter(parameter);
            }
        }
        
        jpaController.getParameterSweepSimulationController().createParameterSweepSimulation(simulation, projectId);
        
        return simulation.getId();
    }

    /**
     * Método para averiguar si en un proyecto existe un parámetro específico
     * @param parameterName Nombre del parámetro
     * @param projectId Identificador del proyecto en el sistema
     * @return Devuelve verdadero si el parámetro existe en el proyecto o falso si no existe
     */
    public boolean hasParameterDescription(String parameterName, int projectId) {
        try{
            jpaController.getParameterDescriptionController().getParameterDescription(parameterName, projectId);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    public boolean hasOutputFileStructure(String outputFileStructureName, int projectId) {
        // TODO Auto-generated method stub
        try{
            jpaController.getOutputFileStructureController().getOutputFileStructure(outputFileStructureName, projectId);
            return true;
        }
        catch(Exception e){
            return false;
        }
    }

    /**
     * Método para agregar un parámetro a una simulación
     * @param parameterMapper Estructura del parámetro a agregar
     * @param projectId Identificador del proyecto
     * @param simulationId Identificador de la simulación
     * @param type Tipo de simulación (individual o de barrido de parámetros)
     * @return Verdadero si el parámetro se ha actualizado. Falso si se ha creado
     * @throws Exception
     */
    public boolean updateParameter(ParameterMapper parameterMapper, int projectId, long simulationId, SimulationTypeEnum type) throws Exception{
        Parameter parameter = null;
        try{ //Consigo recuperar el parámetro, luego actualizo
            parameter = jpaController.getParameterController().getParameter(parameterMapper.getParameterName(), projectId, simulationId);
        }
        catch(Exception e){ //No lo consigo recuperar, luego creo
            ParameterDescription parameterDescription = jpaController.getParameterDescriptionController().getParameterDescription(parameterMapper.getParameterName(), projectId);
            switch(type){
            case SINGLE_SIMULATION:
                if(parameterMapper.isRandom()){
                    parameter = new Parameter(true);
                    jpaController.getParameterController().createParameter(parameter, parameterMapper.getParameterName(), projectId, simulationId);
                    return false;
                }
                else if(!parameterMapper.getValues().isEmpty()){
                    if(parameterMapper.getValues().size()>1)
                        throw new Exception("Simulación de tipo individual. No puede tener más de un valor");
                    if(!parameterDescription.isValid(parameterMapper.getValues().get(0)))
                        throw new Exception("El valor del parámetro introducido no es válido");
                    parameter = new Parameter(parameterMapper.getValues().get(0));
                    jpaController.getParameterController().createParameter(parameter, parameterMapper.getParameterName(), projectId, simulationId);
                    return false;
                }
                else{ //Parámetro por defecto
                    parameter = new Parameter(parameterDescription.getDefaultValue());
                    jpaController.getParameterController().createParameter(parameter, parameterMapper.getParameterName(), projectId, simulationId);
                    return false;
                }
            case PARAMETER_SWEEP_SIMULATION:
                if(parameterMapper.isRandom()){
                    parameter = new Parameter(true);
                    jpaController.getParameterController().createParameter(parameter, parameterMapper.getParameterName(), projectId, simulationId);
                    return false;
                }
                else if(!parameterMapper.getValues().isEmpty()){
                    if(!parameterDescription.isValid(parameterMapper.getValues())){
                        throw new Exception("El valor de los parámetros no es válido");
                    }
                    parameter = new Parameter(parameterMapper.getValues());
                    jpaController.getParameterController().createParameter(parameter, parameterMapper.getParameterName(), projectId, simulationId);
                    return false;
                }
                else if(parameterMapper.getMinValue()!=null && parameterMapper.getMaxValue()!=null && parameterMapper.getStep()!=null){
                    if(!parameterDescription.isValid(parameterMapper.getMinValue(), parameterMapper.getMaxValue())){
                        throw new Exception("El formato de los valores máximo y mínimo no son válidos");
                    }
                    parameter = new Parameter(parameterMapper.getMinValue(), parameterMapper.getMaxValue(), parameterMapper.getStep());
                    jpaController.getParameterController().createParameter(parameter, parameterMapper.getParameterName(), projectId, simulationId);
                    return false;
                }
                else{
                    parameter = new Parameter(parameterDescription.getDefaultValue());
                    jpaController.getParameterController().createParameter(parameter, parameterMapper.getParameterName(), projectId, simulationId);
                    return false;
                }
            default:
                throw new Exception("Tipo de simulación no registrada");
            }
        }
        
        //Se actualiza el parámetro
        switch(type){
        case SINGLE_SIMULATION:
            if(parameterMapper.isRandom()){
                if(parameter.isRandom())
                    return true;
                parameter.getValues().clear(); //Si es individual, solo tiene valores
                parameter.setRandom(true);
                jpaController.getParameterController().updateParameter(parameter);
                return true;
            }
            else if(!parameterMapper.getValues().isEmpty()){
                if(parameterMapper.getValues().size()>1)
                    throw new Exception("Simulación de tipo individual. No puede tener más de un valor");
                if(!parameter.getParameterDescription().isValid(parameterMapper.getValues().get(0)))
                    throw new Exception("El valor del parámetro introducido no es válido");
                parameter.getValues().clear();
                parameter.getValues().add(parameterMapper.getValues().get(0));
                parameter.setRandom(false);
                jpaController.getParameterController().updateParameter(parameter);
                return true;				
            }
            else{ //Parámetro por defecto
                parameter.getValues().clear();
                parameter.getValues().add(parameter.getParameterDescription().getDefaultValue());
                parameter.setRandom(false);
                jpaController.getParameterController().updateParameter(parameter);
                return true;
            }
        case PARAMETER_SWEEP_SIMULATION:
            if(parameterMapper.isRandom()){
                if(parameter.isRandom())
                    return true;
                parameter.getValues().clear();
                parameter.setMinValue(null);
                parameter.setMaxValue(null);
                parameter.setStep(null);
                parameter.setRandom(true);
                jpaController.getParameterController().updateParameter(parameter);
                return true;
            }
            else if(!parameterMapper.getValues().isEmpty()){
                if(!parameter.getParameterDescription().isValid(parameterMapper.getValues())){
                    throw new Exception("El valor de los parámetros no es válido");
                }
                parameter.getValues().clear();
                parameter.getValues().addAll(parameterMapper.getValues());
                parameter.setMinValue(null);
                parameter.setMaxValue(null);
                parameter.setStep(null);
                parameter.setRandom(false);
                jpaController.getParameterController().updateParameter(parameter);
                return true;
            }
            else if(parameterMapper.getMinValue()!=null && parameterMapper.getMaxValue()!=null && parameterMapper.getStep()!=null){
                if(!parameter.getParameterDescription().isValid(parameterMapper.getMinValue(), parameterMapper.getMaxValue())){
                    throw new Exception("El valor de los parámetros no es válido (min, max)");
                }
                parameter.getValues().clear();
                parameter.setMinValue(parameterMapper.getMinValue());
                parameter.setMaxValue(parameterMapper.getMaxValue());
                parameter.setStep(parameterMapper.getStep());
                parameter.setRandom(false);
                jpaController.getParameterController().updateParameter(parameter);
                return true;
            }
            else{
                parameter.getValues().clear();
                parameter.getValues().add(parameter.getParameterDescription().getDefaultValue());
                parameter.setMinValue(null);
                parameter.setMaxValue(null);
                parameter.setStep(null);
                parameter.setRandom(false);
                jpaController.getParameterController().updateParameter(parameter);
                return true;
            }
        default:
            throw new Exception("Tipo de simulación no registrado");
        }
    }

    /**
     * Método que tiene como objetivo eliminar un parámetro
     * @param parameterName Identificador del parámetro
     * @param projectId Identificador del proyecto
     * @param simulationId Identificador del proyecto
     * @throws Exception 
     */
    public void removeParameter(String parameterName, int projectId, long simulationId) throws Exception{
        jpaController.getParameterController().deleteParameter(parameterName, projectId, simulationId);
        
    }

    /**
     * Método para actualizar una simulación de tipo individual
     * @param simulationId Identificador de la simulación
     * @param simulationName Nombre de la simulación
     * @param numRepetitions Número de repeticiones de la simulación
     * @param priority Prioridad de la simulación
     * @param outputFiles Ficheros de salida de la simulación
     * @param parameters Parámetros de la simulación
     * @throws Exception
     */
    public void updateSingleSimulation(long simulationId, String simulationName, int numRepetitions, int priority, ArrayList<String> outputFiles, HashMap<String, ParameterMapper> parameters) throws Exception{
        boolean updateQueue = false;
        SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
        switch(simulation.getStatus()){
        case REMOVING:
            throw new Exception("La simulación se está eliminando");
        case WAITING:
        case ERROR:
        case CLEANING:
            simulation.setName(simulationName);
            if(numRepetitions>0) {
                if(numRepetitions!=simulation.getNumRepetitions() && simulation.getNumRepetitions()==1) {
                    for(OutputFile outputFile : simulation.getOutputFiles()) {
                        if(!(outputFile.getType().equals(OutputFileType.RESULT_FILE) || (outputFile.getType().equals(OutputFileType.TABBED_FILE) && !outputFile.getOutputFileStructure().isMultiLine())))
                            throw new Exception();
                    }
                }
                simulation.setNumRepetitions(numRepetitions);
            }
            if(priority>0){
                if(priority!=simulation.getPriority()){
                    simulation.setPriority(priority);
                }
            }
            if(outputFiles!=null){
                simulation.getOutputFiles().clear();
                for(String outputFileName : outputFiles){
                    OutputFile outputFile = jpaController.getOutputFileController().getOutputFile(outputFileName, simulation.getProject().getId());
                    if(simulation.getNumRepetitions()>1)
                        if(!(outputFile.getType().equals(OutputFileType.RESULT_FILE) || (outputFile.getType().equals(OutputFileType.TABBED_FILE) && !outputFile.getOutputFileStructure().isMultiLine())))
                            throw new Exception();
                    simulation.addOutputFile(outputFile);
                }
                if(simulation.getStatus().equals(SimulationStatus.ERROR)){
                    //TODO - Ver cómo gestiono ahora los estados, que no sea el modelo
                }
            }
            if(parameters!=null){
                ArrayList<Parameter> parameterList = new ArrayList<>();
                for(Entry<String, ParameterMapper> entry : parameters.entrySet()){
                    ParameterDescription parameterDescription = jpaController.getParameterDescriptionController().getParameterDescription(entry.getKey(), simulation.getProject().getId());
                    if(entry.getValue().isRandom()){
                        Parameter parameter = new Parameter(true);
                        parameter.setParameterDescription(parameterDescription);
                        parameterList.add(parameter);
                    }
                    else if(!entry.getValue().getValues().isEmpty()){
                        if(!parameterDescription.isValid(entry.getValue().getValues().get(0))){
                            throw new Exception();
                        }
                        Parameter parameter = new Parameter(entry.getValue().getValues());
                        parameter.setParameterDescription(parameterDescription);
                        parameterList.add(parameter);
                    }
                    else{
                        Parameter parameter = new Parameter();
                        parameter.setParameterDescription(parameterDescription);
                        ArrayList<String> values = new ArrayList<>();
                        values.add(parameterDescription.getDefaultValue());
                        parameter.setValues(values);
                        parameterList.add(parameter);
                    }
                }
                
                jpaController.getParameterController().deleteParameters(simulationId);
                for(Parameter p : parameterList)
                    jpaController.getParameterController().createParameter(p, p.getParameterDescription().getName(), simulation.getProject().getId(), simulationId);
            }
            break;
        case PREPARING:
        case PROCESSING:
        case PAUSED:
        case REPORTING:
        case FINISHED:
            if(numRepetitions!=0 || outputFiles!= null || parameters!= null)
                throw new Exception("Error en la actualización de parámetros. La simulación ya está en ejecución");
            else{
                if(priority > 1){
                    simulation.setPriority(priority);
                    updateQueue = true;
                }
            }
            simulation.setName(simulationName);
            break;
		default:
			break;
        }
        
        simulation.setUpdateDate(new Date());
        jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
        if(updateQueue){
            executionController.updatePriorityJob(simulation.getProject().getUser().getUsername(), simulation.getProject().getId()+"/"+simulation.getId(), priority);
        }
    }
    
    /**
     * Método para crear (actualizar) una simulación de barrido de parámetros
     * @param simulationId Identficador de simulación
     * @param simulationName Nombre de la simulación
     * @param numRepetitions Número de repeticiones de la simulación
     * @param priority Prioridad del grupo de tareas que componen la simulación
     * @param outputFiles Ficheros de salida de la simulación
     * @param parameters Parámetros que va a utilizar la simulación
     * @throws Exception
     */
    public void updateParameterSweepSimulation(long simulationId, String simulationName, int numRepetitions, int priority, ArrayList<String> outputFiles, HashMap<String, ParameterMapper> parameters) throws Exception{
        boolean updateQueue = false;
        ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
        
        switch(simulation.getStatus()){
        case REMOVING:
            throw new Exception();
        case WAITING:
        case ERROR:
        case CLEANING:
            simulation.setName(simulationName);
            if(numRepetitions>0)
                simulation.setNumRepetitions(numRepetitions);
            if(priority>-1)
                simulation.setPriority(priority);
            if(outputFiles!=null){
                simulation.getOutputFiles().clear();
                for(String outputFileName : outputFiles){
                    OutputFile outputFile = jpaController.getOutputFileController().getOutputFile(outputFileName, simulation.getProject().getId());
                    if(!(outputFile.getType().equals(OutputFileType.RESULT_FILE) || (outputFile.getType().equals(OutputFileType.TABBED_FILE) && !outputFile.getOutputFileStructure().isMultiLine())))
                        throw new Exception();
                    simulation.addOutputFile(outputFile);
                }
                if(simulation.getStatus().equals(SimulationStatus.ERROR)){
                    //TODO - Ver cómo gestiono ahora los estados, que no sea el modelo
                }
            }
            if(parameters!=null){
                ArrayList<Parameter> parameterList = new ArrayList<>();
                for(Entry<String, ParameterMapper> entry : parameters.entrySet()){
                    ParameterDescription parameterDescription = jpaController.getParameterDescriptionController().getParameterDescription(entry.getKey(), simulation.getProject().getId());
                    if(entry.getValue().isRandom()){
                        Parameter parameter = new Parameter(true);
                        parameter.setParameterDescription(parameterDescription);
                        parameterList.add(parameter);
                    }
                    else if(!entry.getValue().getValues().isEmpty()){
                        if(!parameterDescription.isValid(entry.getValue().getValues())){
                            throw new Exception();
                        }
                        Parameter parameter = new Parameter(entry.getValue().getValues());
                        parameter.setParameterDescription(parameterDescription);
                        parameterList.add(parameter);
                    }
                    else if(entry.getValue().getMinValue()!=null && entry.getValue().getMaxValue()!=null && entry.getValue().getStep()!=null){
                        if(!parameterDescription.isValid(entry.getValue().getMinValue(), entry.getValue().getMaxValue())){
                            throw new Exception();
                        }
                        Parameter parameter = new Parameter(entry.getValue().getMinValue(), entry.getValue().getMaxValue(), entry.getValue().getStep());
                        parameter.setParameterDescription(parameterDescription);
                        parameterList.add(parameter);
                    }
                    else{
                        Parameter parameter = new Parameter();
                        parameter.setParameterDescription(parameterDescription);
                        ArrayList<String> values = new ArrayList<>();
                        values.add(parameterDescription.getDefaultValue());
                        parameter.setValues(values);
                        parameterList.add(parameter);
                    }
                }
                
                jpaController.getParameterController().deleteParameters(simulationId);
                for(Parameter p : parameterList)
                    //simulation.addParameter(p);
                    jpaController.getParameterController().createParameter(p, p.getParameterDescription().getName(), simulation.getProject().getId(), simulationId);
            }
            break;
        case PREPARING:
        case PROCESSING:
        case PAUSED:
        case REPORTING:
        case FINISHED:
            if(numRepetitions!=0 || outputFiles!= null || parameters!= null)
                throw new Exception("No se puede modificar una simulación que se esté ejecutando");
            else{
                if(priority > 1){
                    simulation.setPriority(priority);
                    updateQueue = true;
                }
            }
            simulation.setName(simulationName);
            break;
        }
        
        simulation.setUpdateDate(new Date());
        jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);

        if(updateQueue){
            for(SingleSimulation s : simulation.getSingleSimulations()){
                s.setPriority(priority);
                jpaController.getSingleSimulationController().updateSingleSimulation(s);
            }
            executionController.updatePriorityJob(simulation.getProject().getUser().getUsername(), simulation.getProject().getId()+"/"+simulation.getId(), priority);
        }
    }

    /**
     * Método para eliminar una simulación
     * @param simulationId Identificador de la simulación
     * @throws Exception
     */
    public void removeSingleSimulation(long simulationId) throws Exception{
        SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
        System.out.println("Se va eliminar la simualción "+simulation.getId());
        switch(simulation.getStatus()){
        case WAITING:
        case PAUSED:
        case FINISHED:
        case CLEANING:
            simulation.setStatus(SimulationStatus.REMOVING);
            jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
            cleanSingleSimulation(simulationId);
            //En principio, cuando acabe de enviar los trabajos, se da cuenta y hace el cambio
            break;
        case PREPARING:
        case PROCESSING:
            executionController.stopSimulation(simulation.getId(), SimulationTypeEnum.SINGLE_SIMULATION);
            simulation.setStatus(SimulationStatus.REMOVING);
            jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
            break;
        case REPORTING:
            // Detener informe...
            simulation.setStatus(SimulationStatus.REMOVING);
            jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
            cleanSingleSimulation(simulationId);
            break;
        case ERROR:
            if(!executionController.isAlreadyRemoved(simulationId) || !isSimulationAlreadyCleaned(simulationId)){
                simulation.setStatus(SimulationStatus.REMOVING);
                jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
                cleanSingleSimulation(simulationId);
            }
            else{
                cleanSingleSimulation(simulationId);
            }
        default: //Solo quedaría un estado
            break;
        }
        jpaController.getSingleSimulationController().deleteSingleSimulation(simulation);
    }

    /**
     * Método para eliminar una simulación de un proyecto
     * @param simulationId Identificador de la simulación
     * @throws Exception
     */
    public void removeParameterSweepSimulation(long simulationId) throws Exception{
        ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
        switch(simulation.getStatus()){
        case WAITING:
        case PAUSED:
        case FINISHED:
        case CLEANING:
            simulation.setStatus(SimulationStatus.REMOVING);
            jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
            cleanParameterSweepSimulation(simulationId);
            break;
        case PROCESSING:
        case PREPARING:
            executionController.stopSimulation(simulation.getId(), SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
            simulation.setStatus(SimulationStatus.REMOVING);
            jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
            break;
        case REPORTING:
            // Detener informe...
            simulation.setStatus(SimulationStatus.REMOVING);
            jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
            cleanParameterSweepSimulation(simulationId);
            break;
        case ERROR:
            if(!executionController.isAlreadyRemoved(simulationId) || !isSimulationAlreadyCleaned(simulationId)){
                simulation.setStatus(SimulationStatus.REMOVING);
                jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
                cleanParameterSweepSimulation(simulationId);
            }
            else{
                cleanParameterSweepSimulation(simulationId);
                //jpaController.getParameterSweepSimulationController().deleteParameterSweepSimulation(simulation);
            }
                
        default: //Solo quedaría un estado
            break;
        }
        jpaController.getParameterSweepSimulationController().deleteParameterSweepSimulation(simulation);
    }
    
    /**
     * Método para eliminar los residuos de una simulación que se haya eliminado
     * @param simulationId Identificador de la simulación
     * @throws Exception
     */
    public void cleanSingleSimulation(long simulationId) throws Exception{
        SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
        //Esta función la va a invocar realmente la simulación más general
        String path = "users/"+simulation.getProject().getUser().getUsername()+"/"+simulation.getProject().getId()+"/"+simulation.getId();
        CloudManager.deletePath(path);
    }
    
    /**
     * Método para eliminar los residuos de una simulación de barrido de parámetros que se haya eliminado
     * @param simulationId Identificador de la simulación
     * @throws Exception
     */
    public void cleanParameterSweepSimulation(long simulationId) throws Exception{
        ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
        String path = "users/"+simulation.getProject().getUser().getUsername()+"/"+simulation.getProject().getId()+"/"+simulation.getId();
        CloudManager.deletePath(path);
    }
    
    public void checkToClean() throws InterruptedException{
        lock.lock();
        
        try{
            while(simulationsToClean.isEmpty() && projectsToClean.isEmpty()) {
                System.out.println("WAITING TO REMOVE");
                notEmpty.await();
            }
        }
        finally{
            lock.unlock();
        }
    }
    
    public long getSimulationToClean(){
        synchronized(simulationsToClean){
            if(simulationsToClean.isEmpty())
                return -1;
            return simulationsToClean.poll();
        }
    }
    
    public int getProjectToClean(){
        synchronized (projectsToClean) {
            if(projectsToClean.isEmpty())
                return -1;
            return projectsToClean.poll();
        }
    }
    
    public int getSimulationToCleanProject(long simulationId){
        try{
            if(parameterSweepsToClean.contains(simulationId)){
                ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
                return simulation.getProject().getId();
            }
            else{
                SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
                return simulation.getProject().getId();
            }
        }
        catch(Exception e){
            return -1;
        }
    }
    
    public String getProjectToCleanUsername(int projectId){
        try{
            Project project = jpaController.getProjectController().getProject(projectId);
            return project.getUser().getUsername();
        }
        catch(Exception e){
            return null;
        }
    }
    
    public void notifySimulationCleaned(long simulationId){
        try{
            if(parameterSweepsToClean.contains(simulationId)){ //Barrido de parámetros
                parameterSweepsToClean.remove(simulationId);
                simulationsRemainingToClean.remove(simulationId);
                ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
                if(simulation.getStatus().equals(SimulationStatus.CLEANING)){
                    if(executionController.isAlreadyRemoved(simulationId)){//Puede que me toque cambiarlo de posición :S
                        simulation.setStatus(SimulationStatus.WAITING);
                        jpaController.getParameterSweepSimulationController().updateParameterSweepSimulation(simulation);
                    }
                }
                else if(simulation.getStatus().equals(SimulationStatus.REMOVING)){
                    if(executionController.isAlreadyRemoved(simulationId))
                        jpaController.getParameterSweepSimulationController().deleteParameterSweepSimulation(simulation);
                }
                
            }
            else{ //Simulación individual
                simulationsRemainingToClean.remove(simulationId);
                SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
                //Comprobar si el proyecto se está borrando
                if(simulation.getStatus().equals(SimulationStatus.CLEANING)){
                    if(executionController.isAlreadyRemoved(simulationId)){//Comprobar si el proyecto se está borrando
                        simulation.setStatus(SimulationStatus.WAITING);
                        jpaController.getSingleSimulationController().updateSingleSimulation(simulation);
                    }
                }
                else if(simulation.getStatus().equals(SimulationStatus.REMOVING)){
                    if(executionController.isAlreadyRemoved(simulationId))
                        jpaController.getSingleSimulationController().deleteSingleSimulation(simulation);
                }
            }
        }
        catch(Exception e){
            //TODO
        }
        
    }
    
    public void notifyProjectCleaned(int projectId){
        try{
            projectsRemainingToClean.remove(Integer.valueOf(projectId));
            System.out.println("Removed from remaining");
            if(!projectsAndSimulationsRemoving.containsKey(projectId)){
                System.out.println("Deleting");
                Project project = jpaController.getProjectController().getProject(projectId);
                jpaController.getProjectController().deleteProject(project);
                System.out.println("Deleted");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean isSimulationAlreadyCleaned(long simulationId){
        return !simulationsRemainingToClean.contains(simulationId);
    }

    /**
     * Pasarela para eliminar un proyecto del sistema
     * @param projectId Identificador del proyecto en el sistema
     */
    public void removeProject(int projectId) {
        try{
            Project project = jpaController.getProjectController().getProject(projectId);
            project.setRemove(true); //Marca de eliminación
            jpaController.getProjectController().updateProject(project);
            ArrayList<Long> removingSimulations = new ArrayList<>();
            projectsAndSimulationsRemoving.put(project.getId(), removingSimulations);
            projectsRemainingToClean.add(project.getId());
            
            //Eliminación de cola
            for(ParameterSweepSimulation simulation : project.getParameterSweepSimulations()){
                switch(simulation.getStatus()){
                case WAITING:
                case PAUSED:
                case FINISHED:
                    break;
                case PROCESSING:
                    executionController.stopSimulation(simulation.getId(), SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION);
                case PREPARING:
                    removingSimulations.add(simulation.getId());
                    break;
                case REPORTING:
                    // Detener informe
                    break;
                case CLEANING:
                case ERROR:
                case REMOVING:
                    if(!executionController.isAlreadyRemoved(simulation.getId()))
                        removingSimulations.add(simulation.getId());
                    break;
                default: //Solo quedaría un estado
                    break;
                }
            }
            
            for(SingleSimulation simulation : project.getSingleSimulations()){
                switch(simulation.getStatus()){
                case WAITING:
                case PAUSED:
                case FINISHED:
                    break;
                case PROCESSING:
                    executionController.stopSimulation(simulation.getId(), SimulationTypeEnum.SINGLE_SIMULATION);
                case PREPARING:
                    removingSimulations.add(simulation.getId());
                    break;
                case REPORTING:
                    // Detener informe
                    break;
                case CLEANING:
                case ERROR:
                case REMOVING:
                    if(!executionController.isAlreadyRemoved(simulation.getId()))
                        removingSimulations.add(simulation.getId());
                    break;
                default: //Solo quedaría un estado
                    break;
                }
            }
            
            
            System.out.println("Checked all simulations");
            if(removingSimulations.isEmpty()) {
                System.out.println("No simulations to stop");
                projectsAndSimulationsRemoving.remove(project.getId());
            }
            
            System.out.println("Preparing the notification");
            synchronized(projectsToClean){
                lock.lock();
                try {
                    System.out.println("Adding project to remove");
                    projectsToClean.add(project.getId());
                    System.out.println("notifico de la eliminación");
                    notEmpty.signal();
                }
                finally {
                    lock.unlock();
                }
            }
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public boolean isProjectRemoving(long simulationId, SimulationTypeEnum type){
        try{
            if(type.equals(SimulationTypeEnum.SINGLE_SIMULATION)){
                SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
                return simulation.getProject().isRemove();
            }
            else{
                ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
                return simulation.getProject().isRemove();
            }
        }
        catch(Exception e){ //Cuidado con como esta puesto esta parte del código
            return false;
        }
    }
    
    public void notifySimulation4ProjectRemoved(long simulationId, SimulationTypeEnum type){
        try{
            Project project = null;
            if(type.equals(SimulationTypeEnum.SINGLE_SIMULATION)){
                SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
                project = simulation.getProject();
            }
            else{
                ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
                project = simulation.getProject();
            }
            
            if(projectsAndSimulationsRemoving.containsKey(project.getId())){
                ArrayList<Long> remainingSimulations = projectsAndSimulationsRemoving.get(project.getId());
                remainingSimulations.remove(simulationId);
                if(remainingSimulations.isEmpty()){
                    projectsAndSimulationsRemoving.remove(project.getId());
                    
                    if(!projectsRemainingToClean.contains(project.getId()))
                        jpaController.getProjectController().deleteProject(project);
                }
            }
        }
        catch(Exception e){
            
        }
    }

    public int getUserCurrentSimulations(String username) {
        if(hasUser(username)) {
            int currentSimulations = executionController.getUserCurrentSimulations(username);
            if(currentSimulations!=-1)
                return currentSimulations;
            return 0;
        }
        else
            return -1;
    }

    public long getNumSingleSimulations(int projectId) {
        // TODO Auto-generated method stub
        return jpaController.getProjectController().getNumberOfSingleSimulations(projectId);
    }

    public int getCompletedSimulations(long simulationId, SimulationTypeEnum type) {
        // TODO Auto-generated method stub
        return executionController.getCompletedSimulations(simulationId, type);
    }
    
    public int getTotalSimulations(long simulationId, SimulationTypeEnum type){
        try {
            switch(type) {
            case SINGLE_SIMULATION:{
                SingleSimulation simulation = jpaController.getSingleSimulationController().getSingleSimulation(simulationId);
                return simulation.getNumRepetitions();
                }
            case PARAMETER_SWEEP_SIMULATION:{
                ParameterSweepSimulation simulation = jpaController.getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
                if(simulation.getSingleSimulations().isEmpty()){
                    return simulation.getRemainingSimulations();
                }
                else
                    return simulation.getSingleSimulations().size() * simulation.getNumRepetitions();
                }
            default:
                return -1;
            }
        }
        catch(Exception e) {
            return -1;
        }
    }
}
