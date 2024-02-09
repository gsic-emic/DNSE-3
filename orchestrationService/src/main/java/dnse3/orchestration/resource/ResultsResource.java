package dnse3.orchestration.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import dnse3.common.CloudManager;
import dnse3.orchestration.auxiliar.RegistraLog;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.SimulationStatus;
import dnse3.orchestration.jpa.model.simulation.SimulationTypeEnum;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

/**
 * ResultsResource
 */
public class ResultsResource extends ServerResource {
    
    public long simulationId;
    public SimulationTypeEnum type;
    public String username;
    public int projectId;
    //private static final Logger usersLogger = Logger.getLogger("DNSE3UsersLogger");

    @Override
    public void doInit(){
        try{
            String username = getAttribute("username");
            int projectId = Integer.valueOf(getAttribute("projectId"));

            if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId, username))
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no pertenece al usuario");

            Long simulationId = Long.valueOf(getAttribute("simulationId"));
            SimulationTypeEnum type;
            switch (getAttribute("typeSimulation")) {
                case "singlesimulations":
                    type = SimulationTypeEnum.SINGLE_SIMULATION;
                    break;
                case "parametersweepsimulations":
                    type = SimulationTypeEnum.PARAMETER_SWEEP_SIMULATION;
                    break;
                default:
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Tipo de simulación no soportada");
            }
            
            if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasSimulation(simulationId, projectId, type))
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "La simulación no pertenece al proyecto");

            this.simulationId = simulationId;
            this.type = type;
            this.projectId = projectId;
            this.username = username;
        } catch(Exception e){
            throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e.getMessage());
        }
    }
    
    @Get
    public Representation getZip() {
        try {
            boolean rep = false;
            String simulationName = "";
            
            switch (type) {
            case SINGLE_SIMULATION:
                SingleSimulation single = ((DNSE3OrchestrationApplication) getApplication()).getJpaController()
                                            .getSingleSimulationController().getSingleSimulation(simulationId);
                
                if(!single.getStatus().equals(SimulationStatus.FINISHED)) {
                    
                    throw new ResourceException(Status.CLIENT_ERROR_CONFLICT, "La simulación no se ha completado");
                }
                if(!single.getStatus().equals(SimulationStatus.FINISHED)) {
                
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "La simulación aún no se ha completado");
                }
                
                simulationName = single.getName();
                
                if(single.getNumRepetitions()==1)
                    rep = true;
                break;
            case PARAMETER_SWEEP_SIMULATION:
                ParameterSweepSimulation sweep = ((DNSE3OrchestrationApplication) getApplication()).getJpaController()
                                                    .getParameterSweepSimulationController().getParameterSweepSimulation(simulationId);
                if(!sweep.getStatus().equals(SimulationStatus.FINISHED)) {
                    
                    throw new ResourceException(Status.CLIENT_ERROR_CONFLICT, "La simulación no se ha completado");
                }
                if(!sweep.getStatus().equals(SimulationStatus.FINISHED)) {
                
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "La simulación aún no se ha completado");
                }
                simulationName = sweep.getName();
                break;
            default:
                    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Tipo de simulación no soportado");
            }
            
            String ruta = "users/"+username+"/"+projectId+"/"+simulationId;
            
            String rutaTMP = "tmp/temp-"+Long.toString(System.nanoTime())+"/";
            File tempPath = new File(rutaTMP);
            tempPath.mkdirs();
            File zipFile;
            
            if(rep) {//Simulación de una única tarea
                List<String> outputFiles = CloudManager.listObjects(ruta + "/outputs");
                if(outputFiles == null)
                    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Los ficheros de resultados no están listos");
                ArrayList<File> files = new ArrayList<>();
                for(String a : outputFiles) {
                    files.add(new File(CloudManager.downloadFile(a, tempPath)));
                }
                FileOutputStream fileOutputStream = new FileOutputStream(rutaTMP + "outputs.zip");
                ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
                for(File f : files) {
                    FileInputStream fileInputStream = new FileInputStream(f);
                    ZipEntry zipEntry = new ZipEntry(f.getName());
                    zipOutputStream.putNextEntry(zipEntry);
                    byte[] b = new byte[1024];
                    int l;
                    while((l = fileInputStream.read(b)) >= 0) {
                        zipOutputStream.write(b, 0, l);
                    }
                    fileInputStream.close();
                    f.delete();
                }
                zipOutputStream.close();
                fileOutputStream.close();
                zipFile = new File(rutaTMP, "outputs.zip");
            }
            else {
                List<String> outputFiles = CloudManager.listObjects(ruta + "/results");
                if(outputFiles == null)
                    throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Los ficheros de resultados no están listos");
                zipFile = new File(CloudManager.downloadFile(ruta + "/results/results.zip", tempPath));
            }
            
            FileRepresentation response = new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);
            Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT); 
            disp.setFilename(simulationName + ".zip"); 
            disp.setSize(zipFile.length());
            response.setAutoDeleting(true);
            response.setDisposition(disp);
			RegistraLog.registra(RegistraLog.desResult, username, projectId, simulationId);
            //usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - SimulationId:" + simulationId + " - DESCARGA DE RESULTADOS");
            return response;
        } catch (Exception e) {
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Error al obtener los ficheros de resultados");
        }
    }
}