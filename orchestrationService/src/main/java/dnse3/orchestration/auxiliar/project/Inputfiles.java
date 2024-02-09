//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2018.07.30 a las 02:10:08 PM CEST 
//


package dnse3.orchestration.auxiliar.project;

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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="basefile" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="additionalfile" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
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
