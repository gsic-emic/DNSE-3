package dnse3.orchestration.resource;

import java.util.List;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.jpa.model.project.OutputFileStructure;
import dnse3.orchestration.jpa.model.project.OutputFileStructureSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class OutputFileStructuresResource extends ServerResource {

	private int projectId;
	
	/**
	 * Método que realiza las operaciones oportunas para comprobar si un proyecto pertenece a un usuario. 
	 * Si pertenece, se asignan los valores de ls variables username y projectId
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId = Integer.valueOf(getAttribute("projectId"));
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username)){
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no pertece al usuario");
			}
			this.projectId= projectId;
			//Falta el caso de que se esté eliminando el proyecto
//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
//				throw new ResourceException(status)
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Error en el identificador del proyecto "+e.toString());
		}
	}
	
	/**
	 * Método para obtener la lista con la estrucutra que tienen los ficheros de salida
	 * @return JSON con la lista de las estructuras de los ficheros de resultado
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			List<OutputFileStructure> list = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getOutputFileStructureController().getOutputFileStructures(projectId);
			
			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(OutputFileStructure.class, new OutputFileStructureSerializer(getOriginalRef(),true)).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(list));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}
}
