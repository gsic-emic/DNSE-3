package dnse3.orchestration.resource;

import java.util.List;

import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.jpa.model.user.User;
import dnse3.orchestration.jpa.model.user.UserSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class UsersResource extends ServerResource {
    
    /**
     * Método que comprueba si se puede conectar con la base de datos. Si no lo puede, 
     * lanza una excepción.
     */
    @Override
    public void doInit(){
        if(!((DNSE3OrchestrationApplication) getApplication()).getJpaController().testConnection())
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"The service couldn't connect to the database");
    }
    
    /**
     * Método que devuelve la lista de usuarios creados en el sistema.
     * @return Puede devolver la lista de usuarios si no está vacía o null si lo está.
     */
    @Get("json")
    public JsonRepresentation getJson(){
        List<User> userList = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getUserController().getUsers();
        
        if(userList.isEmpty())
            return null; //Mirar a ver si me devuelve 204 en este caso
        
        Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserSerializer(((DNSE3OrchestrationApplication) getApplication()).getDataController(), true)).setPrettyPrinting().create(); //De momento dejo de lado las representaciones JSON
        JsonRepresentation response = new JsonRepresentation(gson.toJson(userList));
        response.setIndenting(true);
        return response;
    }

}
