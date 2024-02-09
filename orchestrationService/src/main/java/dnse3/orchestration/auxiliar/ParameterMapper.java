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
	 * M�todo para crear un vector multidimensional relacionado con los par�metros
	 * @param parameterObj JSON con el objeto que representa al par�metro
	 * @param multipleValues Indica si el par�metro puede tener valores m�ltiples (true) o un valor fijo (false)
	 * @throws Exception M�ltiples motivos para lanzar una excepci�n. La mayor�a de ellos est�n relacionados con la 
	 * mal formaci�n del JSON
	 */
	public ParameterMapper(JSONObject parameterObj, boolean multipleValues) throws Exception{
		if(!parameterObj.has("name")){
			throw new Exception("JSON mal formado. El par�metro no tiene nombre");
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
				throw new Exception("JSON mal formado. El par�metro no es de tipo value ni random");
		}
		else{
			if(parameterObj.has("values") && !(parameterObj.has("minValue") || parameterObj.has("maxValue") || parameterObj.has("step")) && !(parameterObj.has("random") && parameterObj.getBoolean("random"))){
				JSONArray valuesArray = parameterObj.getJSONArray("values");
				if(valuesArray.length()==0)
					throw new Exception("JSON mal formado. Los valores que puede tomar el par�metro no pueden ser 0");
				
				for(int i=0; i<valuesArray.length(); i++)
					this.values.add(valuesArray.getString(i));
			}
			else if(!parameterObj.has("values") && (parameterObj.has("minValue") && parameterObj.has("maxValue") && parameterObj.has("step")) && !(parameterObj.has("random") && parameterObj.getBoolean("random"))){
				this.minValue=parameterObj.getString("minValue");
				this.maxValue=parameterObj.getString("maxValue");
				this.step=parameterObj.getString("step");
				
				if(this.step.trim().startsWith("-")){
					if(Double.valueOf(this.minValue)<Double.valueOf(this.maxValue)){
						throw new Exception("JSON mal formado. Para inicios negativos, el valor m�nimo tiene que ser m�s alto que el m�ximo");
					}
				}
				else{
					if(Double.valueOf(this.minValue)>Double.valueOf(this.maxValue)){
						throw new Exception("JSON mal formado. Para inicios positivos, el valor m�nimo tiene que ser m�s bajo que el m�ximo");
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
	 * Recupera el identificador del par�metro
	 * @return Identificador del par�metro
	 */
	public String getParameterName() {
		return parameterName;
	}

	/**
	 * Establece el identificador del par�metro
	 * @param parameterName Identificador del par�metro
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	/**
	 * M�todo para recuperar los valores de un par�metro
	 * @return Valores del par�metro
	 */
	public ArrayList<String> getValues() {
		return values;
	}

	/**
	 * Establece los valores que vaa tomar el par�metro
	 * @param values Lista de valores que va a tomar el par�metro
	 */
	public void setValues(ArrayList<String> values) {
		this.values = values;
	}

	/**
	 * Obtiene el valor m�nimo del par�metro
	 * @return Valor m�nimo del par�metro
	 */
	public String getMinValue() {
		return minValue;
	}

	/**
	 * Establece el valor m�nimo del par�metro
	 * @param minValue Valor m�nimo que se quiere establecer
	 */
	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}

	/**
	 * Obtiene el valor m�ximo del par�metro
	 * @return Valor m�ximo del par�metro
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * Establece el valor m�ximo del par�metro
	 * @param maxValue Valor m�ximo que se desea establecer
	 */
	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}
	
	/**
	 * Obtiene el incremento del par�metro
	 * @return Incrementos del par�metro para pasar del valor m�n. al m�x.
	 */
	public String getStep(){
		return this.step;
	}
	
	/**
	 * M�todo para establecer los incrementos para llegar del valor min. al m�x.
	 * @param step Valor del incremento que se desea establecer
	 */
	public void setStep(String step){
		this.step=step;
	}

	/**
	 * M�todo que comprueba si un par�metro es del tipo random
	 * @return El valor de random
	 */
	public boolean isRandom() {
		return random;
	}

	/**
	 * M�todo para establecer si el par�metro es random
	 * @param random Valor de random
	 */
	public void setRandom(boolean random) {
		this.random = random;
	}
	
	

}
