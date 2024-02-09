//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2019.03.27 a las 05:52:49 PM CET 
//


package test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


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
 *         &lt;choice minOccurs="0">
 *           &lt;element name="greaterthan" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *           &lt;element name="greaterthanorequalto" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="lessthan" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *           &lt;element name="lessthanorequalto" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;/choice>
 *         &lt;element name="defaultvalue" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "greaterthan",
    "greaterthanorequalto",
    "lessthan",
    "lessthanorequalto",
    "defaultvalue"
})
@XmlRootElement(name = "rationaltype")
public class Rationaltype {

    protected Float greaterthan;
    protected Float greaterthanorequalto;
    protected Float lessthan;
    protected Float lessthanorequalto;
    protected float defaultvalue;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Obtiene el valor de la propiedad greaterthan.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getGreaterthan() {
        return greaterthan;
    }

    /**
     * Define el valor de la propiedad greaterthan.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setGreaterthan(Float value) {
        this.greaterthan = value;
    }

    /**
     * Obtiene el valor de la propiedad greaterthanorequalto.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getGreaterthanorequalto() {
        return greaterthanorequalto;
    }

    /**
     * Define el valor de la propiedad greaterthanorequalto.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setGreaterthanorequalto(Float value) {
        this.greaterthanorequalto = value;
    }

    /**
     * Obtiene el valor de la propiedad lessthan.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLessthan() {
        return lessthan;
    }

    /**
     * Define el valor de la propiedad lessthan.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLessthan(Float value) {
        this.lessthan = value;
    }

    /**
     * Obtiene el valor de la propiedad lessthanorequalto.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getLessthanorequalto() {
        return lessthanorequalto;
    }

    /**
     * Define el valor de la propiedad lessthanorequalto.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setLessthanorequalto(Float value) {
        this.lessthanorequalto = value;
    }

    /**
     * Obtiene el valor de la propiedad defaultvalue.
     * 
     */
    public float getDefaultvalue() {
        return defaultvalue;
    }

    /**
     * Define el valor de la propiedad defaultvalue.
     * 
     */
    public void setDefaultvalue(float value) {
        this.defaultvalue = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
