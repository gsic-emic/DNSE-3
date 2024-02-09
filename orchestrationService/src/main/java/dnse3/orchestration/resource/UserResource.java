package dnse3.orchestration.resource;

import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Put;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dnse3.orchestration.jpa.model.user.User;
import dnse3.orchestration.jpa.model.user.UserSerializer;
import dnse3.orchestration.server.DNSE3OrchestrationApplication;

public class UserResource extends ServerResource {

    /** Variable privada donde se almacena el nombre del usuario sobre el que se va a 
     * realizar la operaci�n.*/
    private String username;
    
    /**
     * M�todo que comprueba la posibilidad de conexi�n con la base de datos. Si no es 
     * posible conectarse con la base de datos lanza una excepci�n. Si puede conectarse
     * con la base de datos fija el valor de la variable privada username.
     */
    @Override
    public void doInit(){
        if(!((DNSE3OrchestrationApplication) getApplication()).getJpaController().testConnection())
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL,"The service couldn't connect to the database");
        
        this.username = getAttribute("username");
    }
    
    /**
     * M�todo que devuelve la informaci�n del usuario que se le indique.
     * @return Representaci�n en JSON de la informaci�n del usuario sobre el que se ha 
     * realizado la consulta. Si al consultar la existencia del usuario ocasiona una
     * excepci�n, se le devolver� al cliente una excepci�n del tipo recurso indicando  
     * no lo ha encontrado.
     */
    @Get("json")
    public JsonRepresentation getJson(){
        try{
            User user = ((DNSE3OrchestrationApplication) getApplication()).getJpaController().getUserController().getUser(username);
            //Me falta recuperar el n�mero de simulaciones que tienen activas
            Gson gson = new GsonBuilder().registerTypeAdapter(User.class, new UserSerializer(((DNSE3OrchestrationApplication) getApplication()).getDataController())).setPrettyPrinting().create();
            JsonRepresentation response = new JsonRepresentation(gson.toJson(user));
            response.setIndenting(true);
            return response;
        }
        catch(Exception e){
            e.printStackTrace();
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }
    }
    
    /**
     * M�todo para crear un usuario en el sistema.
     * @param request Elemento JSON que se incluya deber� incluir:
     * <li> Nombre del usuario (username), que deber� ser el mismo que se introduce en la direcci�n URI del recurso</li>
     * <li> Como elemento opcional, podr� incluir el n�mero m�ximo de simulacinoes que se desea que pueda lanzar en paralelo (maxSimulations). Si no se incluye utilizar� el valor por defecto (10.000) </li>
     */
    @Put("json")
    public void postJson(JsonRepresentation request){
        try{
            JSONObject obj = request.getJsonObject();
            
            if(!obj.has("username") || !obj.getString("username").equals(username)){
                //Log
                throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "The username is not provided or does not fit the request");
            }
            
            if(!((DNSE3OrchestrationApplication) getApplication()).getDataController().hasUser(username)){
                if(obj.has("maxSimulations"))
                    ((DNSE3OrchestrationApplication) getApplication()).getDataController().createUser(username, obj.getInt("maxSimulations"));
                else
                    ((DNSE3OrchestrationApplication) getApplication()).getDataController().createUser(username);
                setStatus(Status.SUCCESS_CREATED);
            }
            else{
                if(obj.has("maxSimulations"))
                    ((DNSE3OrchestrationApplication) getApplication()).getDataController().updateUser(username, obj.getInt("maxSimulations"));
            }
        }
        catch(JSONException e){
            //Log
            throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "The server could not process the request");
        }
        catch(Exception e){
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
    }
    
    /**
     * M�todo para elminar un usuario del sistema.
     */
    @Delete
    public void remove(){
        try {
            ((DNSE3OrchestrationApplication) getApplication()).getDataController().deleteUser(username);
            setStatus(Status.SUCCESS_OK);
        }catch (Exception e) {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, e.getMessage());
        }
    }
}
