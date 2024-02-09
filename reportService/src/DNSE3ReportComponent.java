import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Protocol;

import dnse3.common.CloudManager;

public class DNSE3ReportComponent extends Component{
	
	public static void main(String[] args) throws Exception{
		new DNSE3ReportComponent().start();
	}
	
	public DNSE3ReportComponent(){
		Server server = getServers().add(Protocol.HTTP,8082);
		server.getContext().getParameters().set("tracing","true");
		CloudManager cm = new CloudManager("2558af5e16ad4449980524caabc8694e", "NubeSergio");
		getDefaultHost().attachDefault(new DNSE3ReportApplication(new ReportQueue(cm), cm));
	}

}
