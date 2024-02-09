package dnse3.orchestration.resource;

import java.util.List;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.jpa.model.project.ParameterDescription;
import dnse3.orchestration.jpa.model.project.ParameterDescriptionSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class ParameterDescriptionsResource extends ServerResource {

	private int projectId;
	
	/**
	 * Método para iniciar el recurso. Fija el identificador del usuario y proyecto si no encuentra
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no pertenece al usuario");
			}
			this.projectId = projectId;
			//Falta el caso de que se esté eliminando el proyecto
//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
//				throw new ResourceException(status)
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Error en el identificador del proyecto "+e.toString());
		}
	}
	
	/**
	 * Método para obtener la descripción de los parámetros del proyecto
	 * @return JSON con la descripción de los parámetros del modelo
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			List<ParameterDescription> parameterDescriptions = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getParameterDescriptionController().getParameterDescriptions(projectId);
			if(parameterDescriptions.isEmpty())
				return null;
			
			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ParameterDescription.class, new ParameterDescriptionSerializer(getOriginalRef(),true)).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(parameterDescriptions));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}

}
