package dnse3.orchestration.jpa.controller.simulation;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import dnse3.orchestration.jpa.model.project.ParameterDescription;
import dnse3.orchestration.jpa.model.simulation.Parameter;
import dnse3.orchestration.jpa.model.simulation.Simulation;

public class ParameterController {
	
	private EntityManager em = null;
	
	public ParameterController(EntityManager em){
		if(em == null)
			throw new IllegalArgumentException("em : Entity manager can't be null");
		this.em = em;
	}
	
	public void setEntityManager(EntityManager em){
		if(em == null)
			throw new IllegalArgumentException("em : Entity manager can't be null");
		this.em = em;
	}
	
	/**
	 * M�todo para obtener la lista de par�metros de la simulaci�n
	 * @param simulationId Identificador de la simulaci�n en el sistema
	 * @return Lista de par�metros de la simulaci�n
	 * @throws Exception Lanza una excepci�n cuando la simulaci�n no existe en el sistema
	 */
	public List<Parameter> getParameters(long simulationId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			Simulation simulation = em.find(Simulation.class, simulationId);
			if(simulation == null)
				throw new Exception("La simulaci�n no existe en el sistema");
			
			return simulation.getParameters();
		}
	}
	
	/**
	 * M�todo para obtener un par�metro de una simulaci�n
	 * @param parameterName Identificador del par�metro
	 * @param projectId Idetntificador del proyecto
	 * @param simulationId Identificador de la simulaci�n
	 * @return Devuelve un objeto del par�metro consultado
	 * @throws Exception Lanza una excepci�n cuando el par�metro no se encuentra en la simulaci�n del 
	 * proyecto consultado
	 */
	public Parameter getParameter(String parameterName, int projectId, long simulationId) throws Exception{
		synchronized (em) {
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			try{
				TypedQuery<Parameter> query = em.createNamedQuery("selectParameter", Parameter.class);
				query.setParameter("parameterName", parameterName);
				query.setParameter("projectId", projectId);
				query.setParameter("simulationId", simulationId);
				
				return query.getSingleResult();
			}
			catch(NoResultException e){
				throw new Exception("Par�metro no encontrado en "+projectId);
			}
		}
	}
	
	/**
	 * M�todo para agregar un par�metro a una simulaci�n
	 * @param parameter Par�metro a agregar a la simulaci�n
	 * @param parameterName Identificador del par�metro
	 * @param projectId Identificador del proyecto
	 * @param simulationId Identificador de la simulacion
	 * @throws Exception Lanza una excepci�n cuando la simulaci�n o el proyecto no existen
	 */
	public void createParameter(Parameter parameter, String parameterName, int projectId, long simulationId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			try{				
				Simulation simulation = em.find(Simulation.class, simulationId);
				if(simulation == null)
					throw new Exception("La simulaci�n no existe en el proyecto");
				
				//Ya est� en otro m�todo
				TypedQuery<ParameterDescription> query = em.createNamedQuery("selectParameterDescription", ParameterDescription.class);
				query.setParameter("parameterName", parameterName);
				query.setParameter("projectId", projectId);
				ParameterDescription parameterDescription = query.getSingleResult();
				
				em.getTransaction().begin();
				
				simulation.addParameter(parameter);
				parameter.setParameterDescription(parameterDescription);
				
				em.persist(parameter); //Necesito comprobar que no tenga ya el par�metro la simulaci�n
				em.merge(simulation);
				em.flush();
				em.getTransaction().commit();
			}
			catch(NoResultException|EntityExistsException e){
				if(em.getTransaction().isActive())
					em.getTransaction().rollback();
				throw new Exception(e.toString());
			}
		}
	}
	
	/**
	 * M�todo para eliminar los par�metros de una simulaci�n
	 * @param simulationId Identificador de la simulaci�n
	 * @throws Exception Lanza una excepci�n si la simulaci�n no existe en el sistema
	 */
	public void deleteParameters(long simulationId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			Simulation simulation = em.find(Simulation.class, simulationId);
			if(simulation == null)
				throw new Exception("La simulaci�n indicada no existe en el sistema");
			
			em.getTransaction().begin();
			
			for(Parameter p : simulation.getParameters())
				em.remove(p);
			simulation.getParameters().clear();
			
			em.merge(simulation);
			em.flush();
			em.getTransaction().commit();
		}
	}
	
	/**
	 * M�todo para borrar un par�metro de una simulaci�n
	 * @param parameter Par�metro de la simulaci�n a eliminar
	 */
	public void deleteParameter(Parameter parameter){
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			em.getTransaction().begin();
			if(!em.contains(parameter))
				em.merge(parameter);
			
			Simulation simulation = parameter.getSimulation();
			simulation.getParameters().remove(parameter);
			
			em.remove(parameter);
			em.merge(simulation);
			em.flush();
			em.getTransaction().commit();
		}
	}
	
	/**
	 * M�todo para eliminar un par�metro de una simulaci�n
	 * @param parameterName Identificador del par�metro
	 * @param projectId Identificador del proyecto
	 * @param simulationId Identificador de la simulaci�n
	 * @throws Exception Lanza ua excepci�n cuando el par�metro no existe la coincidencia 
	 * parametro/simulacion/proyecto
	 */
	public void deleteParameter(String parameterName, int projectId, long simulationId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			try{
				em.getTransaction().begin();
				
				TypedQuery<Parameter> query = em.createNamedQuery("selectParameter", Parameter.class);
				query.setParameter("parameterName", parameterName);
				query.setParameter("projectId", projectId);
				query.setParameter("simulationId", simulationId);
				
				Parameter parameter = query.getSingleResult();
				
				Simulation simulation = parameter.getSimulation();
				simulation.getParameters().remove(parameter);
				
				em.remove(parameter);
				em.merge(simulation);
				em.flush();
				em.getTransaction().commit();
			}
			catch(NoResultException | NonUniqueResultException e){
				if(em.getTransaction().isActive())
					em.getTransaction().rollback();
				throw new Exception("El par�metro no est� registrado en la simulaci�n "+simulationId+" del proyecto "+projectId);
			}
		}
	}

	/**
	 * M�todo para actualizar un par�metro
	 * @param parameter Par�metro actualizado
	 * @throws Exception
	 */
	public void updateParameter(Parameter parameter) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			parameter.getSimulation().setUpdateDate(new Date());

			em.getTransaction().begin();
			em.merge(parameter);
			em.lock(parameter, LockModeType.PESSIMISTIC_WRITE);
			em.flush();
			em.getTransaction().commit();
		}
	}

}
