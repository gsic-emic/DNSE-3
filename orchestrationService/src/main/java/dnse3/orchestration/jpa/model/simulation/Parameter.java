package dnse3.orchestration.jpa.model.simulation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
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

import dnse3.orchestration.jpa.model.project.ParameterDescription;

@Entity
@Table(name="PARAMETER")
@NamedQueries({
	@NamedQuery(
			name="selectParameter",
			query="SELECT p "+
					"FROM Parameter p "+
					"WHERE p.parameterDescription.name = :parameterName and "+
					"p.parameterDescription.project.id = :projectId and "+
					"p.simulation.id = :simulationId")
})
public class Parameter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name="ID")
	private int id;
	
	@ManyToOne
	@JoinColumn(name="PARAMETER_DESCRIPTION_ID")
	private ParameterDescription parameterDescription;
	
	@ManyToOne
	@JoinColumn(name="SIMULATION_ID")
	private Simulation simulation;
	
	@Column(name="MIN_VALUE")
	private String minValue;
	
	@Column(name="MAX_VALUE")
	private String maxValue;
	
	@Column(name="STEP")
	private String step;
	
	@ElementCollection
	@OrderColumn
	private List<String> values;
	
	@Column(name="RANDOM")
	private boolean random;
	
	/**
	 * Constructor de la clase
	 */
	public Parameter(){
		//JPA
		this.values = new ArrayList<>();
	}
	
	/**
	 * Constructor de la clase. Establece el valor que toma el parámetro.
	 * @param value Valor que toma el parámetro
	 */
	public Parameter(String value){
		this.values = new ArrayList<>();
		values.add(value);
		this.random=false;
	}
	
	/**
	 * Constructor de la clase. Establece la lista de valores que puede tomar
	 * @param values Valores que va a tomar
	 */
	public Parameter(List<String> values){
		this.values=values;
		this.random=false;
	}
	
	/**
	 * Constructor de la clase. Indica el valor mínimo, máximo y el incremento del parámetro
	 * @param minValue Valor mínimo del parámetro
	 * @param maxValue Valor máximo del parámetro
	 * @param step Valor de incrementos por el cual se llega del mínimo al máximo
	 */
	public Parameter(String minValue, String maxValue, String step){
		this.minValue= minValue;
		this.maxValue= maxValue;
		this.step = step;
		
		this.values = new ArrayList<>();
		this.random=false;
	}
	
	/**
	 * Constructor de la clase. Crea una instancia de la clase indicando si 
	 * es o no es un parámetro aleatorio
	 * @param random Parámetro que fija si es aleatoria o no
	 */
	public Parameter(boolean random){
		this.values = new ArrayList<>();
		this.random = random;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ParameterDescription getParameterDescription() {
		return parameterDescription;
	}

	public void setParameterDescription(ParameterDescription parameterDescription) {
		this.parameterDescription = parameterDescription;
	}

	/**
	 * Método para obtener una simulación 
	 * @return Instancia de la simulación
	 */
	public Simulation getSimulation() {
		return simulation;
	}

	/**
	 * Método para establecer una simulación
	 * @param simulation Instancia de la simulación a almacenar
	 */
	public void setSimulation(Simulation simulation) {
		this.simulation = simulation;
		if(!simulation.getParameters().contains(this)){
			this.simulation.getParameters().add(this);
		}
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public boolean isRandom() {
		return random;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}
	
	public String getSingleValue(){
		if(random)
			return parameterDescription.getNewRandomString();
			
		if(!values.isEmpty())
			return values.iterator().next();
		return minValue;
	}

	public void explode(List<HashSet<Parameter>> parameters) {
		List<HashSet<Parameter>> singleValues = new ArrayList<>();
		
		if(random){
			HashSet<Parameter> val = new HashSet<>();
			Parameter p = new Parameter(random);
			p.setParameterDescription(getParameterDescription());
			val.add(p);
			singleValues.add(val);
		}
		else{
			switch(getParameterDescription().getType()){
			case STRING_VALUE:
			case SEED: //Aunque sea una semilla, se puede definir su valor único
				for(String s: values){
					HashSet<Parameter> val = new HashSet<>();
					Parameter p = new Parameter(s);
					p.setParameterDescription(getParameterDescription());
					val.add(p);
					singleValues.add(val);
				}
				break;
			case INTEGER_VALUE:
				if(values!=null && !values.isEmpty()){
					for(String s: values){
						HashSet<Parameter> val = new HashSet<>();
						Parameter p = new Parameter(s);
						p.setParameterDescription(getParameterDescription());
						val.add(p);
						singleValues.add(val);
					}
				}
				else{
					System.out.println(minValue);
					System.out.println(maxValue);
					System.out.println(step);
					for(int i = Integer.valueOf(minValue); i<=Integer.valueOf(maxValue); i+=Integer.valueOf(step)){
						HashSet<Parameter> val = new HashSet<>();
						Parameter p = new Parameter(String.valueOf(i));
						p.setParameterDescription(getParameterDescription());
						val.add(p);
						singleValues.add(val);
					}
				}
				break;
			case RATIONAL_VALUE:
				if(values!=null && !values.isEmpty()){
					for(String s: values){
						HashSet<Parameter> val = new HashSet<>();
						Parameter p = new Parameter(s);
						p.setParameterDescription(getParameterDescription());
						val.add(p);
						singleValues.add(val);
					}
				}
				else{
					for(double i = Double.valueOf(minValue); i<=Double.valueOf(maxValue); i+=Double.valueOf(step)){
						HashSet<Parameter> val = new HashSet<>();
						Parameter p = new Parameter(String.valueOf(i));
						p.setParameterDescription(getParameterDescription());
						val.add(p);
						singleValues.add(val);
					}
				}
				break;				
			}
		}
		
		
		if(parameters.isEmpty())
			parameters.addAll(singleValues);
		else{
			List<HashSet<Parameter>> temp = new ArrayList<>();
			for(HashSet<Parameter> p: singleValues){
				for(HashSet<Parameter> pp: parameters){
					HashSet<Parameter> localCopy = new HashSet<>();
					localCopy.addAll(pp);
					localCopy.addAll(p);
					temp.add(localCopy);
				}
			}
			parameters.clear();
			parameters.addAll(temp);
		}
		
	}
}
