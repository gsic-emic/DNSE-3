package dnse3.orchestration.jpa.controller.simulation;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.jpa.model.simulation.Parameter;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;

public class ParameterSweepSimulationController {

private EntityManager em = null;
    
    public ParameterSweepSimulationController(EntityManager em){
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
     * M�todo para obtener la lista de los par�metros de una simulaci�n de barrido
     * @param projectId Identificador del proyecto
     * @return Lista de los par�metros de la simulaci�n
     * @throws Exception Lanza una excepci�n cuando el proyecto no exista
     */
    public List<ParameterSweepSimulation> getParameterSweepSimulations(int projectId) throws Exception{
        synchronized (em) {
            Project project = em.find(Project.class, projectId);
            if(project == null) {
                throw new Exception("No hay ninguna simulaci�n para el proyecto");
            }
            return project.getParameterSweepSimulations();
        }
    }
    
    /**
     * M�todo para obtener una simulaci�n de barrido de par�metros del sistema
     * @param simulationId Identificador de la simulaci�n
     * @return Objeto que representa a la simulaci�n
     * @throws Exception Lanza una excepci�n cuando la simulaci�n no exista en el sistema
     */
    public ParameterSweepSimulation getParameterSweepSimulation(long simulationId) throws Exception{
        synchronized(em){
            ParameterSweepSimulation simulation = em.find(ParameterSweepSimulation.class, simulationId);
            if(simulation==null){
                //em.close();
                throw new Exception("La simulaci�n no est� registrada en el sistema");
            }
            //em.close();
            return simulation;
        }
    }
    
    /**
     * M�todo para crear una simulaci�n de tipo barrido de par�metros
     * @param simulation Identificador de la simulaci�n
     * @param projectId Identificador del proyecto
     * @throws Exception Lanza una excepci�n cuando el proyecto no exista en el sistema
     */
    public void createParameterSweepSimulation(ParameterSweepSimulation simulation, int projectId) throws Exception{ //Los par�metros se van asignando uno a uno
        synchronized (em) {
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            
            em.getTransaction().begin();
            
            Project project = em.find(Project.class, projectId);
            if(project == null){
                em.getTransaction().rollback();
                throw new Exception("El proyecto no existe en el sistema");
            }
            
            simulation.setProject(project);
            em.persist(simulation);
            for(Parameter p : simulation.getParameters())
                em.persist(p);
            em.merge(project);
            em.flush();
            em.getTransaction().commit();
        }
    }
    
    /**
     * M�todo para eliminar a una simulaci�n de un proyecto
     * @param simulation Identificador de la simulaci�n
     */
    public void deleteParameterSweepSimulation(ParameterSweepSimulation simulation){
        synchronized(em){
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            
            em.getTransaction().begin();
            if(!em.contains(simulation))
                em.merge(simulation);
            
            Project project = simulation.getProject();
            project.getParameterSweepSimulations().remove(simulation);
            
            em.remove(simulation);
            em.merge(project);
            em.flush();
            em.getTransaction().commit();
        }
    }
    
    /**
     * M�todo para actualizar las caracter�sticas de una simulaci�n de barrido de par�metros
     * @param simulation Simulaci�n a modificar
     * @throws Exception
     */
    public void updateParameterSweepSimulation(ParameterSweepSimulation simulation) throws Exception{
        synchronized(em){
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            
            simulation.setUpdateDate(new Date());
            
            em.getTransaction().begin();
            em.merge(simulation);
            em.lock(simulation, LockModeType.PESSIMISTIC_WRITE);
            em.flush();
            em.getTransaction().commit();
        }
    }
}
