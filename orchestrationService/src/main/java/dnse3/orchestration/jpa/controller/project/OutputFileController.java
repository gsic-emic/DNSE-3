package dnse3.orchestration.jpa.controller.project;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import dnse3.orchestration.jpa.model.project.OutputFile;
import dnse3.orchestration.jpa.model.project.OutputFileStructure;
import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.jpa.model.simulation.Simulation;

public class OutputFileController {
	
	private EntityManager em = null;
	
	public OutputFileController(EntityManager em){
		if(em == null)
			throw new IllegalArgumentException("em : Entity manager can't be empty");
		this.em=em;
	}
	
	public void setEntityManager(EntityManager em){
		if(em == null)
			throw new IllegalArgumentException("em : Entity manager can't be empty");
		this.em=em;
	}
	
	/**
	 * Método para obtener los outFiles de un proyecto
	 * @param projectId Identificador del proyecto
	 * @return Lista de objetos con los outFiles
	 * @throws Exception Lanza una excepción cuando no existe ningún fichero de salida
	 */
	public List<OutputFile> getOutputFilesFromProject(int projectId) throws Exception{
		synchronized(em){
			Project project = em.find(Project.class, projectId);
			if(project==null)
				throw new Exception("No existe ningún outFile en el proyecto");
			
			return project.getOutputFiles();
		}
	}
	
	/**
	 * Método que devuelve la lista de ficheros de salida de la simulación
	 * @param simulationId Identificador de la salida
	 * @return Lista de objetos de ficheros de salida
	 * @throws Exception Lanza una excepción cuando no existe ningún fichero de salida
	 */
	public List<OutputFile> getOutputFilesFromSimulation(long simulationId) throws Exception{
		synchronized(em){
			Simulation simulation = em.find(Simulation.class, simulationId);
			if(simulation==null)
				throw new Exception("No existe ningún outFiles de la simulación");
			
			return simulation.getOutputFiles();
		}
	}
	
	/**
	 * Método para obtener el fichero de salida del proyecto indicados
	 * @param outputFileName Nombre del fichero de salida a recuperar
	 * @param projectId Identificador del proyecto
	 * @return Fichero de salida
	 * @throws Exception El resultado de la query no ha obtenido ningún parámetro
	 */
	public OutputFile getOutputFile(String outputFileName, int projectId) throws Exception{ //No se puede recuperar un OutputFile desde la simulación sin iterar
		synchronized (em) {
			try{
				TypedQuery<OutputFile> query = em.createNamedQuery("selectOutputFile", OutputFile.class);
				query.setParameter("outputFileName", outputFileName);
				query.setParameter("projectId", projectId);
			
				OutputFile output = query.getSingleResult();
				//em.close();
				return output;
			}catch(NoResultException e){
				//em.close();
				throw new Exception("La query indica que no existe ese tipo de fichero de salida en el proyecto solicitado");
			}
			catch(Exception e){
				throw new Exception();
			}
		}
	}
	
	public void createOutputFile(OutputFile outputFile, String outputFileStructureName, int projectId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			em.getTransaction().begin();
			
			try{
				Project project = em.find(Project.class, projectId);
				if(project == null){
					em.getTransaction().rollback();
					throw new Exception();
				}
				//OutputFileStructureController.getOutputFileStructure(outputFileStructureName,projectId);
				TypedQuery<OutputFileStructure> query = em.createNamedQuery("selectOutputFileStructure", OutputFileStructure.class);
				query.setParameter("outputFileStructureName", outputFileStructureName);
				query.setParameter("projectId", projectId);
				
				OutputFileStructure outputFileStructure = query.getSingleResult();
				
				outputFile.setOutputFileStructure(outputFileStructure);
				outputFile.setProject(project);
				
				em.persist(outputFile);
				em.merge(project);
				em.flush();
				em.getTransaction().commit();
			}
			catch(NoResultException e){
				em.getTransaction().rollback();
				throw new Exception();
			}
		}
	}
	
	public void createOutputFile(OutputFile outputFile, int projectId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			em.getTransaction().begin();
			
			Project project = em.find(Project.class, projectId);
			if(project == null){
				em.getTransaction().rollback();
				throw new Exception();
			}
			
			outputFile.setProject(project);
			
			em.persist(outputFile);
			em.merge(project);
			em.flush();
			em.getTransaction().commit();
		}
	}

}
