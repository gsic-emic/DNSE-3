package dnse3.orchestration.jpa.model.project;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table(name="OUTPUT_FILE_STRUCTURE")
@NamedQueries({
	@NamedQuery(
			name="selectOutputFileStructure",
			query="SELECT ofs "+
					"FROM OutputFileStructure ofs "+
					"WHERE ofs.name = :outputFileStructureName and "+
					"ofs.project.id = :projectId")
})
public class OutputFileStructure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="ID")
	@GeneratedValue
	private int id;
	
	@Column(name="NAME")
	private String name;
	
	@ElementCollection
	@OrderColumn
	@Column(name="OUTPUT_VAR")
	private List<String> outputVars;
	
	@Column(name="MULTI_LINE")
	private boolean multiLine;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID")
	private Project project;
	
	public OutputFileStructure(){
		//JPA
	}
	
	public OutputFileStructure(String name, List<String> outputVars, boolean multiLine){
		this.name=name;
		this.outputVars=outputVars;
		this.multiLine=multiLine;
	}
	
	public OutputFileStructure(String name, List<String> outputVars){
		new OutputFileStructure(name,outputVars,true);
	}

	//Getters and Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<String> getOutputVars() {
		return outputVars;
	}

	public void setOutputVars(List<String> outputVars) {
		this.outputVars = outputVars;
	}

	public boolean isMultiLine() {
		return multiLine;
	}

	public void setMultiLine(boolean multiLine) {
		this.multiLine = multiLine;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		if(this.project.getOutputFileStructures()==null || !this.project.getOutputFileStructures().contains(this))
			this.project.getOutputFileStructures().add(this);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
