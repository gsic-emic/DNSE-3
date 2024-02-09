package dnse3.scale;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import dnse3.scale.auxiliar.ComDocker;
import dnse3.scale.auxiliar.ComOpenStack;
import dnse3.scale.server.ScaleComponent;

/**
 * Clase principal del Servicio de Monitorizaci�n y Escalado. Desde esta clase
 * se lanza el servidor que escucha la m�trica de colas. Tambi�n se inicia el
 * bucle de comprobaci�n del valor de la m�trica.
 * 
 * @author GSIC gsic.uva.es
 * @version 20191220
 */
public class Scale extends Thread {
	/** Instante del cual se tiene almacenado el valor de la longitud de la cola */
	private long instanteAnterior;
	/** La longitud de la cola que se notifica desde el Servicio de Colas */
	private double valorActualCola;
	/** Formato de fecha y hora para indicar el instante de la m�trica */
	// private SimpleDateFormat simpleDateFormat;
	/** N�mero m�ximo de m�quinas virtuales que pueden formar el cl�ster */
	private int maxNodes;
	/** N�mero m�ximo de contenedores que se pueden ejecutar en cada nodo. */
	private int containerPerNode;
	/** Nombre del servicio que se desea escalar. */
	private String servicioPD;
	/**
	 * Umbral con el que se decide si aumentar o disminuir el n�mero de contenedores
	 * que se est�n ejecutando en el cl�ster.
	 */
	private int umbral;
	/**
	 * N�mero m�ximo de contenedores que se pueden ejecutar en el cl�ster con todas
	 * las m�quinas activas.
	 */
	private int maxContainers;
	/** N�mero de m�quinas agregadas al cl�ster mediante el escalado */
	private int numeroMaquina;
	/** Objeto para realizar las comunicaciones con el cliente de Docker */
	private ComDocker comDocker;
	/**
	 * M�todo para realizar las comunicaciones con OpenStack para el escalado de MV
	 */
	private ComOpenStack comOpenStack;
	/**
	 * Nombre con el que se identificar�n las m�quinas virtuales que se creen a
	 * mayores de las existentes en el cl�ster.
	 */
	public final String nombreBase = "worker";
	/** Logger del Servicio de Escalado. */
	private static final Logger logger = Logger.getLogger("DNSE3ScaleLogger");

	/**
	 * Constructro de la clase. Establece los valores b�sicos para que el sistema
	 * comience a autogestionarse
	 * 
	 * @param properties Valores necesarios para llevar a cabo la conexi�n con
	 *                   Docker y OpenStack
	 */
	public Scale(Properties properties) {
		instanteAnterior = 0;
		valorActualCola = 0;
		// simpleDateFormat = new SimpleDateFormat("yyyy MM dd - HH:mm:ss");
		maxNodes = Integer.parseInt(properties.getProperty("MAXMV"));
		containerPerNode = Integer.parseInt(properties.getProperty("MAXDOCKERMV"));
		servicioPD = properties.getProperty("SERVICE");
		umbral = Integer.parseInt(properties.getProperty("THRESHOLD"));
		maxContainers = containerPerNode * maxNodes;
		numeroMaquina = 0;

		comDocker = new ComDocker(properties.getProperty("MANAGER_URL"), servicioPD);
		comOpenStack = new ComOpenStack(properties);
		this.start();
	}

	/**
	 * M�todo que se ejecuta en bucle para realizar las comprobaciones necesarias
	 * para adecuar los recursos activos a la cantidad de trabajo pendiente.
	 * Gestiona las esperas entre interaciones.
	 */
	@Override
	public void run() {
		boolean esperaContenedores = false, esperaNodos = false;
		int multi = 1, expo = 1;
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			logger.error("| run | " + e.toString() + " MSG: " + e.getMessage());
		}
		int numero = comDocker.nContainer();
		logger.info("| run | Contenedores activos " + numero);
		numero = comDocker.nNodes();
		logger.info("| run | M�quinas activas " + numero);
		while (true) {
			try {
				esperaContenedores = calculaNConte();
				esperaNodos = calculaNMaq();

				/*
				 * Si no se produce un escalado, la espera aumenta hasta un m�ximo de 8 segundos
				 */
				if (esperaContenedores || esperaNodos)
					multi = 1;
				else if (multi < 3)
					++multi;

				expo = (int) Math.pow(2, multi);
				Thread.sleep((esperaContenedores || esperaNodos) ? 1000 : expo * 1000);
			} catch (Exception e) {
				logger.error("| run | " + e.toString() + " MSG: " + e.getMessage());
			}
		}
	}

	/**
	 * M�todo para escalar el n�mero de contenedores activos que se est�n ejecutando
	 * en el cl�ster. El m�todo de escalado depender� si el servicio es
	 * "simulacion_simulator" � "report_report"
	 * 
	 * @return Devuelve 'verdadero' si el sistema se ha escalado y 'false' en caso
	 *         contrario.
	 * @throws InterruptedException Posible excepci�n lanzada por la espera de medio
	 *                              segundo.
	 */
	private boolean calculaNConte() {
		boolean espera = false;
		int contenedoresServicio = comDocker.nContainer();
		double operacion;

		switch (servicioPD) {
		case "simulacion_simulator":
			operacion = ((valorActualCola == 0) ? 1 : valorActualCola) / contenedoresServicio;

			if (contenedoresServicio < maxContainers) {
				if (operacion >= umbral) {
					if (contenedoresServicio < (comDocker.nNodes() * containerPerNode)) {
						logger.debug("| Contenedores activos | Comienza el aumento de contenedores");
						++contenedoresServicio;
						if (comDocker.scaleContainer(1) == contenedoresServicio)
							espera = true;
					}
				}
			}

			if (operacion < umbral / 2) {
				if (contenedoresServicio > 1) {
					contenedoresServicio = (int) Math.ceil(((double) contenedoresServicio) / 2);
					logger.debug("| Contenedores activos | Comienza la reducci�n de contenedores");
					if (comDocker.defineNumberContainer(contenedoresServicio) == contenedoresServicio)
						;
					espera = true;
				}
			}
			break;

		case "report_report":
			if (contenedoresServicio == 0) {
				operacion = ((valorActualCola > 0) ? 1 : 0);
			} else {
				operacion = valorActualCola / contenedoresServicio;
			}

			if (contenedoresServicio < maxContainers) {
				if (contenedoresServicio < valorActualCola / umbral) {
					if (contenedoresServicio < (comDocker.nNodes() * containerPerNode)) {
						logger.debug("| Contenedores activos | Comienza el aumento de contenedores");
						int intermedio = (int) (Math.ceil(valorActualCola / umbral));
						contenedoresServicio = (intermedio > maxContainers) ? maxContainers : intermedio;
						if (comDocker.defineNumberContainer(contenedoresServicio) == contenedoresServicio)
							espera = true;
					}
				}
			}

			// if(operacion < umbral)
			if (contenedoresServicio > valorActualCola)
				if (contenedoresServicio > 0) {
					logger.debug("| Contenedores activos | Comienza la reducci�n de contenedores");
					contenedoresServicio = (int) valorActualCola;
					if (comDocker.defineNumberContainer(contenedoresServicio) == contenedoresServicio)
						espera = true;
				}
			break;
		default:
			break;
		}
		if (espera) {
			logger.info("| Contenedores activos | " + contenedoresServicio);
		}
		return espera;
	}

	/**
	 * M�todo para escalar el n�mero de m�quinas virtuales que est�n activas en el
	 * cl�ster
	 * 
	 * @return 'true' si el sistema se ha escalado. 'false' si no se ha escalado.
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private boolean calculaNMaq() throws IOException, InterruptedException {
		int nMaqActivas = comDocker.nNodes();
		int nContActivos = comDocker.nContainer();

		if (nMaqActivas < maxNodes) {
			// Podemos aumentar el cl�ster
			if (nContActivos == nMaqActivas * containerPerNode) {
				// Hay que aumentar el n�mero de m�quinas que forman el cl�ster
				nContActivos = nContActivos + (int) Math.ceil(((double) containerPerNode / 2));
				double operacion = ((valorActualCola == 0) ? 1 : valorActualCola) / nContActivos;
				if (operacion > umbral) {
					logger.info("| M�quinas activas | " + nMaqActivas);
					logger.debug("| M�quinas activas | Comienza el aumento");
					int intentos = 0;
					while ((comOpenStack.crearStack(nombreBase + numeroMaquina) == false) && intentos < 3)
						intentos++;
					if (intentos < 3) {
						++numeroMaquina;
						int esperaMaxima = 300000;
						do {
							Thread.sleep(500);
							if (esperaMaxima == 0) {
								logger.warn(
										"esperaMaxima. No se ha conseguido aumentar el n�mero de MV en el sistema. Se ha intentado durante 5 minutos. Se establece el n�mero m�ximo como el actual. Comprobar stacks en OpenStack");
								maxNodes = nMaqActivas;
								maxContainers = maxNodes * containerPerNode;
								break;
							}
							--esperaMaxima;
						} while (comDocker.nNodes() < (nMaqActivas + 1));
						logger.debug("| M�quinas activas | " + (nMaqActivas + 1) + " Finaliza el aumento");
						return true;
					} else {
						logger.warn("| M�quinas activas | No se ha conseguido aumentar el n�mero de m�quinas");
						return false;
					}
				} else {
					return false;
				}
			}
		}

		if (nContActivos < (nMaqActivas - 1) * containerPerNode) {
			// Tenemos que reducir en una unidad el n�mero de m�quinas activas que forman el
			// cl�ster
			if (numeroMaquina > 0) {
				logger.info("| M�quinas activas | " + nMaqActivas);
				--numeroMaquina;
				logger.debug("| M�quinas activas | Comienza la reducci�n");
				// ejecuta("openstack stack delete " + nombreBase + numeroMaquina);
				int intentos = 0;
				while ((comOpenStack.eliminarStack(nombreBase + numeroMaquina) == false) && intentos < 3)
					intentos++;
				if (intentos < 3) {
					do {
						Thread.sleep(500);
					} while (comDocker.nNodes() > (nMaqActivas - 1));
					logger.debug("| M�quinas activas | " + (nMaqActivas - 1) + " Finalizada la reducci�n");
					return true;
				} else {
					++numeroMaquina;
					logger.warn("| M�quinas activas | No se ha conseguido reducir");
					return false;
				}

			}
		}

		if (nContActivos > maxContainers) {
			// Tenemos que reducir el n�mero de contenedores activos en el cl�ster
			comDocker.defineNumberContainer(nMaqActivas * containerPerNode);
			return true;
		}

		return false;
	}

	/**
	 * M�todo para almacenar el tama�o de la cola que se notifica desde el Servicio
	 * de Colas. Se actualiza el valor del tama�o de cola cuando el timestamp que
	 * nos llegue sea mayor que el que tenemos almacenado.
	 * 
	 * @param timeStamp     Timestamp con el instante de la m�trica
	 * @param numberOfTasks Valor del tama�o de cola
	 */
	public void setNumberTasks(String timeStamp, String numberOfTasks) {
		try {
			long nuevoInstante = Long.parseLong(timeStamp);
			if (nuevoInstante > instanteAnterior) {
				instanteAnterior = nuevoInstante;
				valorActualCola = Double.parseDouble(numberOfTasks);
				logger.info("| Size Queue | " + (int) valorActualCola);
			}
		} catch (NumberFormatException e) {
			logger.error("| Size Queue | " + e.toString() + "MSG: " + e.getMessage());
		}
	}

	/**
	 * M�todo que inicia todo el Servicio de Monitorizaci�n y Escalado. Comprueba
	 * que el fichero con las propiedades de escalado existe Realiza la llamada al
	 * m�todo que crea el servidor y comprueba el estado del servicio a monitorizar
	 * 
	 * @param args Argumentos de entrada. No son necesarios.
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			File f = new File("scale.properties");
			if (!f.exists()) {
				logger.error("| main | El Servicio de Escalado necesita el fichero scale.properties");
				System.exit(-1);
			}
			Properties properties = new Properties();
			try {
				InputStream is = new FileInputStream(f);
				properties.load(is);
			} catch (IOException e) {
			} // Ya se ha comprobado que el fichero existe
			try {
				new ScaleComponent(properties);
			} catch (Exception e) {
				logger.error("| main | " + e.toString() + "MSG: " + e.getMessage());
				System.exit(-1);
			}
		} else {
			logger.error("| main | El Servicio de Escalado no necesita ning�n argumento de entrada");
			System.exit(-1);
		}
	}
}