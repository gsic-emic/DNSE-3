package dnse3.orchestration.jpa.model.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import dnse3.orchestration.jpa.model.simulation.ParameterSweepSimulation;
import dnse3.orchestration.jpa.model.simulation.SingleSimulation;
import dnse3.orchestration.jpa.model.user.User;

@Entity
@Table(name = "PROJECT")
@NamedQueries({
	@NamedQuery(
			name="getNumberSingleSimulations",
			query="SELECT COUNT(s) "+
					"FROM SingleSimulation s "+
					"WHERE s.parameterSweepSimulation IS NULL and "+
					"s.project.id = :projectId"),
	@NamedQuery(
		name="getRemovigProjects",
		query = "SELECT p FROM Project p WHERE p.remove=true"
	)
})

public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="ID")
	private int id;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name = "DESCRIPTION", length = 1001)
	private String description;
	
	@Column(name = "SOURCE_URI")
	private String sourceURI;
	
	@Column(name = "PACKAGE_URI")
	private String packageURI;
	
	@ManyToOne
	@JoinColumn(name="USERNAME")
	private User user;
	
	@Column(name = "CREATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Column(name = "UPDTE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
	
	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy
	private List<SingleSimulation> singleSimulations;

	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy
	private List<ParameterSweepSimulation> parameterSweepSimulations;

	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy(value="name ASC")
	private List<ParameterDescription> parameterDescriptions;

	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy(value="name ASC")
	private List<OutputFileStructure> outputFileStructures;

	@OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@OrderBy(value="name ASC")
	private List<OutputFile> outputFiles;
	
	//TODO: marca de eliminación
	@Column(name = "REMOVE")
	private boolean remove;
	
	public Project(){
		this.singleSimulations =  new ArrayList<>();
		this.parameterSweepSimulations = new ArrayList<>();
		
		this.parameterDescriptions = new ArrayList<>();
		this.outputFileStructures =  new ArrayList<>();
		this.outputFiles = new ArrayList<>();
		
		this.remove=false;
	}
	
	public Project(String name, String description){
		this.name = name;
		this.description = description;
		
		this.creationDate =  new Date();
		this.updateDate = new Date();
		
		this.singleSimulations =  new ArrayList<>();
		this.parameterSweepSimulations = new ArrayList<>();
		
		this.parameterDescriptions = new ArrayList<>();
		this.outputFileStructures =  new ArrayList<>();
		this.outputFiles = new ArrayList<>();
		
		this.remove=false;
	}
	
	public Project(String name, String description,
			List<ParameterDescription> parameterDescriptions,
			List<OutputFileStructure> outputFileStructures,
			List<OutputFile> outputFiles){
		this.name = name;
		this.description = description;
		
		this.creationDate =  new Date();
		this.updateDate = new Date();
		
		this.singleSimulations =  new ArrayList<>();
		this.parameterSweepSimulations = new ArrayList<>();
		
		this.parameterDescriptions = parameterDescriptions;
		this.outputFileStructures =  outputFileStructures;
		this.outputFiles = outputFiles;
		
		this.remove=false;
	}

	/**
	 * Método para recuperar el identificador del proyecto en el sistema
	 * @return Identificador del proyecto
	 */
	public int getId() {
		return id;
	}

	/**
	 * Método para establecer el identificador del proyecto en el sistema
	 * @param id Identificador del proyecto en el sistema
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Método para obtener el nombre del proyecto
	 * @return Nombre del proyecto
	 */
	public String getName() {
		return name;
	}

	/**
	 * Método para establecer el nombre del proyecto
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Método para obtener la descripción del proyecto
	 * @return Descriptor del proyecto
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Método para establecer la descripción del proyecto
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Método para obtener la URI del proyecto
	 * @return URI del proyecto
	 */
	public String getSourceURI() {
		return sourceURI;
	}

	/**
	 * Método para establecer la URI del proyecto
	 * @param sourceURI nueva URI del proyecto
	 */
	public void setSourceURI(String sourceURI) {
		this.sourceURI = sourceURI;
	}
	
	public String getPackageURI() {
		return packageURI;
	}

	public void setPackageURI(String packageURI) {
		this.packageURI = packageURI;
	}

	/**
	 * Método para recuperar el propietario de un proyecto
	 * @return Identificador del usuario
	 */
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public List<SingleSimulation> getSingleSimulations() {
		return singleSimulations;
	}

	public void setSingleSimulations(List<SingleSimulation> singleSimulations) {
		this.singleSimulations = singleSimulations;
	}

	/**
	 * Recupera la lista de simulaciones de barrido
	 * @return Lista de simulaciones de barrido
	 */
	public List<ParameterSweepSimulation> getParameterSweepSimulations() {
		return parameterSweepSimulations;
	}

	/**
	 * Establece la lista de simulaciones de barrido
	 * @return Lista de simulaciones de barrido
	 */
	public void setParameterSweepSimulations(List<ParameterSweepSimulation> parameterSweepSimulations) {
		this.parameterSweepSimulations = parameterSweepSimulations;
	}

	/**
	 * Método para recuperar la lista de descriptores de los parámetros de un proyecto
	 * @return Lista de las descripciones de los parámetros
	 */
	public List<ParameterDescription> getParameterDescriptions() {
		return parameterDescriptions;
	}

	/**
	 * Método para establecer la lista de descriptores de los parámetros
	 * @param parameterDescriptions Descriptores de los parámetros
	 */
	public void setParameterDescriptions(List<ParameterDescription> parameterDescriptions) {
		this.parameterDescriptions = parameterDescriptions;
	}

	/**
	 * Métdo para recuperar la lista de la estructura de los ficheros de resultados
	 * @return Lista de ficheros de salida
	 */
	public List<OutputFileStructure> getOutputFileStructures() {
		return outputFileStructures;
	}

	/**
	 * Establece la estructura de los ficheros de salida
	 * @param outputFileStructures Lista con las estructuras de los ficheros de salida
	 */
	public void setOutputFileStructures(List<OutputFileStructure> outputFileStructures) {
		this.outputFileStructures = outputFileStructures;
	}

	/**
	 * Método para obtener la lista de los ficheros de salida
	 * @return Lista de los ficheros de salida
	 */
	public List<OutputFile> getOutputFiles() {
		return outputFiles;
	}

	/**
	 * Método para establecer la lista de ficheros de salida
	 * @param outputFiles Lista de ficheros de salida
	 */
	public void setOutputFiles(List<OutputFile> outputFiles) {
		this.outputFiles = outputFiles;
	}
	
	/**
	 * Método para comprobar si un proyecto se está borrando
	 */
	public boolean isRemove() {
		return remove;
	}

	/**
	 * Establece que el proyecto se está borrando
	 * @param remove
	 */
	public void setRemove(boolean remove) {
		this.remove = remove;
	}

	/**
	 * Método para añadir una simulación de tipo individual
	 * @param simulation Simulación a añadir
	 */
	public void addSingleSimulation(SingleSimulation simulation){
		this.singleSimulations.add(simulation);
		if(simulation.getProject()==null || !simulation.getProject().equals(this)) //Si se quita esto, se puede hacer que solo haya referencia en un sentido
			simulation.setProject(this);
	}
	
	/**
	 * Método para añadir una simulación de tipo barrido de parámetros
	 * @param simulation Simulación de tipo barrido de parámetros
	 */
	public void addParameterSweepSimulation(ParameterSweepSimulation simulation){
		this.parameterSweepSimulations.add(simulation);
		if(simulation.getProject()==null || !simulation.getProject().equals(this))
			simulation.setProject(this);
	}
	
	/**
	 * Método para añadir una descripción al parámetro
	 * @param parameterDescription Descripción del parámetro a añadir
	 */
	public void addParameterDescription(ParameterDescription parameterDescription){
		this.parameterDescriptions.add(parameterDescription);
		if(parameterDescription.getProject()==null || !parameterDescription.getProject().equals(this))
			parameterDescription.setProject(this);
	}
	
	/**
	 * Método para agregar una estructura de ficheros
	 * @param outputFileStructure Estructra de ficheros a añadir
	 */
	public void addOutputFileStructure(OutputFileStructure outputFileStructure){
		this.outputFileStructures.add(outputFileStructure);
		if(outputFileStructure.getProject()==null || !outputFileStructure.getProject().equals(this))
			outputFileStructure.setProject(this);
	}
	
	/**
	 * Método para añadir un fichero de salida
	 * @param outputFile Fichero de salida a añadir
	 */
	public void addOutputFile(OutputFile outputFile){
		this.outputFiles.add(outputFile);
		if(outputFile.getProject()==null || !outputFile.getProject().equals(this))
			outputFile.setProject(this);
	}

	/**
	 * Método para obtener el número de parametros que tiene una simulación de barrido
	 * @return Entero con la longitud de parámetros de una simulación de barrido de parámetros
	 */
	public int getNumParameterSweepSimulations() {
		// TODO Auto-generated method stub
		return parameterSweepSimulations.size();
	}
	
	//TODO: contadores
	
}
