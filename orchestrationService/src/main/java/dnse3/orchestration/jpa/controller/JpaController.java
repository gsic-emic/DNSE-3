package dnse3.orchestration.jpa.controller;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;

import dnse3.orchestration.jpa.controller.project.OutputFileController;
import dnse3.orchestration.jpa.controller.project.OutputFileStructureController;
import dnse3.orchestration.jpa.controller.project.ParameterDescriptionController;
import dnse3.orchestration.jpa.controller.project.ProjectController;
import dnse3.orchestration.jpa.controller.simulation.ParameterController;
import dnse3.orchestration.jpa.controller.simulation.ParameterSweepSimulationController;
import dnse3.orchestration.jpa.controller.simulation.SingleSimulationController;
import dnse3.orchestration.jpa.controller.user.UserController;
import dnse3.orchestration.jpa.model.project.Project;
import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.SimulationStatus;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;
import dnse3.orchestration.jpa.model.user.User;

public class JpaController {
	
	private UserController userController = null;
	private ProjectController projectController = null;
	private SingleSimulationController singleSimulationController = null;
	private ParameterSweepSimulationController parameterSweepSimulationController = null;
	private ParameterDescriptionController parameterDescriptionController = null;
	private ParameterController parameterController = null;
	private OutputFileStructureController outputFileStructureController = null;
	private OutputFileController outputFileController = null;
	
	
	private EntityManagerFactory emf;
	private EntityManager em;
	
	public JpaController(){
		emf = Persistence.createEntityManagerFactory("dnse3");// parameter must be synchronized with persistence-unit@name at META-INF/persistence.xml
		String JDBC_driver = (String)emf.getProperties().get("javax.persistence.jdbc.driver");
		try {
			Class.forName(JDBC_driver);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(JDBC_driver + " not found; access to Internal Registry will not be possible"); 
		}
		
		em = emf.createEntityManager();
		//Aquí irá la llamada a la búsqueda de trabajos que no estén en los estados WAITING o FINISHED
	}

	public void restartSystem(){
		System.out.println("Comienza la comprobación de la base de datos");
		getSingleSimulationController();
		getParameterSweepSimulationController();
		synchronized(em){
			int i = 0;
			try{ //Se finaliza la eliminación de los proyectos que se estaban eliminando
				TypedQuery<Project> query = em.createNamedQuery("getRemovigProjects", Project.class);
				List<Project> projects = query.getResultList();

				for(Project project : projects){
					project.setRemove(true);
				}
				try{
					Thread.sleep(500);
				}catch(InterruptedException e){}
			}catch(NoResultException e){
				System.out.println("No hay proyectos que eliminar");
			}

			try{ //Se finaliza la elminación de los barridos que se estaban eliminando
				TypedQuery<ParameterSweepSimulation> query = em.createNamedQuery("listAllRemovingParameterSweepSimulations", ParameterSweepSimulation.class);
				List<ParameterSweepSimulation> parameterSweepSimulations = query.getResultList();
				System.out.println("listAllRemovingParameterSweepSimulations " + parameterSweepSimulations.size());
				i=0;
				for(ParameterSweepSimulation parameterSweepSimulation : parameterSweepSimulations){
					parameterSweepSimulationController.deleteParameterSweepSimulation(parameterSweepSimulation);
					System.out.println("Borrado "+(i++));
				}
				try{
					Thread.sleep(500);
				}catch(InterruptedException e){}
			}catch(Exception e) {e.printStackTrace();}

			try{ //Se finaliza la detección de las simulaciones de barrido
				TypedQuery<ParameterSweepSimulation> query = em.createNamedQuery("listAllCleaningParameterSweepSimualtions", ParameterSweepSimulation.class);
				List<ParameterSweepSimulation> parameterSweepSimulations = query.getResultList();
				System.out.println("listAllCleaningParameterSweepSimualtions " + parameterSweepSimulations.size());
				i=0;
				for(ParameterSweepSimulation parameterSweepSimulation : parameterSweepSimulations){
					parameterSweepSimulation.setStatus(SimulationStatus.WAITING);
					getParameterSweepSimulationController().updateParameterSweepSimulation(parameterSweepSimulation);
					System.out.println("Actualizado "+(i++));
				}
				try{
					Thread.sleep(500);
				} catch(InterruptedException e){}
			} catch (Exception e) {e.printStackTrace();}

			try{ //Se resetean las simulaciones pausadas
				TypedQuery<ParameterSweepSimulation> query = em.createNamedQuery("listAllPausedParameterSweepSimulations", ParameterSweepSimulation.class);
				List<ParameterSweepSimulation> parameterSweepSimulations = query.getResultList();
				System.out.println("listAllPausedParameterSweepSimulations " + parameterSweepSimulations.size());
				i=0;
				for(ParameterSweepSimulation parameterSweepSimulation : parameterSweepSimulations){
					parameterSweepSimulation.setStatus(SimulationStatus.WAITING);
					getParameterSweepSimulationController().updateParameterSweepSimulation(parameterSweepSimulation);
					System.out.println("Actualizado "+(i++));
				}
				try{
					Thread.sleep(500);
				} catch(InterruptedException e){}
			} catch (Exception e) {e.printStackTrace();}

			try{ //Se resetean las simulaciones reporting
				TypedQuery<ParameterSweepSimulation> query = em.createNamedQuery("listAllReportingParameterSweepSimulations", ParameterSweepSimulation.class);
				List<ParameterSweepSimulation> parameterSweepSimulations = query.getResultList();
				System.out.println("listAllReportingParameterSweepSimulations " + parameterSweepSimulations.size());
				i=0;
				for(ParameterSweepSimulation parameterSweepSimulation : parameterSweepSimulations){
					parameterSweepSimulation.setStatus(SimulationStatus.WAITING);
					getParameterSweepSimulationController().updateParameterSweepSimulation(parameterSweepSimulation);
					System.out.println("Actualizado "+(i++));
				}
				try{
					Thread.sleep(500);
				} catch(InterruptedException e){}
			} catch(Exception e){e.printStackTrace();}

			try{ //Se resetean las simulaciones en ejecución
				TypedQuery<ParameterSweepSimulation> query = em.createNamedQuery("listAllProcessingParameterSweepSimulations", ParameterSweepSimulation.class);
				List<ParameterSweepSimulation> parameterSweepSimulations = query.getResultList();
				System.out.println("listAllProcessingParameterSweepSimulations " + parameterSweepSimulations.size());
				i=0;
				for(ParameterSweepSimulation parameterSweepSimulation : parameterSweepSimulations){
					parameterSweepSimulation.setStatus(SimulationStatus.WAITING);
					getParameterSweepSimulationController().updateParameterSweepSimulation(parameterSweepSimulation);
					System.out.println("Actualizado "+(i++));
				}
				try{
					Thread.sleep(500);
				} catch(InterruptedException e){}
			} catch(Exception e){e.printStackTrace();}

			
			try{ //Se finaliza la eliminanción de las simulaciones individuales que se estaban eliminando
				TypedQuery<SingleSimulation> query = em.createNamedQuery("listAllRemovingSingleSimulations", SingleSimulation.class);
				List<SingleSimulation> singleSimulations = query.getResultList();
				System.out.println("listAllRemovingSingleSimulations " + singleSimulations.size());
				i=0;
				for(SingleSimulation singleSimulation : singleSimulations){
					singleSimulationController.deleteSingleSimulation(singleSimulation);
					System.out.println("Borrado "+(i++));
				}
				try{
					Thread.sleep(500);
				}catch(InterruptedException e){}
			}catch(NoResultException e){
				System.out.println("No hay simulaciones individuales que eliminar");
			}

			try{ //Se finaliza la detección de las simulaciones individuales
				TypedQuery<SingleSimulation> query = em.createNamedQuery("listAllCleaningSingleSimulations", SingleSimulation.class);
				List<SingleSimulation> singleSimulations = query.getResultList();
				System.out.println("listAllCleaningSingleSimulations " + singleSimulations.size());
				i=0;
				for(SingleSimulation singleSimulation : singleSimulations){
					singleSimulation.setStatus(SimulationStatus.WAITING);
					getSingleSimulationController().updateSingleSimulation(singleSimulation);
					System.out.println("Actualizado "+(i++));
				}
				try{
					Thread.sleep(500);
				} catch(InterruptedException e){}
			} catch(NoResultException e){
				System.out.println("No hay simulaciones de individuales en CLEANING");
			} catch (Exception e) {e.printStackTrace();}

			try{ //Se resetean las simulaciones individuales pausadas
				TypedQuery<SingleSimulation> query = em.createNamedQuery("listAllPausedSingleSweepSimulations", SingleSimulation.class);
				List<SingleSimulation> singleSimulations = query.getResultList();
				System.out.println("listAllPausedSingleSweepSimulations " + singleSimulations.size());
				i=0;
				for(SingleSimulation singleSimulation : singleSimulations){
					singleSimulation.setStatus(SimulationStatus.WAITING);
					getSingleSimulationController().updateSingleSimulation(singleSimulation);
					System.out.println("Actualizado "+(i++));
				}
				try{
					Thread.sleep(500);
				} catch(InterruptedException e){}
			} catch (Exception e) {e.printStackTrace();}

			try{ //Se resetean las simulaciones individuales reporting
				TypedQuery<SingleSimulation> query = em.createNamedQuery("listAllReportingSingleSimulations", SingleSimulation.class);
				List<SingleSimulation> singleSimulations = query.getResultList();
				System.out.println("listAllReportingSingleSimulations " + singleSimulations.size());
				i=0;
				for(SingleSimulation singleSimulation : singleSimulations){
					singleSimulation.setStatus(SimulationStatus.WAITING);
					getSingleSimulationController().updateSingleSimulation(singleSimulation);
					System.out.println("Actualizado "+(i++));
				}
				try{
					Thread.sleep(500);
				} catch(InterruptedException e){}
			} catch(Exception e){e.printStackTrace();}

			try{ //Se resetean las simulaciones individuales en ejecución
				TypedQuery<SingleSimulation> query = em.createNamedQuery("listAllProcessingSingleSimulations", SingleSimulation.class);
				List<SingleSimulation> singleSimulations = query.getResultList();
				System.out.println("listAllProcessingSingleSimulations " + singleSimulations.size());
				i=0;
				for(SingleSimulation singleSimulation : singleSimulations){
					singleSimulation.setStatus(SimulationStatus.WAITING);
					getSingleSimulationController().updateSingleSimulation(singleSimulation);
					System.out.println("Actualizado "+(i++));
				}
				try{
					Thread.sleep(500);
				} catch(InterruptedException e){}
			} catch(Exception e){e.printStackTrace();}
			
		}
		System.out.println("Finaliza la comprobación de la base de datos");
	}
	
	public boolean testConnection(){
		//EntityManager em = emf.createEntityManager();
		synchronized(em){
			//DNSE3OrchestrationLogger.getLogger().debug("JPA - Test Connection");
			if(em.getTransaction().isActive()){
				//DNSE3OrchestrationLogger.getLogger().warn("JPA - Transaction open since a previous operation.");
				em.getTransaction().rollback();
			}
			try{
				em.getTransaction().begin();
				TypedQuery<User> query = em.createNamedQuery("listUsers", User.class);
		        query.getFirstResult();
				em.getTransaction().commit();
				//DNSE3OrchestrationLogger.getLogger().debug("JPA - Successful connection");
			}catch(PersistenceException e){
				//DNSE3OrchestrationLogger.getLogger().warn("JPA - Connection failed. Attempting to retry");
				EntityManager newEm = emf.createEntityManager();
				try{
					newEm.getTransaction().begin();
					TypedQuery<User> query = em.createNamedQuery("listUsers", User.class);
			        query.getFirstResult();
			        newEm.getTransaction().commit();
			        //DNSE3OrchestrationLogger.getLogger().debug("JPA - New connection succesful. Configuring controllers");
			        synchronized(newEm){
			        	if(projectController==null)
			        		projectController=new ProjectController(newEm);
			        	else
			        		projectController.setEntityManager(newEm);
			        	
			        	if(outputFileController==null)
			        		outputFileController=new OutputFileController(newEm);
			        	else
			        		outputFileController.setEntityManager(newEm);
			        	
			    		if(outputFileStructureController==null)
			    			outputFileStructureController=new OutputFileStructureController(newEm);
			    		else
			    			outputFileStructureController.setEntityManager(newEm);
			    		
			    		if(parameterController==null)
			    			parameterController=new ParameterController(newEm);
			    		else
			    			parameterController.setEntityManager(newEm);
			    		
			    		if(parameterDescriptionController==null)
			    			parameterDescriptionController= new ParameterDescriptionController(newEm);
			    		else
			    			parameterDescriptionController.setEntityManager(newEm);
			    		if(parameterSweepSimulationController==null)
			    			parameterSweepSimulationController= new ParameterSweepSimulationController(newEm);
			    		else
			    			parameterSweepSimulationController.setEntityManager(newEm);
			    		
			    		if(singleSimulationController==null)
			    			singleSimulationController= new SingleSimulationController(newEm);
			    		else
			    			singleSimulationController.setEntityManager(newEm);
			    		
			    		if(userController==null)
			    			userController=new UserController(newEm);
			    		else
			    			userController.setEntityManager(newEm);
			    		
			    		em=newEm;
			        }
			        return true;
				}
				catch(PersistenceException ee){
					//DNSE3OrchestrationLogger.getLogger().error("JPA - Failed connection to Database. Check the database status");
					if(em.getTransaction().isActive()){
						em.getTransaction().rollback();
					}
					return false;
				}
			}
			return true;
		}
	}
	
	public void closeEntityManagers() {
		em.close();
		emf.close();

		projectController=null;
		outputFileController=null;
		outputFileStructureController=null;
		parameterController=null;
		parameterDescriptionController=null;
		parameterSweepSimulationController=null;
		singleSimulationController=null;
		userController=null;
	}
	
	public UserController getUserController(){
		if(userController==null)
			userController=new UserController(em);
		return userController;
	}
	
	public ProjectController getProjectController(){
		if(projectController==null)
			projectController= new ProjectController(em);
		return projectController;
	}
	
	public SingleSimulationController getSingleSimulationController(){
		if(singleSimulationController==null)
			singleSimulationController=new SingleSimulationController(em);
		return singleSimulationController;
	}
	
	public ParameterSweepSimulationController getParameterSweepSimulationController(){
		if(parameterSweepSimulationController==null)
			parameterSweepSimulationController=new ParameterSweepSimulationController(em);
		return parameterSweepSimulationController;
	}
	
	public ParameterDescriptionController getParameterDescriptionController(){
		if(parameterDescriptionController==null)
			parameterDescriptionController=new ParameterDescriptionController(em);
		return parameterDescriptionController;
	}
	
	public ParameterController getParameterController(){
		if(parameterController==null)
			parameterController=new ParameterController(em);
		return parameterController;
	}
	
	public OutputFileStructureController getOutputFileStructureController(){
		if(outputFileStructureController==null)
			outputFileStructureController=new OutputFileStructureController(em);
		return outputFileStructureController;
	}
	
	public OutputFileController getOutputFileController(){
		if(outputFileController==null)
			outputFileController=new OutputFileController(em);
		return outputFileController;
	}

}
