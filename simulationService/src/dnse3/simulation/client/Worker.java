package dnse3.simulation.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;

import org.apache.http.entity.ContentType;
import org.restlet.resource.ResourceException;

import dnse3.common.CloudManager;
import dnse3.common.TaskStatusEnum;
import dnse3.common.tasks.OutputFileSummary;
import dnse3.common.tasks.Task;

public class Worker implements Runnable {

    private RenewalAgent renewalAgent; // Class prepared to renew the execution of the simulation
    private String filenameFather;
    private String filename; // Name of the script/file with scripts
    private Process simulator; // Process used for running simulations
    //private String queueAddress;
    private Task task;
    private WorkerGenerator workerGenerator;
    private File tempPath;
    private ExecutorService renewPool;
    private static File homePath;
    
    static{
        File jarPath = new File(Worker.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        homePath = jarPath.getParentFile();
        /*System.out.println(homePath.getAbsolutePath());
        try{
            Thread.sleep(10000);
        }catch(InterruptedException e){
            
        }*/
    }

    /**
     * Constructor de la clase Worker. Se encarga de almacenar la tarea, el generador de trabajador y la dirección de la cola. 
     * También crea el agente para renovar la conexión con la base de datos.
     * @param task tarea a realizar
     * @param workerGenerator generador del trabajador
     * @param queueAddress dirección de la cola
     */
    public Worker(Task task, WorkerGenerator workerGenerator, String queueAddress) {
        this.task = task;
        this.workerGenerator = workerGenerator;
        //this.queueAddress = queueAddress;
        
        //NullPointerException

        this.renewalAgent= new RenewalAgent(task, queueAddress, this);
    }
    
    /**
     * Método para seleccionar la tarea. Realiza la regeneración de la conexión.
     * @param task tarea a seleccionar
     */
    public void setTask(Task task){
        this.task = task;
        this.renewalAgent.setTask(task);
    }

    /**
     * Método principal de la clase Worker. Realiza las siguientes acciones:
     * <p>Comprueba que la dirección de la tarea es correcta y que no está vacía
     * <p>Realiza la petición para la reserva de la tarea
     * <p>Genera el directorio temporal si no existía previamente
     * <p>Genera un directorio único para esta simulación
     * <p>Comprueba si el contenido del fichero que acaba de recibir es un null, si no lo es lleva a cabo la simulación, 
     * pero si es null borra los ficheros y directorios temporales.
     */
    public void run() {
        if (task.getSrc() == null || task.getSrc().isEmpty()) {
            System.err.println("Task " + task.getId() + "not valid: No Source address");
            clearFiles(tempPath);
        }
        else {
            renewPool = Executors.newSingleThreadExecutor();
            try {
                renewPool.execute(renewalAgent); // With all the data required, we can now start with the execution, as well as notify the server
                renewPool.shutdown();
                
                File file = new File(homePath,"tmp");
                if (!file.exists()) {
                    file.mkdirs();
                }
                
                this.tempPath = new File(file, "temp-" + Long.toString(System.nanoTime()));
                this.tempPath.mkdir();
                
                //Comprobamos si existe en la memoria caché
                File cacheado = new File(task.getSrc());
                File padre = new File(rutaPadre(task.getSrc()));
                String nombre = null;
                if(!cacheado.exists()) {//Descargamos el fichero de la nube
                    padre.mkdirs();
                    nombre = CloudManager.downloadFile(task.getSrc(), padre);
                }else {
                	nombre = cacheado.getName();
                }
                if(nombre != null){
                    this.filename = cacheado.getName();
                    this.filenameFather = rutaPadre(task.getSrc());

                    workerGenerator.validTask(this);
                    System.out.println("Exito GET");

                    //Mirar a ver si es null
                    if(filename!=null)
                        runSimulation();
                    else{
                        renewalAgent.notifyError(); // At this point, we have finished with the simulation, so we dont need to renew the task
                        clearFiles(tempPath);
                        System.err.println(Instant.now() + ": " +task.getSrc() + " filename==null");
                        workerGenerator.validTask(this);
                        workerGenerator.removeWorker(this, task.getId());
                    }
                }
                else{
                    renewalAgent.notifyError(); // At this point, we have finished with the simulation, so we dont need to renew the task
                    clearFiles(tempPath);
                    System.err.println(Instant.now() + ": " +task.getSrc() + " filename==null");
                    workerGenerator.validTask(this);
                    workerGenerator.removeWorker(this, task.getId());
                }
            }
            catch (ResourceException e) {
                // Notification to the Queue Service
                System.out.println("Error en el recurso");
                renewalAgent.notifyError();
                clearFiles(tempPath);
                workerGenerator.validTask(this);
                workerGenerator.removeWorker(this, task.getId());
            }
            catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                System.out.println("URI no válida");
                renewalAgent.notifyError();
                clearFiles(tempPath);
                workerGenerator.validTask(this);
                workerGenerator.removeWorker(this, task.getId());
            }
        }
    }

    /**
     * Método para obtener los directorios que contienen a un fichero
     * @param ruta Path que contentrá tanto los directorios como el fichero
     * @return Ruta de directorios sin incluir el fichero
     */
    private String rutaPadre(String ruta) {
        String [] padre = ruta.split("/");
        String p = "";
        for(int i = 0; i < padre.length - 1; i++)
            p += padre[i] + "/";
        System.err.println("Pinto ruta padre: "+p);
        return p;
    }

    /**
     * Ejecuta la simulación.
     * <p> Lo primero que realiza es la extracción del fichero zip en el directorio creado con el método run()
     * <p> Comprueba si en ese mismo directorio están los fichero o se ha creado un directorio a mayores. Si hay más carpetas salimos lanzando la excepción ResourceException.
     * <p> Comprueba la existencia del fichero project.xml. Si no existe vuelve a lanzar la excepción ResourceException.
     * <p> Copia el Makefile. Lleva a cabo la compilación del proyecto.
     * <p> Lleva a cabo la simulación de la ejecución. Espera a que finalice la misma.
     * <p> Comprueba si ha ocurrido un error. Lo notifica si se ha producido. Si no se ha producido genera los parámetros para su posterior manipulación.
     * <p> Cuando todo ha finalizado, lleva a cabo una limpieza de los ficheros.
     */
  private void runSimulation() {
        try {
            ArrayList<String> parameters = new ArrayList<>();
            File simfile = new File(filenameFather, filename);
            simfile.setExecutable(true);
            
            parameters.add(simfile.getAbsolutePath());
            for (Entry<String, String> e : task.getParameters().entrySet())
                parameters.add("--" + e.getKey() + "=" + e.getValue());

            // Success with the compilation, now we have to run it
            File output = new File(tempPath, "output");
            ProcessBuilder sim = new ProcessBuilder(parameters);
            sim.directory(tempPath);
            sim.redirectOutput(output);
            sim.redirectErrorStream(true);
            simulator = sim.start();
            System.out.println("Execution start");

            // Wait until the simulation ends
            int status = simulator.waitFor();

            if (!task.getStatus().equals(TaskStatusEnum.FINISHED)) {
                if (status != 0) {
                    // Notify the error to the server
                    System.out.println("Error en la ejecución");
                    for (String s : parameters)
                        System.out.println(s);
                    renewalAgent.notifyError();
                    }
                 else {
                    // Volcado de los parámetros
                    HashMap<String, Boolean> outputFiles = new HashMap<>();

                    for (OutputFileSummary o : task.getOutputFiles())
                        outputFiles.put(o.getName(), o.isMultiLine());

                    Writer param = new FileWriter(new File(tempPath, "parameters.json"));
                    Gson gson = new Gson();
                    gson.toJson(task.getParameters(), param);
                    param.close();

                    // Filtrar nombre segun outputFiles
                    File[] files = tempPath.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            // TODO Auto-generated method stub
                            return pathname.isFile() && (outputFiles.containsKey(pathname.getName()) || pathname.getName().equals("parameters.json"));
                        }
                    });
                    // Tengo que comprobar que solo tienen una línea
                    for (File file : files) {
                        if (task.getStatus().equals(TaskStatusEnum.FINISHED))
                            break;

                        BufferedReader br = new BufferedReader(new FileReader(file));
                        if (br.readLine() != null) { // If the file is not empty
                            //System.out.println(file == null);
                            //System.out.println(task == null);
                            //System.out.println(task.getOutputFiles() == null);
                            if (!(!outputFiles.containsKey(file.getName()) && br.readLine() != null))
                                //CloudManager.uploadFile(task.getOutputPath() + file.getName(), file, ContentType.TEXT_PLAIN); // The file is uploaded
                                CloudManager.uploadFile(task.getOutputPath(), file, ContentType.TEXT_PLAIN);
                        }
                        br.close(); // We close the file
                    }
                }
            }

            renewalAgent.stopRunning(); // At this point, we have finished with the simulation, so we dont need to renew the task
            // renewalAgent.join();
            try{
                renewPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        catch (IOException | IllegalStateException e) {
            // Should think about this case
            e.printStackTrace();
            if(renewalAgent!=null)
                renewalAgent.stopRunning();
        }
        catch (InterruptedException e) {
            // The server asked to stop the simulation.
            // We have to remove all the files generated, as well as stop the renewalAgent
            e.printStackTrace();
            if(renewalAgent!=null)
                renewalAgent.stopRunning();
        }
        clearFiles(tempPath);
        System.out.println(Instant.now());
        workerGenerator.removeWorker(this, task.getId());
    }

    /**
     * Método para detener una simulación basandonos en el identificador del mismo.
     * @param simulationID Identificador de la simulación que va a detener.
     * @throws WorkerException Se lanza cuando el identificador que nos llega no corresponde con ninguna tarea.
     */
    public void stopSimulation(String simulationID) throws WorkerException {
        if (!task.getId().equals(simulationID))
            throw new WorkerException();
        // if (this.renewalAgent.isAlive())
        if(!this.renewPool.isTerminated())
            this.renewalAgent.setAborted(true);
        this.task.setStatus(TaskStatusEnum.FINISHED);
        this.simulator.destroy();
    }

    /**
     * Método para detener una simulación.
     */
    public void stopSimulation() {
        this.task.setStatus(TaskStatusEnum.FINISHED);
        this.simulator.destroy();
    }

    /**
     * Método para eliminar los residuos de las distintas simulaciones. Los residuos pueden ser tanto ficheros como directorios.
     * @param file fichero o directorio a eliminar.
     */
    public void clearFiles(File file) {
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();

            for (File f : files) {
                if (f.isDirectory())
                    clearFiles(f);
                else
                    f.delete();
            }

            file.delete();
        }
    }

    /**
     * Método que te devuelve la tarea almacenada en la variable privada de la clase.
     * @return tarea almacenada en la variable privada de la clase.
     */
    public Task getTask() {
        return task;
    }
}