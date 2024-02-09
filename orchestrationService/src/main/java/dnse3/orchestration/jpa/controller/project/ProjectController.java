package dnse3.orchestration.jpa.controller.project;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.jpa.model.user.User;

public class ProjectController {
	
	private EntityManager em;
	
	public ProjectController(EntityManager em){
		if(em==null)
			throw new IllegalArgumentException("em: Entity manager can't be null");
		this.em = em;
	}
	
	public void setEntityManager(EntityManager em){
		if(em==null)
			throw new IllegalArgumentException("em: Entity manager can't be null");
		this.em = em;
	}
	
	public List<Project> getProjects(String username) throws Exception{
		synchronized(em){
			User user = em.find(User.class, username);
			if(user == null)
				throw new Exception("Usuario no registrado en el sistema");
			return user.getProjects();
		}
	}
	
	/**
	 * Método para recuperar un proyecto del sistema
	 * @param projectId Identificador del proyecto en el sistema
	 * @return Representación del proyecto mediante un objeto de la clase Project
	 * @throws Exception Lanza una excepción cuando el proyecto no existe en el sistema
	 */
	public Project getProject(int projectId) throws Exception{
		synchronized(em){
			Project project = em.find(Project.class, projectId);
			if(project==null)
				throw new Exception("El proyecto no existe en el sistema.");
			return project;
		}
	}
	
	/**
	 * Método para agregar un proyecto al sistema y asignarlo a un usuario 
	 * @param project Proyecto que se va a agregar
	 * @param username Identificador del usuario al que se le va a agregar el proyecto
	 * @throws Exception Lanza una excepción cuando el usuario no existe en la BBDD
	 */
	public void createProject(Project project, String username) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			em.getTransaction().begin();
			
			User user = em.find(User.class, username);
			if(user==null){
				em.getTransaction().rollback();
				throw new Exception("El usuario no existe en el sistema");
			}
			
			project.setUser(user);
			user.addProject(project);
			em.persist(project);
			em.flush();
			em.getTransaction().commit();
		}
	}
	
	/**
	 * Método para modificar a un proyecto que ya esté registrado en el sistema
	 * @param project Objeto que representa al proyecto
	 */
	public void updateProject(Project project){
		synchronized (em) {
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			project.setUpdateDate(new Date());
			
			em.getTransaction().begin();
			em.merge(project);
			em.lock(project, LockModeType.PESSIMISTIC_WRITE);
			em.flush();
			em.getTransaction().commit();
		}
		System.out.println("Proyecto actualizado");
	}
	
	/**
	 * Método para eliminar un proyecto que esté registrado en el sistema
	 * @param project Objeto que representa al proyecto
	 */
	public void deleteProject(Project project){
		synchronized (em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			try{
				em.getTransaction().begin();
				if(!em.contains(project))//Si no contine el proyecto?
					em.merge(project);
				
				User user = project.getUser();
				user.getProjects().remove(project);
				em.remove(project);
				em.merge(user);
				em.flush();
				em.getTransaction().commit();
			}
			catch(IllegalArgumentException e){
				e.printStackTrace();
			}
		}
	}
	
	public long getNumberOfSingleSimulations(int projectId) {
		synchronized(em){
			try{
				TypedQuery<Long> query = em.createNamedQuery("getNumberSingleSimulations", Long.class);
				query.setParameter("projectId", projectId);
				long result = query.getSingleResult();
	
				//em.close();
				return result;
			}catch(NoResultException es){
				//em.close();
				return 0;
			}
		}
	}

}
