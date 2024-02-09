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
	 * Constructor de la clase. Crea la simulación asignado el nombre
	 * @param name Nombre de la simulación
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
	 * Constructor de la clase. Crea la simulación asignado el nombre y número
	 * de repeticion que se le pase por parámetros.
	 * @param name Nombre de la simulación
	 * @param numRepetitions Número de repeticiones
	 * @param priority Prioridad de la simulación
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
	 * Recupera el identificador de la simulación en la BBDD
	 * @return Identificador de la simulación
	 */
	public long getId() {
		return id;
	}

	/**
	 * Establece el identificador de la simulación en la BBDD
	 * @param id Identificador que se le va a asignar a la simulación
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Recupera el nombre de la simulación
	 * @return Nombre de la simulación
	 */
	public String getName() {
		return name;
	}

	/**
	 * Establece el nombre de la simulación
	 * @param name Nombre de la simulación
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Recupera el estado de la simulación
	 * @return Estado de la simulación
	 */
	public SimulationStatus getStatus() {
		return status;
	}

	/**
	 * Establece el estado de la simulación
	 * @param status Estado deseado de la simulación
	 */
	public void setStatus(SimulationStatus status) {
		this.status = status;
	}

	/**
	 * Recupera el número de repeticiones de la simulación
	 * @return Número de simulaciones de la simulación
	 */
	public int getNumRepetitions() {
		return numRepetitions;
	}

	/**
	 * Establece el número de repeticiones de la simulación
	 * @param numRepetitions Número de simulaciones desado de la simulación
	 */
	public void setNumRepetitions(int numRepetitions) {
		this.numRepetitions = numRepetitions;
	}

	/**
	 * Recupera la prioridad de la simulación
	 * @return Valor de la prioridad
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Establece la prioridad de la simulación
	 * @param priority Prioridad de la simulación
	 */
	public void setPriority(int priority){
		this.priority = priority;
	}

	/**
	 * Recupera la lista de parámetros de la simulación 
	 * @return Lista de parámetros de la simulación
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}

	/**
	 * Establece la lista de parámetros de la simulación
	 * @param parameters Parámetros deseados de la simulación
	 */
	public void setParameters(List<Parameter> parameters) {
		this.parameters = parameters;
	}

	/**
	 * Recupera la lista de ficheros de salida de la simulación
	 * @return Lista de ficheros de salida de la simulación
	 */
	public List<OutputFile> getOutputFiles() {
		return outputFiles;
	}

	/**
	 * Establece la lista de ficheros de resultados de la simulación
	 * @param parameters Ficheros de salida deseados de la simulación
	 */
	public void setOutputFiles(List<OutputFile> outputFiles) {
		this.outputFiles = outputFiles;
	}

	/**
	 * Recupera la fecha de creación de la simulación
	 * @return Fecha de creación de la simulación
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Establece la fecha de creación de la simulación
	 * @param creationDate Fecha de creación de la simulación
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * Recupera la fecha de actualización de la simulación
	 * @return Fecha de actualización de la simulación
	 */
	public Date getUpdateDate() {
		return updateDate;
	}

	/**
	 * Actualiza la fecha de modificación
	 * @param updateDate Fecha de modificación
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * Obtiene la fecha de inicio de la simulación
	 * @return Fecha de inicio de la simulación
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Establece la fecha de inicio de la simulación
	 * @param startDate Fecha de inicio de la simulación
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * Recupera la fecha de finalización de la simulación
	 * @return Fecha de finalización de la simulación
	 */
	public Date getFinishDate() {
		return finishDate;
	}

	/**
	 * Establece la fecha de finalización de la simulación
	 * @param finishDate Fecha de finalización de la simulación
	 */
	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
		if(startDate != null){
			setTotalTime(finishDate.getTime() - startDate.getTime());
		}
	}

	/**
	 * Obtiene el tiempo total que ha necesitado una simulación para completarse
	 * @return Tiempo total que una simulación ha necesitado para completarse
	 */
	public long getTotalTime() {
		return totalTime;
	}

	/**
	 * Establece el tiempo que se ha necesitado para realizar una simulación y su informe
	 * @param totalTime Tiempo total que se ha necesitado para completar la simulación
	 */
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}
	
	/**
	 * Recupera el número de simulaciones completadas
	 * @return Número de simulaciones completadas
	 */
	public Set<Integer> getCompletedRepetitions() {
		return completedRepetitions;
	}

	/**
	 * Establece el número de simulaciones completadas
	 * @param completedRepetitions Colección de simulaciones finalizadas
	 */
	public void setCompletedRepetitions(Set<Integer> completedRepetitions) {
		this.completedRepetitions = completedRepetitions;
	}

	/**
	 * Actualización al tiempo total que se ha necesitado para completar la simulación
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
	 * Aumento en una unidad de los parámetros de la simulación
	 * @param parameter Parámetro que se desea agregar a la simulación
	 */
	public void addParameter(Parameter parameter){
		this.parameters.add(parameter);
		if(parameter.getSimulation()==null || !parameter.getSimulation().equals(this)){
			parameter.setSimulation(this);
		}
	}

}
