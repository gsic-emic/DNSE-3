package dnse3.orchestration.jpa.model.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import dnse3.orchestration.jpa.model.project.Project;

@Entity
@Table(name="USERS")
@NamedQueries({
	@NamedQuery(
			name="listUsers",
			query="SELECT u FROM User u")
})

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_SIMULATION_LIMIT = 10000;
	
	@Id
	@Column(name="USERNAME")
	private String username;
	
	@Column(name="SIMULATION_LIMIT")
	private int simulationLimit;
	
	@OneToMany(mappedBy="user",fetch=FetchType.LAZY,cascade=CascadeType.REMOVE)
	@OrderBy
	private List<Project> projects;
	
	/**
	 * Constructor de la clase.
	 */
	public User(){
		//JPA
		this.projects = new ArrayList<>();
	}
	
	/**
	 * Constructor de la clase. Se le indica el identificador del usuario
	 * @param username Identificador del usuario en el sitema
	 */
	public User(String username){
		this.username = username;
		this.simulationLimit = DEFAULT_SIMULATION_LIMIT;
		this.projects = new ArrayList<>();
	}
	
	/**
	 * Constructor de la clase. Se le indica el nombre del usuario y el límite de simulaciones que puede realizar
	 * @param username Identificador del usuario en el sitema
	 * @param simulationLimit Límite de simulaciones que puede realizar un usuario
	 */
	public User(String username, int simulationLimit){
		this.username = username;
		this.simulationLimit = (simulationLimit>0) ? simulationLimit : DEFAULT_SIMULATION_LIMIT;
		this.projects = new ArrayList<>();
	}

	/**
	 * Recupera el identificador del usuario del sistema
	 * @return Identificador del usuario
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Establece el identificador del usuario en el sistema
	 * @param username Identificador del usuario
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Obtiene el límite de simulaciones del usuario
	 * @return Límite de simulaciones que puede realizar el usuario
	 */
	public int getSimulationLimit() {
		return simulationLimit;
	}

	/**
	 * Establece el límite de simulaciones que puede realizar un usaurio
	 * @param simulationLimit Límite de simulaciones que puede realizar el usuario
	 */
	public void setSimulationLimit(int simulationLimit) {
		this.simulationLimit = simulationLimit;
	}

	/**
	 * Lista de proyectos que tiene el usuario
	 * @return La lista de proyectos del usuario
	 */
	public List<Project> getProjects() {
		return projects;
	}

	/**
	 * Establece la lista de proyectos del usuario
	 * @param projects Lista de proyectos del usuario
	 */
	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}
	
	/**
	 * Método para agregar un proyecto al usaurio
	 * @param project Proyecto a agregar al usuario
	 */
	public void addProject(Project project){
		this.projects.add(project);
		System.out.println("\n\n"+this.projects.get(0).getId()+"\n\n");
		if(project.getUser()==null || !project.getUser().equals(this))
			project.setUser(this);
	}
}
