package dnse3.orchestration.auxiliar;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class ParameterMapper {
	
	private String parameterName;
	private ArrayList<String> values;
	private String minValue;
	private String maxValue;
	private String step;
	//private String units;
	private boolean random;
	
	/**
	 * Método para crear un vector multidimensional relacionado con los parámetros
	 * @param parameterObj JSON con el objeto que representa al parámetro
	 * @param multipleValues Indica si el parámetro puede tener valores múltiples (true) o un valor fijo (false)
	 * @throws Exception Múltiples motivos para lanzar una excepción. La mayoría de ellos están relacionados con la 
	 * mal formación del JSON
	 */
	public ParameterMapper(JSONObject parameterObj, boolean multipleValues) throws Exception{
		if(!parameterObj.has("name")){
			throw new Exception("JSON mal formado. El parámetro no tiene nombre");
		}
		this.parameterName=parameterObj.getString("name");
		this.random=false;
		this.values=new ArrayList<>();
		if(!multipleValues){
			if(parameterObj.has("value") && !(parameterObj.has("random") && parameterObj.getBoolean("random")))
				this.values.add(parameterObj.getString("value"));
//			else if(!parameterObj.has("value") && parameterObj.has("random"))
			else if(parameterObj.has("value") && parameterObj.has("random"))
				this.random=parameterObj.getBoolean("random");
			else if(!parameterObj.has("value") && parameterObj.has("random"))
				this.random=parameterObj.getBoolean("random");
			else
				throw new Exception("JSON mal formado. El parámetro no es de tipo value ni random");
		}
		else{
			if(parameterObj.has("values") && !(parameterObj.has("minValue") || parameterObj.has("maxValue") || parameterObj.has("step")) && !(parameterObj.has("random") && parameterObj.getBoolean("random"))){
				JSONArray valuesArray = parameterObj.getJSONArray("values");
				if(valuesArray.length()==0)
					throw new Exception("JSON mal formado. Los valores que puede tomar el parámetro no pueden ser 0");
				
				for(int i=0; i<valuesArray.length(); i++)
					this.values.add(valuesArray.getString(i));
			}
			else if(!parameterObj.has("values") && (parameterObj.has("minValue") && parameterObj.has("maxValue") && parameterObj.has("step")) && !(parameterObj.has("random") && parameterObj.getBoolean("random"))){
				this.minValue=parameterObj.getString("minValue");
				this.maxValue=parameterObj.getString("maxValue");
				this.step=parameterObj.getString("step");
				
				if(this.step.trim().startsWith("-")){
					if(Double.valueOf(this.minValue)<Double.valueOf(this.maxValue)){
						throw new Exception("JSON mal formado. Para inicios negativos, el valor mínimo tiene que ser más alto que el máximo");
					}
				}
				else{
					if(Double.valueOf(this.minValue)>Double.valueOf(this.maxValue)){
						throw new Exception("JSON mal formado. Para inicios positivos, el valor mínimo tiene que ser más bajo que el máximo");
					}
				}
			}
			else if(parameterObj.has("values") && parameterObj.has("random")) {
				this.random=parameterObj.getBoolean("random");
			}
			else if(!parameterObj.has("values") && !(parameterObj.has("minValue") || parameterObj.has("maxValue") || parameterObj.has("step")) && parameterObj.has("random")){
				this.random=parameterObj.getBoolean("random");
			}
			else{
				throw new Exception("JSON mal formado");
			}
		}
	}

	/**
	 * Recupera el identificador del parámetro
	 * @return Identificador del parámetro
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Establece el identificador del parámetro
	 * @param parameterName Identificador del parámetro
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	/**
	 * Método para recuperar los valores de un parámetro
	 * @return Valores del parámetro
	 */
	public ArrayList<String> getValues() {
		return values;
	}

	/**
	 * Establece los valores que vaa tomar el parámetro
	 * @param values Lista de valores que va a tomar el parámetro
	 */
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	/**
	 * Obtiene el valor mínimo del parámetro
	 * @return Valor mínimo del parámetro
	 */
	public String getMinValue() {
		return minValue;
	}

	/**
	 * Establece el valor mínimo del parámetro
	 * @param minValue Valor mínimo que se quiere establecer
	 */
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	/**
	 * Obtiene el valor máximo del parámetro
	 * @return Valor máximo del parámetro
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * Establece el valor máximo del parámetro
	 * @param maxValue Valor máximo que se desea establecer
	 */
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	
	/**
	 * Obtiene el incremento del parámetro
	 * @return Incrementos del parámetro para pasar del valor mín. al máx.
	 */
	public String getStep(){
		return this.step;
	}
	
	/**
	 * Método para establecer los incrementos para llegar del valor min. al máx.
	 * @param step Valor del incremento que se desea establecer
	 */
	public void setStep(String step){
		this.step=step;
	}

	/**
	 * Método que comprueba si un parámetro es del tipo random
	 * @return El valor de random
	 */
	public boolean isRandom() {
		return random;
	}

	/**
	 * Método para establecer si el parámetro es random
	 * @param random Valor de random
	 */
	public void setRandom(boolean random) {
		this.random = random;
	}
	
	

}
