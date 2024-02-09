package dnse3.orchestration.jpa.model.simulation;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import dnse3.orchestration.jpa.model.project.Project;

@Entity
@Table(name="SINGLE_SIMULATION")
@DiscriminatorValue(value="S")
@PrimaryKeyJoinColumn(name="ID")
@NamedQueries({
	@NamedQuery(
		name="listSingleSimulations",
		query="SELECT s "+
				"FROM SingleSimulation s "+
				"WHERE s.parameterSweepSimulation IS NULL and "+
				"s.project.id = :projectId"),
	@NamedQuery(
		name = "listAllRemovingSingleSimulations",
		query = "SELECT s "+
				"FROM SingleSimulation s " +
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.REMOVING)"
				),
	@NamedQuery(
		name = "listAllCleaningSingleSimulations",
		query = "SELECT s " +
				"FROM SingleSimulation s " +
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.CLEANING)"
				),
	@NamedQuery(
		name = "listAllPausedSingleSweepSimulations",
		query = "SELECT s "+
				"FROM SingleSimulation s "+
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.PAUSED)"
				),
	@NamedQuery(
		name = "listAllReportingSingleSimulations",
		query = "SELECT s " +
				"FROM SingleSimulation s " +
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.REPORTING)"
				),
	@NamedQuery(
		name = "listAllProcessingSingleSimulations",
		query = "SELECT s " +
				"FROM SingleSimulation s " +
				"WHERE s.status IN (dnse3.orchestration.jpa.model.simulation.SimulationStatus.PROCESSING)"
				)
})
public class SingleSimulation extends Simulation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID")
	private Project project;
	
	@ManyToOne
	@JoinColumn(name="PARAMETER_SWEEP_ID")
	private ParameterSweepSimulation parameterSweepSimulation;
	
	/**
	 * Constructor de la clase.
	 */
	public SingleSimulation(){
		super();
	}
	
	/**
	 * Constructor de la clase. Asigna identificador a la nueva simulaci�n
	 * @param name Identificador de la simulaci�n
	 */
	public SingleSimulation(String name){
		super(name);
	}
	
	/**
	 * Constructor de la clase. Asigna identificador y n�mero de repeticiones a la nueva simulaci�n
	 * @param name Identificador de la simulaci�n
	 * @param numRepetitions N�mero de repeciones de la simulaci�n
	 */
	public SingleSimulation(String name, int numRepetitions, int priority){
		super(name, numRepetitions, priority);
	}

	/**
	 * M�todo para obtener un proyecto registrado en el sistema
	 * @return El objeto del proyecto almacenado
	 */
	public Project getProject() {
		return project;
	}

	/**
	 * Asigna proyecto a la simulaci�n
	 * @param project Proyecto al que va asignada la simulaci�n
	 */
	public void setProject(Project project) {
		this.project = project;
		if(!this.project.getSingleSimulations().contains(this))
			this.project.getSingleSimulations().add(this);
	}

	/**
	 * Recupera el identificador de la simulaci�n de barrido a la que pertenece
	 * @return Identificador de la simulaci�n de barrido a la que pertenece
	 */
	public ParameterSweepSimulation getParameterSweepSimulation() {
		return parameterSweepSimulation;
	}

	/**
	 * Establece el identificador de la simulaci�n de barrido a la que pertenece
	 * @param parameterSweepSimulation Identificador de la simulaci�n de barrido
	 */
	public void setParameterSweepSimulation(ParameterSweepSimulation parameterSweepSimulation) {
		this.parameterSweepSimulation = parameterSweepSimulation;
	}

	/**
	 * M�todo para obtener el n�mero de simulaciones que el usuario puede lanzar
	 * @return N�mero de simulaciones que el usuario puede lanzar
	 */
	public int getRemainingSimulations() {
		return getNumRepetitions() - getCompletedRepetitions().size();
	}
}
