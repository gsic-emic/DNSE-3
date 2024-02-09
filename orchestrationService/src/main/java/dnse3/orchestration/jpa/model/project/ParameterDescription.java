package dnse3.orchestration.jpa.model.project;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
@Table(name="PARAMETER_DESCRIPTION")
@NamedQueries({
	@NamedQuery(
			name="selectParameterDescription",
			query="SELECT pd "+
					"FROM ParameterDescription pd "+
					"WHERE pd.name = :parameterName and "+
					"pd.project.id = :projectId")
})
public class ParameterDescription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private int id;
	
	@Column(name = "NAME")
	private String name;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID")
	private Project project;
	
	@Column(name="TYPE")
	private ParameterTypeEnum type;
	
	@Column(name="GREATER")
	private String greater;
	
	@Column(name="GREATER_EQUAL")
	private String greaterEqual;

	@Column(name="LESS")
	private String less;

	@Column(name="LESS_EQUAL")
	private String lessEqual;
	
	@Column(name="DEFAULT_VALUE")
	private String defaultValue;
	
	@ElementCollection
	@OrderColumn
	private List<String> values;
	
private static SecureRandom randomGenerator = new SecureRandom();
	
	public ParameterDescription(){
		//JPA
	}
	
	/**
	 * Constructor de la clase. Asigna identificador, tipo y valores máximos y mínimos del parámetro
	 * @param name Identificador del parámetro
	 * @param type Tipo de parámetro
	 * @param greater Máximo valor que puede tomar
	 * @param greaterEqual El parámetro tiene que valer lo indicado en este campo o más
	 * @param less Valor mínimo del parámetro
	 * @param lessEqual El parámetro tiene que tomar lo indicado en este campo o menos
	 * @param defaultValue Valor por defecto del parámetro
	 */
	public ParameterDescription(String name, ParameterTypeEnum type, String greater, String greaterEqual, String less, String lessEqual, String defaultValue){
		this.name=name;
		this.type=type;
		this.greater=greater;
		this.greaterEqual=greaterEqual;
		this.less=less;
		this.lessEqual=lessEqual;
		this.defaultValue=defaultValue;
		
		this.values= new ArrayList<>();
	}
	
	/**
	 * Constructor de la clase. Asigna identificador, tipo de parámetro y posibles valores que puede tomar el parámetro.
	 * @param name Identificador del parámetro en el proyecto
	 * @param type Tipo de parámetro
	 * @param values Lista de posibles valores que puede tomar el parámetro
	 * @param defaultValue Valor por defecto del parámetro
	 */
	public ParameterDescription(String name,ParameterTypeEnum type, List<String> values, String defaultValue){
		this.name=name;
		this.type=type;
		this.values=values;
		this.defaultValue=defaultValue;
	}
	
	/**
	 * Constructor de la clase. Asigna identificador, tipo y valores máximos y mínimos del parámetro
	 * @param name Identificador del parámetro
	 * @param type Tipo de parámetro
	 * @param greater Máximo valor que puede tomar
	 * @param greaterEqual El parámetro tiene que valer lo indicado en este campo o más
	 * @param less Valor mínimo del parámetro
	 * @param lessEqual El parámetro tiene que tomar lo indicado en este campo o menos
	 */
	public ParameterDescription(String name, ParameterTypeEnum type, String greater, String greaterEqual, String less, String lessEqual){
		this.name=name;
		this.type=type;
		this.greater=greater;
		this.greaterEqual=greaterEqual;
		this.less=less;
		this.lessEqual=lessEqual;
		
		this.values= new ArrayList<>();
	}
	
	/**
	 * Constructor de la clase. Asigna identificador, tipo de parámetro y posibles valores que puede tomar el parámetro.
	 * @param name Identificador del parámetro en el proyecto
	 * @param type Tipo de parámetro
	 * @param values Lista de posibles valores que puede tomar el parámetro
	 */
	public ParameterDescription(String name,ParameterTypeEnum type, List<String> values){
		this.name=name;
		this.type=type;
		this.values=values;
	}
	
	/**
	 * Constructor de la clase. Asigna identificador y tipo de parámetro
	 * @param name Identificador del parámetro en el proyecto
	 * @param type Tipo de parámetro
	 */
	public ParameterDescription(String name, ParameterTypeEnum type){
		this.name=name;
		this.type=type;
		
		this.values=new ArrayList<>();
	}

	//Getters and Setter
	
	//TODO alternar set por valores y demás
	public ParameterTypeEnum getType() {
		return type;
	}

	public void setType(ParameterTypeEnum type) {
		this.type = type;
	}

	public String getGreater() {
		return greater;
	}

	public void setGreater(String greater) {
		this.greater = greater;
	}

	public String getGreaterEqual() {
		return greaterEqual;
	}

	public void setGreaterEqual(String greaterEqual) {
		this.greaterEqual = greaterEqual;
	}

	public String getLess() {
		return less;
	}

	public void setLess(String less) {
		this.less = less;
	}

	public String getLessEqual() {
		return lessEqual;
	}

	public void setLessEqual(String lessEqual) {
		this.lessEqual = lessEqual;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
		if(this.project.getParameterDescriptions()==null || !this.project.getParameterDescriptions().contains(this))
			this.project.getParameterDescriptions().add(this);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Método para obtener el valor por defecto del parámetro
	 * @return Valor por defecto del parámetro
	 */
	public String getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Establece el valor por defecto del parámetro
	 * @param defaultValue Valor que se establece como predeterminado
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * Método para comprobar si la lista de valores del parámetro son todos válidos
	 * @param values Valores que se desea comprobar
	 * @return Devuelve verdadero si todos los vales tienen el formato adecuado. 
	 * Falso si alguno de los valores no es válido
	 */
	public boolean isValid(List<String> values){
		for(String v : values){
			if(!isValid(v))
				return false;
		}
		return true;
	}
	
	/**
	 * Comprueba si el valor de un parámetro es válido
	 * @param value Valor a comprobar
	 * @return True si es válido, false si no lo es
	 */
	public boolean isValid(String value){
		switch(type){
		case STRING_VALUE:
			if(values.isEmpty())
				return true;
			return values.contains(value);
		case INTEGER_VALUE:
			if(greater!=null){
				if(less!=null)
					return Integer.valueOf(value)>Integer.valueOf(greater) && Integer.valueOf(value)<Integer.valueOf(less);
				else if(lessEqual!=null)
					return Integer.valueOf(value)>Integer.valueOf(greater) && Integer.valueOf(value)<=Integer.valueOf(lessEqual);
				else
					return Integer.valueOf(value)>Integer.valueOf(greater);
			}
			else if(greaterEqual!=null){
				if(less!=null)
					return Integer.valueOf(value)>=Integer.valueOf(greaterEqual) && Integer.valueOf(value)<Integer.valueOf(less);
				else if(lessEqual!=null)
					return Integer.valueOf(value)>=Integer.valueOf(greaterEqual) && Integer.valueOf(value)<=Integer.valueOf(lessEqual);
				else
					return Integer.valueOf(value)>=Integer.valueOf(greaterEqual);
			}
			else if(less!=null){
				return Integer.valueOf(value)<Integer.valueOf(less);
			}
			else if(lessEqual!=null){
				return Integer.valueOf(value)<=Integer.valueOf(lessEqual);
			}
			else
				return true;
		case RATIONAL_VALUE:
			if(greater!=null){
				if(less!=null)
					return Double.valueOf(value)>Double.valueOf(greater) && Double.valueOf(value)<Double.valueOf(less);
				else if(lessEqual!=null)
					return Double.valueOf(value)>Double.valueOf(greater) && Double.valueOf(value)<=Double.valueOf(lessEqual);
				else
					return Double.valueOf(value)>Double.valueOf(greater);
			}
			else if(greaterEqual!=null){
				if(less!=null)
					return Double.valueOf(value)>=Double.valueOf(greaterEqual) && Double.valueOf(value)<Double.valueOf(less);
				else if(lessEqual!=null)
					return Double.valueOf(value)>=Double.valueOf(greaterEqual) && Double.valueOf(value)<=Double.valueOf(lessEqual);
				else
					return Double.valueOf(value)>=Double.valueOf(greaterEqual);
			}
			else if(less!=null){
				return Double.valueOf(value)<Double.valueOf(less);
			}
			else if(lessEqual!=null){
				return Double.valueOf(value)<=Double.valueOf(lessEqual);
			}
			else
				return true;
		case SEED:
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Método para comprobar si los valores máximo y mínimo de un parámetro son válidos
	 * @param minValue Valor mínimo del parámetro
	 * @param maxValue Valor máximo del parámetro
	 * @return True si el formato de ambos valores es válido o false si no lo es
	 */
	public boolean isValid(String minValue, String maxValue){
		switch (type) {
		case STRING_VALUE:
			return false;
		case INTEGER_VALUE:
			if(Integer.valueOf(minValue)>Integer.valueOf(maxValue))
				return false;
			if(greater!=null){
				if(less!=null)
					return Integer.valueOf(minValue)>Integer.valueOf(greater) && Integer.valueOf(minValue)<Integer.valueOf(less) &&
							Integer.valueOf(maxValue)>Integer.valueOf(greater) && Integer.valueOf(maxValue)<Integer.valueOf(less);
				else if(lessEqual!=null)
					return Integer.valueOf(minValue)>Integer.valueOf(greater) && Integer.valueOf(minValue)<=Integer.valueOf(lessEqual) &&
							Integer.valueOf(maxValue)>Integer.valueOf(greater) && Integer.valueOf(maxValue)<Integer.valueOf(lessEqual);
				else
					return Integer.valueOf(minValue)>Integer.valueOf(greater) && Integer.valueOf(maxValue)>Integer.valueOf(greater);
			}
			else if(greaterEqual!=null){
				if(less!=null)
					return Integer.valueOf(minValue)>=Integer.valueOf(greaterEqual) && Integer.valueOf(minValue)<Integer.valueOf(less) &&
							Integer.valueOf(maxValue)>=Integer.valueOf(greaterEqual) && Integer.valueOf(maxValue)<Integer.valueOf(less);
				else if(lessEqual!=null)
					return Integer.valueOf(minValue)>=Integer.valueOf(greaterEqual) && Integer.valueOf(minValue)<=Integer.valueOf(lessEqual) &&
							Integer.valueOf(maxValue)>=Integer.valueOf(greaterEqual) && Integer.valueOf(maxValue)<=Integer.valueOf(lessEqual);
				else
					return Integer.valueOf(minValue)>=Integer.valueOf(greaterEqual) && Integer.valueOf(maxValue)>=Integer.valueOf(greaterEqual);
			}
			else if(less!=null){
				return Integer.valueOf(minValue)<Integer.valueOf(less) && Integer.valueOf(maxValue)<Integer.valueOf(less);
			}
			else if(lessEqual!=null){
				return Integer.valueOf(minValue)<=Integer.valueOf(lessEqual) && Integer.valueOf(maxValue)<=Integer.valueOf(lessEqual);
			}
			else
				return true;
		case RATIONAL_VALUE:
			if(Double.valueOf(minValue)>Double.valueOf(maxValue))
				return false;
			if(greater!=null){
				if(less!=null)
					return Double.valueOf(minValue)>Double.valueOf(greater) && Double.valueOf(minValue)<Double.valueOf(less) &&
							Double.valueOf(maxValue)>Double.valueOf(greater) && Double.valueOf(maxValue)<Double.valueOf(less);
				else if(lessEqual!=null)
					return Double.valueOf(minValue)>Double.valueOf(greater) && Double.valueOf(minValue)<=Double.valueOf(lessEqual) &&
							Double.valueOf(maxValue)>Double.valueOf(greater) && Double.valueOf(maxValue)<Double.valueOf(lessEqual);
				else
					return Double.valueOf(minValue)>Double.valueOf(greater) && Double.valueOf(maxValue)>Double.valueOf(greater);
			}
			else if(greaterEqual!=null){
				if(less!=null)
					return Double.valueOf(minValue)>=Double.valueOf(greaterEqual) && Double.valueOf(minValue)<Double.valueOf(less) &&
							Double.valueOf(maxValue)>=Double.valueOf(greaterEqual) && Double.valueOf(maxValue)<Double.valueOf(less);
				else if(lessEqual!=null)
					return Double.valueOf(minValue)>=Double.valueOf(greaterEqual) && Double.valueOf(minValue)<=Double.valueOf(lessEqual) &&
							Double.valueOf(maxValue)>=Double.valueOf(greaterEqual) && Double.valueOf(maxValue)<=Double.valueOf(lessEqual);
				else
					return Double.valueOf(minValue)>=Double.valueOf(greaterEqual) && Double.valueOf(maxValue)>=Double.valueOf(greaterEqual);
			}
			else if(less!=null){
				return Double.valueOf(minValue)<Double.valueOf(less) && Double.valueOf(maxValue)<Double.valueOf(less);
			}
			else if(lessEqual!=null){
				return Double.valueOf(minValue)<=Double.valueOf(lessEqual) && Double.valueOf(maxValue)<=Double.valueOf(lessEqual);
			}
			else
				return true;
		case SEED:
		    int minLength = minValue.length();
		    int maxLength = maxValue.length();
		    if (minLength == 0 || maxLength == 0) {
		        return false;
		    }
		    int i = 0;
		    if (minValue.charAt(0) == '-') {
		        if (minLength == 1) {
		            return false;
		        }
		        i = 1;
		    }
		    for (; i < minLength; i++) {
		        char c = minValue.charAt(i);
		        if (c < '0' || c > '9') {
		            return false;
		        }
		    }
		    
		    i = 0;
		    if (maxValue.charAt(0) == '-') {
		        if (maxLength == 1) {
		            return false;
		        }
		        i = 1;
		    }
		    for (; i < maxLength; i++) {
		        char c = maxValue.charAt(i);
		        if (c < '0' || c > '9') {
		            return false;
		        }
		    }
		    return true;
		default:
			return false;
		}
	}
	
	public String getNewRandomString(){
		switch(type){
		case STRING_VALUE:
			if(values.isEmpty())
				return new BigInteger(130, randomGenerator).toString(32);
			int i=0;
			int random = ThreadLocalRandom.current().nextInt(values.size()-1);
			for(String s:values){
				if(i==random)
					return s;
				i++;
			}
			return values.iterator().next();
		case INTEGER_VALUE:
			if(greaterEqual!=null && greater==null){
				if(lessEqual!=null && less ==null){
					return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.valueOf(lessEqual), Integer.valueOf(greaterEqual)));
				}
				else if(less!=null && lessEqual==null){
					return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.valueOf(less)+1, Integer.valueOf(greaterEqual)));
				}
				else{
					return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.valueOf(greaterEqual)));
				}				
			}
			else if(greater!=null && greaterEqual==null){
				if(lessEqual!=null && less ==null){
					return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.valueOf(lessEqual), Integer.valueOf(greater)-1));
				}
				else if(less!=null && lessEqual==null){
					return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.valueOf(less)+1, Integer.valueOf(greater)-1));
				}
				else{
					return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.MIN_VALUE, Integer.valueOf(greater)-1));
				}	
			}
			else{
				if(lessEqual!=null && less ==null){
					return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.valueOf(lessEqual), Integer.MAX_VALUE));
				}
				else if(less!=null && lessEqual==null){
					return String.valueOf(ThreadLocalRandom.current().nextInt(Integer.valueOf(less)+1, Integer.MAX_VALUE));
				}
				else{
					return String.valueOf(ThreadLocalRandom.current().nextInt());
				}	
			}
		case RATIONAL_VALUE:
			double result;
			if(greaterEqual!=null && greater==null){
				if(lessEqual!=null && less ==null){
					return String.valueOf(ThreadLocalRandom.current().nextDouble(Double.valueOf(lessEqual), Double.valueOf(greaterEqual)));
				}
				else if(less!=null && lessEqual==null){
					do{
						result = ThreadLocalRandom.current().nextDouble(Double.valueOf(less), Double.valueOf(greaterEqual));
					}while(result==Double.valueOf(less));
					
					return String.valueOf(result);
				}
				else{
					return String.valueOf(ThreadLocalRandom.current().nextDouble(-Double.MAX_VALUE, Double.valueOf(greaterEqual)));
				}				
			}
			else if(greater!=null && greaterEqual==null){
				if(lessEqual!=null && less ==null){
					do{
						result = ThreadLocalRandom.current().nextDouble(Double.valueOf(lessEqual), Double.valueOf(greater));
					}while(result==Double.valueOf(greater));
					
					return String.valueOf(result);
				}
				else if(less!=null && lessEqual==null){
					do{
						result = ThreadLocalRandom.current().nextDouble(Double.valueOf(less), Double.valueOf(greater));
					}while(result==Double.valueOf(greater) || result==Double.valueOf(less));
					
					return String.valueOf(result);
				}
				else{
					do{
						result = ThreadLocalRandom.current().nextDouble(-Double.MAX_VALUE, Double.valueOf(greater));
					}while(result==Double.valueOf(greater));
					
					return String.valueOf(result);
				}	
			}
			else{
				if(lessEqual!=null && less ==null){
					return String.valueOf(ThreadLocalRandom.current().nextDouble(Double.valueOf(lessEqual), Double.MAX_VALUE));
				}
				else if(less!=null && lessEqual==null){
					do{
						result = ThreadLocalRandom.current().nextDouble(Double.valueOf(less), Double.MAX_VALUE);
					}while(result==Double.valueOf(less));
					
					return String.valueOf(result);
				}
				else{
					return String.valueOf(ThreadLocalRandom.current().nextDouble());
				}	
			}
		case SEED:
			return String.valueOf(randomGenerator.nextInt());
		default:
			return new BigInteger(130, randomGenerator).toString(32);
		}
	}
}
