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
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}stringtype" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}integertype" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}rationaltype" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://gsic.tel.uva.es/dnse3/spd_v0p9}seedtype" maxOccurs="unbounded" minOccurs="0"/>
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
    "stringtype",
    "integertype",
    "rationaltype",
    "seedtype"
})
@XmlRootElement(name = "parametertypes")
public class Parametertypes {

    protected List<Stringtype> stringtype;
    protected List<Integertype> integertype;
    protected List<Rationaltype> rationaltype;
    protected List<Seedtype> seedtype;

    /**
     * Gets the value of the stringtype property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the stringtype property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStringtype().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Stringtype }
     * 
     * 
     */
    public List<Stringtype> getStringtype() {
        if (stringtype == null) {
            stringtype = new ArrayList<Stringtype>();
        }
        return this.stringtype;
    }

    /**
     * Gets the value of the integertype property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the integertype property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIntegertype().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Integertype }
     * 
     * 
     */
    public List<Integertype> getIntegertype() {
        if (integertype == null) {
            integertype = new ArrayList<Integertype>();
        }
        return this.integertype;
    }

    /**
     * Gets the value of the rationaltype property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rationaltype property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRationaltype().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Rationaltype }
     * 
     * 
     */
    public List<Rationaltype> getRationaltype() {
        if (rationaltype == null) {
            rationaltype = new ArrayList<Rationaltype>();
        }
        return this.rationaltype;
    }

    /**
     * Gets the value of the seedtype property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the seedtype property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSeedtype().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Seedtype }
     * 
     * 
     */
    public List<Seedtype> getSeedtype() {
        if (seedtype == null) {
            seedtype = new ArrayList<Seedtype>();
        }
        return this.seedtype;
    }

}
