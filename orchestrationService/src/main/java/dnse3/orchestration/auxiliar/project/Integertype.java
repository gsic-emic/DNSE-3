//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2018.07.30 a las 02:10:08 PM CEST 
//


package dnse3.orchestration.auxiliar.project;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="greaterthan" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *           &lt;element name="greaterthanorequalto" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;/choice&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="lessthan" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *           &lt;element name="lessthanorequalto" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="defaultvalue" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
@XmlRootElement(name = "integertype")
public class Integertype {

    protected BigInteger greaterthan;
    protected BigInteger greaterthanorequalto;
    protected BigInteger lessthan;
    protected BigInteger lessthanorequalto;
    @XmlElement(required = true)
    protected BigInteger defaultvalue;
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
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGreaterthan() {
        return greaterthan;
    }

    /**
     * Define el valor de la propiedad greaterthan.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGreaterthan(BigInteger value) {
        this.greaterthan = value;
    }

    /**
     * Obtiene el valor de la propiedad greaterthanorequalto.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getGreaterthanorequalto() {
        return greaterthanorequalto;
    }

    /**
     * Define el valor de la propiedad greaterthanorequalto.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setGreaterthanorequalto(BigInteger value) {
        this.greaterthanorequalto = value;
    }

    /**
     * Obtiene el valor de la propiedad lessthan.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLessthan() {
        return lessthan;
    }

    /**
     * Define el valor de la propiedad lessthan.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLessthan(BigInteger value) {
        this.lessthan = value;
    }

    /**
     * Obtiene el valor de la propiedad lessthanorequalto.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getLessthanorequalto() {
        return lessthanorequalto;
    }

    /**
     * Define el valor de la propiedad lessthanorequalto.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setLessthanorequalto(BigInteger value) {
        this.lessthanorequalto = value;
    }

    /**
     * Obtiene el valor de la propiedad defaultvalue.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getDefaultvalue() {
        return defaultvalue;
    }

    /**
     * Define el valor de la propiedad defaultvalue.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setDefaultvalue(BigInteger value) {
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

    /**
     * Método para verificar el formato de los parámetros indicados en el XML
     * @return Devuelve true cuando el parámetro es válido y false cuando no cumple alguna condición.
     */
	public boolean verify() {
		if((greaterthan!=null && greaterthanorequalto != null) || (lessthan!=null && lessthanorequalto!=null))
			return false;
		
		if(greaterthan!=null){
			if(lessthan!=null){
				if(greaterthan.compareTo(lessthan)>=0)
					return false;
				if(greaterthan.compareTo(defaultvalue)>=0 || lessthan.compareTo(defaultvalue)<=0)
					return false;
			}
			else if(lessthanorequalto!=null){
				if(greaterthan.compareTo(lessthanorequalto)>=0)
					return false;
				if(greaterthan.compareTo(defaultvalue)>=0 || lessthanorequalto.compareTo(defaultvalue)<0)
					return false;
			}
			else{
				if(greaterthan.compareTo(defaultvalue)>=0)
					return false;
			}
		}
		else if(greaterthanorequalto!=null){
			if(lessthan!=null){
				if(greaterthanorequalto.compareTo(lessthan)>=0)
					return false;
				if(greaterthanorequalto.compareTo(defaultvalue)>0 || lessthan.compareTo(defaultvalue)<=0)
					return false;
			}
			else if(lessthanorequalto!=null){
				if(greaterthanorequalto.compareTo(lessthanorequalto)>0)
					return false;
				if(greaterthanorequalto.compareTo(defaultvalue)>0 || lessthanorequalto.compareTo(defaultvalue)<0)
					return false;
			}
			else{
				if(greaterthanorequalto.compareTo(defaultvalue)>0)
					return false;
			}
		}
		else{
			if(lessthan!=null){
				if(lessthan.compareTo(defaultvalue)<=0)
					return false;
			}
			else if(lessthanorequalto!=null){
				if(lessthanorequalto.compareTo(defaultvalue)<0)
					return false;
			}
		}
		
		return true;
	}

}
