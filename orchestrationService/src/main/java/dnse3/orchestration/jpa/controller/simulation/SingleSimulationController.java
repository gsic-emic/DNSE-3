package dnse3.orchestration.jpa.controller.simulation;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.jpa.model.simulation.Parameter;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;

public class SingleSimulationController {

    private EntityManager em = null;
    
    public SingleSimulationController(EntityManager em){
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
     * M�todo para recuperar las simulaciones individuales de un proyecto
     * @param projectId Identificador del proyecto
     * @return Listado de objetos con las simulaciones individuales
     * @throws Exception Lanza una excepci�n cuando el proyecto no existe en el sistema
     */
    public List<SingleSimulation> getSingleSimulations(int projectId) throws Exception{
        synchronized (em) {
            Project project = em.find(Project.class, projectId);
            if(project == null)
                throw new Exception("El proyecto no existe en el sistema");
            //�Se puede hacer que el proyecto s�lo tenga sus simulaciones directas y no las indirectas?
            TypedQuery<SingleSimulation> query = em.createNamedQuery("listSingleSimulations", SingleSimulation.class);
            query.setParameter("projectId", projectId);
            
            return query.getResultList();
        }
    }
    
    /**
     * M�todo para recuperar una simulaci�n del sistema
     * @param simulationId Identificador de la simulaci�n en el sistema
     * @return Devuelve la simulaci�n como un objeto
     * @throws Exception Lanza una excepci�n cuando la simulaci�n no exista en el sistema
     */
    public SingleSimulation getSingleSimulation(long simulationId) throws Exception{
        synchronized(em){
            SingleSimulation simulation = em.find(SingleSimulation.class, simulationId);
            if(simulation==null){
                //em.close();
                throw new Exception("Simulaci�n no encontrada en el sistema");
            }
            //em.close();
            return simulation;
        }
    }
    
    public void createSingleSimulation(SingleSimulation simulation, int projectId) throws Exception{ //Los par�metros se van asignando uno a uno
        synchronized (em) {
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            
            em.getTransaction().begin();
            
            Project project = em.find(Project.class, projectId);
            if(project == null){
                em.getTransaction().rollback();
                throw new Exception();
            }

            simulation.setProject(project);
            em.persist(simulation);
            for(Parameter p : simulation.getParameters()) {
                em.persist(p);
            }
            em.merge(project);
            em.flush();
            em.getTransaction().commit();
        }
    }
    
    /**
     * M�todo par acutalizar el estado de la simulaci�n
     * @param simulation Simulaci�n a actualizar
     * @throws Exception
     */
    public void updateSingleSimulation(SingleSimulation simulation) throws Exception{
        synchronized(em){
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            
            simulation.setUpdateDate(new Date());
            if(simulation.getParameterSweepSimulation()!=null)
                simulation.getParameterSweepSimulation().setUpdateDate(new Date());
            
            em.getTransaction().begin();
            em.merge(simulation);
            em.lock(simulation, LockModeType.PESSIMISTIC_WRITE);
            em.flush();
            em.getTransaction().commit();
        }
    }
    
    /**
     * M�todo para eliminar una simulaci�n
     * @param simulation Objeto que identifica la simulaci�n individual
     */
    public void deleteSingleSimulation(SingleSimulation simulation){
        synchronized (em) {
            if(em.getTransaction().isActive())
                em.getTransaction().rollback();
            em.getTransaction().begin();
            if(!em.contains(simulation))
                em.merge(simulation);
            Project project = simulation.getProject();
            project.getSingleSimulations().remove(simulation);
            
            em.remove(simulation);
            em.merge(project);
            em.flush();
            em.getTransaction().commit();
        }
    }
}
