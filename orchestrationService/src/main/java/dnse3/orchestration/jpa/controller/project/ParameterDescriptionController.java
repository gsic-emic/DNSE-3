package dnse3.orchestration.jpa.controller.project;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import dnse3.orchestration.jpa.model.project.ParameterDescription;
import dnse3.orchestration.jpa.model.project.Project;

public class ParameterDescriptionController {

	private EntityManager em;
	
	public ParameterDescriptionController(EntityManager em){
		if(em==null)
			throw new IllegalArgumentException("em: Entity manager can't be null");
		this.em = em;
	}
	
	public void setEntityManager(EntityManager em){
		if(em==null)
			throw new IllegalArgumentException("em: Entity manager can't be null");
		this.em = em;
	}
	
	/**
	 * Pasarela para obetener la descripción de los parámetros que están disponibles en un proyecto
	 * @param projectId Identificador del proyecto en el sistema
	 * @return La lista con la descripción de los parámetros del sistema
	 * @throws Exception Lanza una excepción cuando el proyecto no existe en el sistema
	 */
	public List<ParameterDescription> getParameterDescriptions(int projectId) throws Exception{
		synchronized (em){
			Project project = em.find(Project.class, projectId);
			if(project == null)
				throw new Exception("El proyecto no se encuentra en el sistema");
			return project.getParameterDescriptions();
		}
	}
	
	/**
	 * Método para comprobar si un parámetro existe dentro de un proyecto
	 * @param parameterName Nombre del parámetro
	 * @param projectId Identificador del proyecto en el sistema
	 * @return Puede devolver el número de coincidencias que encuentra al realizar la consulta a la BBDD
	 * @throws Exception 
	 */
	public ParameterDescription getParameterDescription(String parameterName, int projectId) throws Exception{
		synchronized (em){
			try{
				TypedQuery<ParameterDescription> query = em.createNamedQuery("selectParameterDescription", ParameterDescription.class);
				query.setParameter("parameterName", parameterName);
				query.setParameter("projectId", projectId);
				
				return query.getSingleResult();
			}
			catch (NoResultException e){
				throw new Exception("El parámetro no existe en el sistema");
			}
			catch (Exception e){
				throw new Exception("Se ha producido un error al realizar la consulta por el parámetro "+e.toString());
			}
		}
	}
	
	/**
	 * Método para agregar el parámetro a la BBDD
	 * @param parameterDescription Identificador del parámetro
	 * @param projectId Identificador del proyecto al que va a ser asignado el parámetro
	 * @throws Exception Lanza una excepción cuando el proyecto no existe
	 */
	public void createParameterDescription(ParameterDescription parameterDescription, int projectId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			em.getTransaction().begin();
			
			Project project = em.find(Project.class, projectId);
			if(project == null){
				em.getTransaction().rollback();
				throw new Exception();
			}
			
			parameterDescription.setProject(project);
			em.persist(parameterDescription);
			em.flush();
			em.getTransaction().commit();
		}
	}
}
