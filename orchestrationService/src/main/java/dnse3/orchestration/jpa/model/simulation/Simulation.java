package dnse3.orchestration.jpa.model.simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import dnse3.orchestration.jpa.model.project.OutputFile;

@Entity
@Table(name="SIMULATION")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="TYPE",discriminatorType=DiscriminatorType.STRING, length=1)
public abstract class Simulation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name="ID")
	private long id;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="STATUS")
	private SimulationStatus status;
	
	@Column(name="NUM_REPETITIONS")
	private int numRepetitions;

	@Column(name="PRIORITY")
	private int priority;
	
	@OneToMany(mappedBy="simulation", fetch=FetchType.LAZY, cascade=CascadeType.REMOVE)
	@OrderBy
	private List<Parameter> parameters;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@OrderBy
	private List<OutputFile> outputFiles;
	
	@Column(name="CREATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;
	
	@Column(name="UPDTE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
	@Column(name="START_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;
	
	@Column(name="FINISH_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date finishDate;
	
	@Column(name="TOTAL_TIME")
	private long totalTime;
	
	@ElementCollection
	@Column(name="NUMBER_REPETITION")
	private Set<Integer> completedRepetitions;
	
	/**
	 * Construcctor de la clase
	 */
	public Simulation(){
		//JPA
	}
	
	/**
	 * Constructor de la clase. Crea la simulaci�n asignado el nombre
	 * @param name Nombre de la simulaci�n
	 */
	public Simulation(String name){
		this.name = name;
		
		this.numRepetitions=1;
		this.priority = 50;
		this.totalTime=0;
		
		this.parameters= new ArrayList<>();
		this.outputFiles= new ArrayList<>();
		this.completedRepetitions = new HashSet<>();
		
		this.creationDate=new Date();
		this.updateDate= new Date();
		
		this.status=SimulationStatus.WAITING;
	}
	
	/**
	 * Constructor de la clase. Crea la simulaci�n asignado el nombre y n�mero
	 * de repeticion que se le pase por par�metros.
	 * @param name Nombre de la simulaci�n
	 * @param numRepetitions N�mero de repeticiones
	 * @param priority Prioridad de la simulaci�n
	 */
	public Simulation(String name, int numRepetitions, int priority){
		this.name = name;
		
		this.numRepetitions = numRepetitions;
		this.priority = priority;
		this.totalTime = 0;
		
		this.parameters = new ArrayList<>();
		this.outputFiles = new ArrayList<>();
		this.completedRepetitions = new HashSet<>();
		
		this.creationDate = new Date();
		this.updateDate = new Date();
		
		this.status = SimulationStatus.WAITING;
	}

	/**
	 * Recupera el identificador de la simulaci�n en la BBDD
	 * @return Identificador de la simulaci�n
	 */
	public long getId() {
		return id;
	}

	/**
	 * Establece el identificador de la simulaci�n en la BBDD
	 * @param id Identificador que se le va a asignar a la simulaci�n
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Recupera el nombre de la simulaci�n
	 * @return Nombre de la simulaci�n
	 */
	public String getName() {
		return name;
	}

	/**
	 * Establece el nombre de la simulaci�n
	 * @param name Nombre de la simulaci�n
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Recupera el estado de la simulaci�n
	 * @return Estado de la simulaci�n
	 */
	public SimulationStatus getStatus() {
		return status;
	}

	/**
	 * Establece el estado de la simulaci�n
	 * @param status Estado deseado de la simulaci�n
	 */
	public void setStatus(SimulationStatus status) {
		this.status = status;
	}

	/**
	 * Recupera el n�mero de repeticiones de la simulaci�n
	 * @return N�mero de simulaciones de la simulaci�n
	 */
	public int getNumRepetitions() {
		return numRepetitions;
	}

	/**
	 * Establece el n�mero de repeticiones de la simulaci�n
	 * @param numRepetitions N�mero de simulaciones desado de la simulaci�n
	 */
	public void setNumRepetitions(int numRepetitions) {
		this.numRepetitions = numRepetitions;
	}

	/**
	 * Recupera la prioridad de la simulaci�n
	 * @return Valor de la prioridad
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Establece la prioridad de la simulaci�n
	 * @param priority Prioridad de la simulaci�n
	 */
	public void setPriority(int priority){
		this.priority = priority;
	}

	/**
	 * Recupera la lista de par�metros de la simulaci�n 
	 * @return Lista de par�metros de la simulaci�n
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * Establece la lista de par�metros de la simulaci�n
	 * @param parameters Par�metros deseados de la simulaci�n
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Recupera la lista de ficheros de salida de la simulaci�n
	 * @return Lista de ficheros de salida de la simulaci�n
	 */
	public List<OutputFile> getOutputFiles() {
		return outputFiles;
	}

	/**
	 * Establece la lista de ficheros de resultados de la simulaci�n
	 * @param parameters Ficheros de salida deseados de la simulaci�n
	 */
	public void setOutputFiles(List<OutputFile> outputFiles) {
		this.outputFiles = outputFiles;
	}

	/**
	 * Recupera la fecha de creaci�n de la simulaci�n
	 * @return Fecha de creaci�n de la simulaci�n
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Establece la fecha de creaci�n de la simulaci�n
	 * @param creationDate Fecha de creaci�n de la simulaci�n
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Recupera la fecha de actualizaci�n de la simulaci�n
	 * @return Fecha de actualizaci�n de la simulaci�n
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * Actualiza la fecha de modificaci�n
	 * @param updateDate Fecha de modificaci�n
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * Obtiene la fecha de inicio de la simulaci�n
	 * @return Fecha de inicio de la simulaci�n
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Establece la fecha de inicio de la simulaci�n
	 * @param startDate Fecha de inicio de la simulaci�n
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Recupera la fecha de finalizaci�n de la simulaci�n
	 * @return Fecha de finalizaci�n de la simulaci�n
	 */
	public Date getFinishDate() {
		return finishDate;
	}

	/**
	 * Establece la fecha de finalizaci�n de la simulaci�n
	 * @param finishDate Fecha de finalizaci�n de la simulaci�n
	 */
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
		if(startDate != null){
			setTotalTime(finishDate.getTime() - startDate.getTime());
		}
	}

	/**
	 * Obtiene el tiempo total que ha necesitado una simulaci�n para completarse
	 * @return Tiempo total que una simulaci�n ha necesitado para completarse
	 */
	public long getTotalTime() {
		return totalTime;
	}

	/**
	 * Establece el tiempo que se ha necesitado para realizar una simulaci�n y su informe
	 * @param totalTime Tiempo total que se ha necesitado para completar la simulaci�n
	 */
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}
	
	/**
	 * Recupera el n�mero de simulaciones completadas
	 * @return N�mero de simulaciones completadas
	 */
	public Set<Integer> getCompletedRepetitions() {
		return completedRepetitions;
	}

	/**
	 * Establece el n�mero de simulaciones completadas
	 * @param completedRepetitions Colecci�n de simulaciones finalizadas
	 */
	public void setCompletedRepetitions(Set<Integer> completedRepetitions) {
		this.completedRepetitions = completedRepetitions;
	}

	/**
	 * Actualizaci�n al tiempo total que se ha necesitado para completar la simulaci�n
	 * @param time Incremento (o decremento) temporal
	 */
	public void addTotalTime(long time){
		if(time>0)
			this.totalTime+=time;
	}

	/**
	 * Aumento en una unidad de los ficheros de salida
	 * @param outputFile Fichero de salida que se desea agragar a los ficheros de salida
	 */
	public void addOutputFile(OutputFile outputFile){
		this.outputFiles.add(outputFile);
//		if(outputFile.getSimulation()==null || !outputFile.getSimulation().equals(this)){
//			outputFile.setSimulation(this);
//		}
	}
	
	/**
	 * Aumento en una unidad de los par�metros de la simulaci�n
	 * @param parameter Par�metro que se desea agregar a la simulaci�n
	 */
	public void addParameter(Parameter parameter){
		this.parameters.add(parameter);
		if(parameter.getSimulation()==null || !parameter.getSimulation().equals(this)){
			parameter.setSimulation(this);
		}
	}

}
