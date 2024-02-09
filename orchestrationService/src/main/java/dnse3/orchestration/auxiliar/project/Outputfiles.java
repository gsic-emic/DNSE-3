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
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}tabbedfile" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="tracefile" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}resultfile" maxOccurs="unbounded" minOccurs="0"/&gt;
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
    "tabbedfile",
    "tracefile",
    "resultfile"
})
@XmlRootElement(name = "outputfiles")
public class Outputfiles {

    protected List<Tabbedfile> tabbedfile;
    protected List<String> tracefile;
    protected List<Resultfile> resultfile;

    /**
     * Gets the value of the tabbedfile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tabbedfile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTabbedfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Tabbedfile }
     * 
     * 
     */
    public List<Tabbedfile> getTabbedfile() {
        if (tabbedfile == null) {
            tabbedfile = new ArrayList<Tabbedfile>();
        }
        return this.tabbedfile;
    }

    /**
     * Gets the value of the tracefile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tracefile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTracefile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTracefile() {
        if (tracefile == null) {
            tracefile = new ArrayList<String>();
        }
        return this.tracefile;
    }

    /**
     * Gets the value of the resultfile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the resultfile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getResultfile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Resultfile }
     * 
     * 
     */
    public List<Resultfile> getResultfile() {
        if (resultfile == null) {
            resultfile = new ArrayList<Resultfile>();
        }
        return this.resultfile;
    }

}
