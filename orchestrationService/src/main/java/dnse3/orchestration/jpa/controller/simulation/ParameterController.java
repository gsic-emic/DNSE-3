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
	 * Método para obtener la lista de parámetros de la simulación
	 * @param simulationId Identificador de la simulación en el sistema
	 * @return Lista de parámetros de la simulación
	 * @throws Exception Lanza una excepción cuando la simulación no existe en el sistema
	 */
	public List<Parameter> getParameters(long simulationId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			Simulation simulation = em.find(Simulation.class, simulationId);
			if(simulation == null)
				throw new Exception("La simulación no existe en el sistema");
			
			return simulation.getParameters();
		}
	}
	
	/**
	 * Método para obtener un parámetro de una simulación
	 * @param parameterName Identificador del parámetro
	 * @param projectId Idetntificador del proyecto
	 * @param simulationId Identificador de la simulación
	 * @return Devuelve un objeto del parámetro consultado
	 * @throws Exception Lanza una excepción cuando el parámetro no se encuentra en la simulación del 
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
				throw new Exception("Parámetro no encontrado en "+projectId);
			}
		}
	}
	
	/**
	 * Método para agregar un parámetro a una simulación
	 * @param parameter Parámetro a agregar a la simulación
	 * @param parameterName Identificador del parámetro
	 * @param projectId Identificador del proyecto
	 * @param simulationId Identificador de la simulacion
	 * @throws Exception Lanza una excepción cuando la simulación o el proyecto no existen
	 */
	public void createParameter(Parameter parameter, String parameterName, int projectId, long simulationId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			try{				
				Simulation simulation = em.find(Simulation.class, simulationId);
				if(simulation == null)
					throw new Exception("La simulación no existe en el proyecto");
				
				//Ya está en otro método
				TypedQuery<ParameterDescription> query = em.createNamedQuery("selectParameterDescription", ParameterDescription.class);
				query.setParameter("parameterName", parameterName);
				query.setParameter("projectId", projectId);
				ParameterDescription parameterDescription = query.getSingleResult();
				
				em.getTransaction().begin();
				
				simulation.addParameter(parameter);
				parameter.setParameterDescription(parameterDescription);
				
				em.persist(parameter); //Necesito comprobar que no tenga ya el parámetro la simulación
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
	 * Método para eliminar los parámetros de una simulación
	 * @param simulationId Identificador de la simulación
	 * @throws Exception Lanza una excepción si la simulación no existe en el sistema
	 */
	public void deleteParameters(long simulationId) throws Exception{
		synchronized(em){
			if(em.getTransaction().isActive())
				em.getTransaction().rollback();
			Simulation simulation = em.find(Simulation.class, simulationId);
			if(simulation == null)
				throw new Exception("La simulación indicada no existe en el sistema");
			
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
	 * Método para borrar un parámetro de una simulación
	 * @param parameter Parámetro de la simulación a eliminar
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
	 * Método para eliminar un parámetro de una simulación
	 * @param parameterName Identificador del parámetro
	 * @param projectId Identificador del proyecto
	 * @param simulationId Identificador de la simulación
	 * @throws Exception Lanza ua excepción cuando el parámetro no existe la coincidencia 
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
				throw new Exception("El parámetro no está registrado en la simulación "+simulationId+" del proyecto "+projectId);
			}
		}
	}

	/**
	 * Método para actualizar un parámetro
	 * @param parameter Parámetro actualizado
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
