package dnse3.scale.server;

import java.util.Properties;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import dnse3.scale.Scale;

/**
 * @author GSIC gsic.uva.es
 * @version 20191204
 */
public class ScaleApp extends Application {

	/**
	 * Instancia de la clase principal del Servicio de Monitorizaci�n y Escalado
	 */
	private Scale scale;

	/**
	 * Constructor de la clase. M�todo que crea el bucle por el cual se comprueba el
	 * valor de la m�trica que env�a Colas
	 * 
	 * @param properties Propiedades clave valor que necesita el servicio
	 */
	public ScaleApp(Properties properties) {
		this.scale = new Scale(properties);
	}

	/**
	 * M�todo para recuperar la clase principal del servicio.
	 * 
	 * @return Clase principal del servicio. Es donde se ejecuta el bucle por el
	 *         cual se comprueba el valor de la m�trica de Colas.
	 */
	public Scale getScale() {
		return scale;
	}

	/**
	 * 
	 */
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/v0.1/{queue}", ManageQueue.class);
		return router;
	}
}
