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
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}tabbedfile" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="tracefile" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}resultfile" maxOccurs="unbounded" minOccurs="0"/>
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
