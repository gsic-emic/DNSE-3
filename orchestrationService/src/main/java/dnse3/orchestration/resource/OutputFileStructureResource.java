package dnse3.orchestration.resource;

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

public class OutputFileStructureResource extends ServerResource {

	private int projectId;
	private String outputFileStructureName;
	
	/**
	 * Método que comprueba si los valores de proyecto, usuario y outFile son válidos. Si lo son, asigna el valor 
	 * a las variables de la clase username, projectId y outputFileStrutureName
	 */
	@Override
	public void doInit(){
		try{
			String username = getAttribute("username");
			int projectId=Integer.valueOf(getAttribute("projectId"));
			
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasProject(projectId,username))
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El proyecto no pertenece al usuario");
			//Falta el caso de que se esté eliminando el proyecto
//			if(((DNSE3OrchestrationApplication) getApplication()).getJpaController().getProjectController().isRemoving(projectId))
//				throw new ResourceException(status)
			String outputFileStructureName = getAttribute("outputFileStructureName");
			if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasOutputFileStructure(outputFileStructureName,projectId))
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "El nombre del fichero de salida no pertenece al proyecto");

			this.projectId = projectId;
			this.outputFileStructureName = outputFileStructureName;
		}
		catch(NumberFormatException e){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "Error en el identificador del proyecto "+e.toString());
		}
	}

	/**
	 * Método para obtener la estructura de un fichero de resultados.
	 * @return Devuelve el JSON con la estructura del fichero de resultados
	 */
	@Get("json")
	public JsonRepresentation getJson(){
		try{
			OutputFileStructure outputFileStructure = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getOutputFileStructureController().getOutputFileStructure(outputFileStructureName, projectId);
			
			Gson gson = new GsonBuilder().registerTypeHierarchyAdapter(OutputFileStructure.class, new OutputFileStructureSerializer(getOriginalRef())).setPrettyPrinting().create();
			JsonRepresentation response = new JsonRepresentation(gson.toJson(outputFileStructure));
			response.setIndenting(true);
			return response;
		}
		catch(Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}
}
