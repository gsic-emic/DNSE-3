package dnse3.orchestration.resource;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;

import dnse3.common.CloudManager;
import dnse3.orchestration.auxiliar.RegistraLog;
import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class ProjectModelResource extends ServerResource {

    private int projectId;
    private String username;
    
    /**
     * Método ejecutado al inicio. Fija los valores de las variables username y projectId 
     * cuando ha comprobado que el proyecto y el usuario existen en el sistema.
     */
    @Override
    public void doInit(){
        try{
            String username = getAttribute("username");
            int projectId = Integer.valueOf(getAttribute("projectId"));
            if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no está asignado al usuario");
            }
            this.projectId = projectId;
            this.username = username;
        }
        catch(NumberFormatException e){
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Error en el identificador del proyecto "+e.toString());
        }
    }
    
    /**
     * Método para descargar el modelo de un proyecto.
     */
    @Get
    public Representation downloadModel() throws ResourceException{
        try{
            Project project = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().getProject(projectId);
            if(project.getPackageURI() != null){
                String rutaTMP = "tmp/temp-"+Long.toString(System.nanoTime())+"/";
                File tempPath = new File(rutaTMP);
                tempPath.mkdirs();
                File zipFile = new File(CloudManager.downloadFile(project.getPackageURI(), tempPath));

                FileRepresentation response = new FileRepresentation(zipFile, MediaType.APPLICATION_ZIP);
                Disposition disp = new Disposition(Disposition.TYPE_ATTACHMENT); 
                disp.setFilename(project.getName() + ".zip"); 
                disp.setSize(zipFile.length());
                response.setAutoDeleting(true);
                response.setDisposition(disp);
                DNSE3OrchestrationApplication.dameLogger().info("");
    			RegistraLog.registra(RegistraLog.desModelo, username, projectId);
                //usersLogger.info("Username:" + username + " - ProjectId:" + projectId + " - DESCARGA DEL MODELO");
                return response;
            }
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        }
        return null;
    }

}