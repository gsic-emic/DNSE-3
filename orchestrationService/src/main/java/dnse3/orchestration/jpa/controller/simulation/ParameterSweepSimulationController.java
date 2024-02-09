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
     * Método para obtener la lista de los parámetros de una simulación de barrido
     * @param projectId Identificador del proyecto
     * @return Lista de los parámetros de la simulación
     * @throws Exception Lanza una excepción cuando el proyecto no exista
     */
    public List<ParameterSweepSimulation> getParameterSweepSimulations(int projectId) throws Exception{
        synchronized (em) {
            Project project = em.find(Project.class, projectId);
            if(project == null) {
                throw new Exception("No hay ninguna simulación para el proyecto");
            }
            return project.getParameterSweepSimulations();
        }
    }
    
    /**
     * Método para obtener una simulación de barrido de parámetros del sistema
     * @param simulationId Identificador de la simulación
     * @return Objeto que representa a la simulación
     * @throws Exception Lanza una excepción cuando la simulación no exista en el sistema
     */
    public ParameterSweepSimulation getParameterSweepSimulation(long simulationId) throws Exception{
        synchronized(em){
            ParameterSweepSimulation simulation = em.find(ParameterSweepSimulation.class, simulationId);
            if(simulation==null){
                //em.close();
                throw new Exception("La simulación no está registrada en el sistema");
            }
            //em.close();
            return simulation;
        }
    }
    
    /**
     * Método para crear una simulación de tipo barrido de parámetros
     * @param simulation Identificador de la simulación
     * @param projectId Identificador del proyecto
     * @throws Exception Lanza una excepción cuando el proyecto no exista en el sistema
     */
    public void createParameterSweepSimulation(ParameterSweepSimulation simulation, int projectId) throws Exception{ //Los parámetros se van asignando uno a uno
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
     * Método para eliminar a una simulación de un proyecto
     * @param simulation Identificador de la simulación
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
     * Método para actualizar las características de una simulación de barrido de parámetros
     * @param simulation Simulación a modificar
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
