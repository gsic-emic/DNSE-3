package dnse3.orchestration.resource;

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

public class ParameterDescriptionResource extends ServerResource{

	private int projectId;
	private String parameterName;
	
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));
			
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username))
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no pertenece al usuario");
			//Falta el caso de que se esté eliminando el proyecto
//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
//				throw new ResourceException(status)
			String parameterName = getAttribute("parameterName");
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasParameterDescription(parameterName,projectId))
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El parámetro no existe en el proyecto");

			this.projectId = projectId;
			this.parameterName = parameterName;
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Error en el identificador del proyecto "+e.toString());
		}
	}
	
	/**
	 * Método para obtener la descripción de un parámetro específico de un proyecto
	 * @return JSON con la descripción del parámetro
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			ParameterDescription parameterDescription = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getParameterDescriptionController().getParameterDescription(parameterName, projectId);
			
			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(ParameterDescription.class, new ParameterDescriptionSerializer(getOriginalRef())).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(parameterDescription));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

}
