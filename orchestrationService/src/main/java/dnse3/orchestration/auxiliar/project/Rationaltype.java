//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2018.07.30 a las 02:10:08 PM CEST 
//


package dnse3.orchestration.auxiliar.project;

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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="greaterthan" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *           &lt;element name="greaterthanorequalto" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *         &lt;/choice&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="lessthan" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *           &lt;element name="lessthanorequalto" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="defaultvalue" type="{http://www.w3.org/2001/XMLSchema}float"/&gt;
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
@XmlRootElement(name = "rationaltype")
public class Rationaltype {

    protected Float greaterthan;
    protected Float greaterthanorequalto;
    protected Float lessthan;
    protected Float lessthanorequalto;
    protected Float defaultvalue;
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
    public Float getDefaultvalue() {
        return defaultvalue;
    }

    /**
     * Define el valor de la propiedad defaultvalue.
     * 
     */
    public void setDefaultvalue(Float value) {
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
     * Método para comprobar si un parámetro del XML es válido
     * @return Devuelve true si es válido y false si no lo es
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
