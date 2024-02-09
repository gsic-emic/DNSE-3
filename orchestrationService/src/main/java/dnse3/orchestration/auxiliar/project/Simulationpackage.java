//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2018.07.30 a las 02:10:08 PM CEST 
//


package dnse3.orchestration.auxiliar.project;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}description"/&gt;
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}parametertypes"/&gt;
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}outputfilestructures"/&gt;
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}outputfiles"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description",
    "parametertypes",
    "outputfilestructures",
    "outputfiles"
})
@XmlRootElement(name = "simulationpackage")
public class Simulationpackage {

    @XmlElement(required = true)
    protected Description description;
    @XmlElement(required = true)
    protected Parametertypes parametertypes;
    @XmlElement(required = true)
    protected Outputfilestructures outputfilestructures;
    @XmlElement(required = true)
    protected Outputfiles outputfiles;

    /**
     * Obtiene el valor de la propiedad description.
     * 
     * @return
     *     possible object is
     *     {@link Description }
     *     
     */
    public Description getDescription() {
        return description;
    }

    /**
     * Define el valor de la propiedad description.
     * 
     * @param value
     *     allowed object is
     *     {@link Description }
     *     
     */
    public void setDescription(Description value) {
        this.description = value;
    }

    /**
     * Obtiene el valor de la propiedad parametertypes.
     * 
     * @return
     *     possible object is
     *     {@link Parametertypes }
     *     
     */
    public Parametertypes getParametertypes() {
        return parametertypes;
    }

    /**
     * Define el valor de la propiedad parametertypes.
     * 
     * @param value
     *     allowed object is
     *     {@link Parametertypes }
     *     
     */
    public void setParametertypes(Parametertypes value) {
        this.parametertypes = value;
    }

    /**
     * Obtiene el valor de la propiedad outputfilestructures.
     * 
     * @return
     *     possible object is
     *     {@link Outputfilestructures }
     *     
     */
    public Outputfilestructures getOutputfilestructures() {
        return outputfilestructures;
    }

    /**
     * Define el valor de la propiedad outputfilestructures.
     * 
     * @param value
     *     allowed object is
     *     {@link Outputfilestructures }
     *     
     */
    public void setOutputfilestructures(Outputfilestructures value) {
        this.outputfilestructures = value;
    }

    /**
     * Obtiene el valor de la propiedad outputfiles.
     * 
     * @return
     *     possible object is
     *     {@link Outputfiles }
     *     
     */
    public Outputfiles getOutputfiles() {
        return outputfiles;
    }

    /**
     * Define el valor de la propiedad outputfiles.
     * 
     * @param value
     *     allowed object is
     *     {@link Outputfiles }
     *     
     */
    public void setOutputfiles(Outputfiles value) {
        this.outputfiles = value;
    }

    /**
     * Método para verificar el fichero XML con los parámetros
     */
	public boolean verify() {
		//Comprobar las condiciones del XML
		ArrayList<String> parametersId = new ArrayList<>();
		ArrayList<String> outputFiles = new ArrayList<>();
		
		for(Integertype parameter : parametertypes.getIntegertype()){
			System.out.println(parameter.getId());
			if(!parameter.verify() || parametersId.contains(parameter.getId()))
				return false;
			parametersId.add(parameter.getId());
		}
		
		for(Rationaltype parameter : parametertypes.getRationaltype()){
			System.out.println(parameter.getId());
			if(!parameter.verify() || parametersId.contains(parameter.getId()))
				return false;
			parametersId.add(parameter.getId());
		}
		
		for(Stringtype parameter : parametertypes.getStringtype()){
			if(!parameter.verify() || parametersId.contains(parameter.getId()))
				return false;
			parametersId.add(parameter.getId());
		}
		
		for(Resultfile file : outputfiles.getResultfile()){
			System.out.println(file.getValue());
			if(outputFiles.contains(file.getValue()))
				return false;
			outputFiles.add(file.getValue());
		}
		
		for(Tabbedfile file : outputfiles.getTabbedfile()){
			System.out.println(file.getValue());
			if(outputFiles.contains(file.getValue()))
				return false;
			outputFiles.add(file.getValue());
		}
		
		for(String file : outputfiles.getTracefile()){
			if(outputFiles.contains(file))
				return false;
			outputFiles.add(file);
        }
        
		return true;
	}

}
