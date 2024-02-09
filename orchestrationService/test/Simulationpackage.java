//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2019.03.27 a las 05:52:49 PM CET 
//


package test;

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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}description"/>
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}parametertypes"/>
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}outputfilestructures"/>
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}outputfiles"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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

}
