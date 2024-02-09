package dnse3.scale.server;

import java.util.Properties;

import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.ext.jetty.HttpServerHelper;

/**
 * @author GSIC gsic.uva.es
 * @version 20191218
 */
public class ScaleComponent extends Component {

	public ScaleComponent(Properties properties) throws Exception {
		Server server = getServers().add(Protocol.HTTP, Integer.parseInt(properties.getProperty("PORT")));
		getClients().add(Protocol.HTTP);
		getClients().add(Protocol.HTTPS);
		server.getContext().getParameters().set("tracing", "true");
		// server.getContext().getParameters().set("maxQueued", "-1");

		getDefaultHost().attachDefault(new ScaleApp(properties));
		new HttpServerHelper(server);
		server.start();
	}
}
