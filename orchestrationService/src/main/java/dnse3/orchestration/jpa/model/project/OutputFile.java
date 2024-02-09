package dnse3.orchestration.jpa.model.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name="OUTPUT_FILE")
@NamedQueries({
	@NamedQuery(
			name="selectOutputFile",
			query="SELECT output "+
					"FROM OutputFile output "+
					"WHERE output.name = :outputFileName and "+
					"output.project.id = :projectId")
})
public class OutputFile implements Serializable {

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
	
	@Column(name="TYPE")
	private OutputFileType type;
	
	@ManyToOne
	@JoinColumn(name="OUTPUT_FILE_STRUCTURE_ID")
	private OutputFileStructure outputFileStructure;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID")
	private Project project;
	
	public OutputFile(){
		//JPA
	}
	
	public OutputFile(String name, OutputFileStructure outputFileStructure, OutputFileType type){
		this.name= name;
		this.outputFileStructure=outputFileStructure;
		this.type = type;
	}
	
	public OutputFile(String name, OutputFileType type){
		this.name= name;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OutputFileType getType() {
		return type;
	}

	public void setType(OutputFileType type) {
		this.type = type;
	}

	public OutputFileStructure getOutputFileStructure() {
		return outputFileStructure;
	}

	public void setOutputFileStructure(OutputFileStructure outputFileStructure) {
		this.outputFileStructure = outputFileStructure;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		if(this.project.getOutputFiles()==null || !this.project.getOutputFiles().contains(this))
			this.project.getOutputFiles().add(this);
	}
	
	
}
