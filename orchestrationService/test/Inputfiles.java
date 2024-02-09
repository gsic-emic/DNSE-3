//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2019.03.27 a las 05:52:49 PM CET 
//


package test;

import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element name="basefile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="additionalfile" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
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
    "basefile",
    "additionalfile"
})
@XmlRootElement(name = "inputfiles")
public class Inputfiles {

    @XmlElement(required = true)
    protected String basefile;
    protected List<String> additionalfile;

    /**
     * Obtiene el valor de la propiedad basefile.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBasefile() {
        return basefile;
    }

    /**
     * Define el valor de la propiedad basefile.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBasefile(String value) {
        this.basefile = value;
    }

    /**
     * Gets the value of the additionalfile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalfile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAdditionalfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAdditionalfile() {
        if (additionalfile == null) {
            additionalfile = new ArrayList<String>();
        }
        return this.additionalfile;
    }

}
