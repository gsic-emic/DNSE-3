package dnse3.orchestration.jpa.controller.user;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import dnse3.orchestration.jpa.model.user.User;

public class UserController {
    
    private EntityManager em = null;
    
    public UserController(EntityManager em){
        if(em == null)
            throw new IllegalArgumentException("em: Entity manager can't be null");
        this.em = em;
    }
    
    public void setEntityManager(EntityManager em){
        if(em == null)
            throw new IllegalArgumentException("em: Entity manager can't be null");
        this.em = em;
    }
    
    /**
     * M�todo para obtener todos los usuarios del sistema.
     * @return Lista que contiene todos los usuarios del sistema.
     */
    public List<User> getUsers(){
        synchronized (em) {
            TypedQuery<User> query = em.createNamedQuery("listUsers", User.class);
            List<User> list = query.getResultList();
            return list;
        }
    }
    
    /**
     * M�todo para obtener la informaci�n de un usuario
     * @param username Identificador del usuario en el sistema
     * @return El usuario a trav�s de un objeto de la clase
     * @throws Exception Se lanza una excepci�n si el identificador de usuario no 
     * corresponde con ning�n usuario
     */
    public User getUser(String username) throws Exception{
        synchronized (em) {
            User user = em.find(User.class, username);
            if(user==null)
                throw new Exception("El identificador no corresponde a ning�n usuario del sistema"); //Tengo que preparar bien las excepciones
            return user;
        }
    }
    
    /**
     * M�todo para introducir un nuevo usuario en el sistema
     * @param user Nuevo usuario a crear
     * @throws Exception Lanza una excepci�n cuando el identificador del usuario ya 
     * exista en el sistema
     */
    public void createUser(User user) throws Exception{
        synchronized(em) {
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            
            em.getTransaction().begin();
            
            if(em.find(User.class, user.getUsername())!=null){
                em.getTransaction().rollback();
                throw new Exception("Coincide el identificador introducido con uno ya "
                        + "registrado en el sistema");
            }
            
            em.persist(user); //Revisar EntityExistsException
            em.flush();
            em.getTransaction().commit();
        }
    }
    
    /**
     * M�todo para actualizar la informaci�n de un usuario del sistema.
     * @param user Usuario a modificar
     */
    public void updateUser(User user) throws Exception{
        synchronized(em){
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            
            if(em.find(User.class, user.getUsername())!=null){
                em.getTransaction().rollback();
                throw new Exception("El usuario a modificar no existe.");
            }
            
            em.getTransaction().begin();
            em.merge(user);
            em.lock(user, LockModeType.PESSIMISTIC_WRITE);
            em.flush();
            em.getTransaction().commit();
        }
    }
    
    /**
     * M�todo para eliminar un usuario de la BBDD
     * @param user Usuario a eliminar
     * @throws Exception Si el usuario a eliminar no existe en el sistema se lanza una 
     * excecpci�n
     */
    public void deleteUser(User user) throws Exception{
        synchronized(em) {
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.getTransaction().begin();
            
            if(em.find(User.class, user.getUsername())!=null){
                em.getTransaction().rollback();
                throw new Exception("El usuario a eliminar no existe.");
            }
            
            em.remove(user);
            em.flush();
            em.getTransaction().commit();
        }
    }
}
