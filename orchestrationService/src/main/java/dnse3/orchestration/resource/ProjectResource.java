package dnse3.orchestration.resource;

import org.json.JSONObject;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.jpa.model.project.ProjectSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class ProjectResource extends ServerResource {

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
            //Falta el caso de que se esté eliminando el proyecto
            //if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
                //throw new ResourceException(status)
        }
        catch(NumberFormatException e){
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Error en el identificador del proyecto "+e.toString());
        }
    }
    
    /**
     * Método para recuperar los valores de un proyecto.
     * @return Devuelve, en formato JSON, las características del proyecto.
     */
    @Get("json")
    public JsonRepresentation getJson() throws ResourceException{
        try{
            Project project = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().getProject(projectId);
            Gson gson = new GsonBuilder().registerTypeAdapter(Project.class, new ProjectSerializer(new Reference(getOriginalRef()),((DNSE3OrchestrationApplication) getApplication()).getDataController())).setPrettyPrinting().create();
            JsonRepresentation response = new JsonRepresentation(gson.toJson(project));
            response.setIndenting(true);
            return response;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
        }
    }
    
    /**
     * Método para actualiar un proyecto del sistema.
     * @param request JSON con las características del proyecto.
     */
    @Put("json")
    public void putJson(JsonRepresentation request) throws ResourceException{
        try{
            JSONObject projectObj = request.getJsonObject();
            if(!projectObj.has("name")&&!projectObj.has("description")){
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "El JSON está mal formado");
            }
            String name = null;
            String description = null;
            if(projectObj.has("name"))
                name=projectObj.getString("name");
            if(projectObj.has("description"))
                description=projectObj.getString("description");
            ((DNSE3OrchestrationApplication) getApplication()).getDataController().updateProject(name,description,projectId);
        }
        catch(Exception e){
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
    }
    
    /**
     * Método para eliminar a un proyecto del sistma
     */
    @Delete
    public void remove() throws ResourceException{
        try{
            ((DNSE3OrchestrationApplication) getApplication()).getDataController().removeProject(projectId);
            setStatus(Status.SUCCESS_ACCEPTED);
        }
        catch(Exception e){
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
        }
    }

}
