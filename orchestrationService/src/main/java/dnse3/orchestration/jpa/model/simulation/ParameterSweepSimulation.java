package dnse3.orchestration.jpa.model.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import dnse3.orchestration.jpa.model.project.OutputFile;
import dnse3.orchestration.jpa.model.project.Project;

@Entity
@Table(name="PARAMETER_SWEEP_SIMULATION")
@DiscriminatorValue(value="P")
@PrimaryKeyJoinColumn(name="ID")
@NamedQueries({
	/*@NamedQuery(
			name="listParameterSweepSimulations",
			query="SELECT p "+
					"FROM ParameterSweepSimulation p "+
					"WHERE p. IS NULL AND "+
					"p.project.id = :projectId")*/
	@NamedQuery(
		name = "listAllRemovingParameterSweepSimulations",
		query = "SELECT s "+
				"FROM ParameterSweepSimulation s " +
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.REMOVING)"
				),
	@NamedQuery(
		name = "listAllCleaningParameterSweepSimualtions",
		query = "SELECT s " +
				"FROM ParameterSweepSimulation s " +
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.CLEANING)"
				),
	@NamedQuery(
		name = "listAllPausedParameterSweepSimulations",
		query = "SELECT s "+
				"FROM ParameterSweepSimulation s "+
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.PAUSED)"
				),
	@NamedQuery(
		name = "listAllReportingParameterSweepSimulations",
		query = "SELECT s " +
				"FROM ParameterSweepSimulation s " +
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.REPORTING)"
				),
	@NamedQuery(
		name = "listAllProcessingParameterSweepSimulations",
		query = "SELECT s " +
				"FROM ParameterSweepSimulation s " +
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.PROCESSING)"
				)
})
public class ParameterSweepSimulation extends Simulation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID")
	private Project project;
	
	@OneToMany(mappedBy="parameterSweepSimulation",fetch=FetchType.EAGER, cascade=CascadeType.ALL)
	@OrderBy
	private List<SingleSimulation> singleSimulations;
	
	/**
	 * Construcctor de la clase.
	 */
	public ParameterSweepSimulation(){
		super();
		this.singleSimulations = new ArrayList<>();
	}
	
	/**
	 * Construcctor de la clase. Se le indica nombre
	 * @param name Nombre de la simulación
	 */
	public ParameterSweepSimulation(String name){
		super(name);
		this.singleSimulations = new ArrayList<>();
	}
	
	/**
	 * Construcctor de la clase. Se le indica nombre y número de repeticiones de la simulación
	 * @param name Nombre de la simulación
	 * @param numRepetitions Número de repeticiones de la simulación
	 * @param priority Prioridad de la simulación
	 */
	public ParameterSweepSimulation(String name, int numRepetitions, int priority){
		super(name,numRepetitions,priority);
		this.singleSimulations = new ArrayList<>();
	}

	/**
	 * Método para recuperar un proyecto del sistema
	 * @return El objeto que representa al proyecto en el sistema
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Método para establecer el proyecto de la simulación
	 * @param project Proyecto al que se desea que pertenezca la simulación
	 */
	public void setProject(Project project) {
		this.project = project;
		if(!this.project.getParameterSweepSimulations().contains(this))
			this.project.getParameterSweepSimulations().add(this);
	}

	/**
	 * Método para obtener el listado de simulaciones individuales que pertenecen al barrido
	 * @return Lista de simulaciones individuales que pertenecen al barrido
	 */
	public List<SingleSimulation> getSingleSimulations() {
		return singleSimulations;
	}

	/**
	 * Método para incluir una lista de simulaciones individuales en el barrido
	 * @param singleSimulations Simulaciones individuales a incluir
	 */
	public void setSingleSimulations(List<SingleSimulation> singleSimulations) {
		this.singleSimulations = singleSimulations;
	}

	/**
	 * Método para recuperar el número de simulaciones pendientes de realizarse
	 * @return Número de simulaciones pendientes en el barrido
	 */
	public int getRemainingSimulations() {
		if(this.singleSimulations.isEmpty()){
			List<HashSet<Parameter>> parameters = new ArrayList<>();
			for(Parameter p : getParameters())
				p.explode(parameters);
			return parameters.size()*getNumRepetitions();
		}
		else{
			int remaining = 0;
			for(SingleSimulation s : this.singleSimulations){
				remaining +=s.getRemainingSimulations();
			}
			return remaining;
		}
	}

	/**
	 * Método para crear una simulación individual y agregarla a la lista de simulaciones individuales del barrido
	 */
	public void createSingleSimulations() {
		if(this.singleSimulations.isEmpty()){
			List<HashSet<Parameter>> parameters = new ArrayList<>();
			for(Parameter p : getParameters())
				p.explode(parameters);
			
			//int i=0;
			for(HashSet<Parameter> p : parameters){
				System.out.println(singleSimulations.size());
				SingleSimulation s = new SingleSimulation(String.valueOf((singleSimulations.size())*getNumRepetitions()), getNumRepetitions(), getPriority());
				
				for(OutputFile o : getOutputFiles())
					s.addOutputFile(o);
				for(Parameter pp : p)
					s.addParameter(pp);
				
				s.setParameterSweepSimulation(this);
				s.setStatus(SimulationStatus.PROCESSING);
				this.singleSimulations.add(s);
			}
		}
	}
}
