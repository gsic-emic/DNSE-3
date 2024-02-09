package dnse3.orchestration.jpa.controller.project;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import dnse3.orchestration.jpa.model.project.OutputFileStructure;
import dnse3.orchestration.jpa.model.project.Project;

public class OutputFileStructureController {

	private EntityManager em = null;
	
	public OutputFileStructureController(EntityManager em){
		if (em==null)
			throw new IllegalArgumentException("em : Entity manager can't be null");
		this.em = em;
	}
	
	public void setEntityManager(EntityManager em){
		if (em==null)
			throw new IllegalArgumentException("em : Entity manager can't be null");
		this.em = em;
	}
	
	/**
	 * Pasarela para obtener la estructura de los ficheros de salida
	 * @param projectId Identificador del proyecto en el sistema
	 * @return Lista de estructuras de ficheros de salida
	 * @throws Exception Lanza una exceción cuando el proyecto no existe en el sistema
	 */
	public List<OutputFileStructure> getOutputFileStructures(int projectId) throws Exception{
		synchronized(em){
			Project project = em.find(Project.class, projectId);
			if(project==null)
				throw new Exception("El proyecto no existe en el sistema");
			
			return project.getOutputFileStructures();
		}
	}
	
	/**
	 * Pasarela para recuperar el valor de la estructura del fichero de resultados que se indique
	 * @param outputFileStructureName Nombre del fichero de resultados a comprobar
	 * @param projectId Identificador del proyecto en el sistema
	 * @return Número de coincidencias al realizar la consulta sobre la base de datos
	 * @throws Exception Lanza una excepción si no existe ningún fichero de resultados que coincida con
	 * el nombre dentro del proyecto
	 */
	public OutputFileStructure getOutputFileStructure(String outputFileStructureName, int projectId) throws Exception{
		synchronized(em){
			try{
				TypedQuery<OutputFileStructure> query = em.createNamedQuery("selectOutputFileStructure", OutputFileStructure.class);
				query.setParameter("outputFileStructureName", outputFileStructureName);
				query.setParameter("projectId", projectId);
				
				return query.getSingleResult();
			}
			catch(NoResultException e){
				throw new Exception("No existe ninguna coincidencia en el sistema");
			}
			catch(Exception e){
				throw new Exception(e.toString());
			}
		}
	}
	
	public void createOutputFileStructure(OutputFileStructure outputFileStructure, int projectId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			em.getTransaction().begin();
			
			Project project = em.find(Project.class, projectId);
			if(project==null){
				em.getTransaction().rollback();
				throw new Exception();
			}
			
			outputFileStructure.setProject(project);
			em.persist(outputFileStructure);
			em.flush();
			em.getTransaction().commit();
		}
	}
}
