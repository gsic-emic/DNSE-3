package dnse3.orchestration.auxiliar;

import java.time.Instant;

import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.message.ObjectArrayMessage;

import dnse3.orchestration.server.DNSE3OrchestrationApplication;

/**
 * Clase para registrar acciones en el log de DNSE3 del servicio de orquestación. 
 * Actualmente este registro se realiza en formato CSV
 * @author Pablo
 * @version 20210208
 *
 */
public class RegistraLog {
	/** Nueva simulación */
	public static final String nueSimula = "newSim";
	/** Modificación de una simulación existente */
	public static final String modSimula = "modSim";
	/** Inicio de una simulación */
	public static final String iniSimula = "startSim";
	/** Pausa de una simulación */
	public static final String pauSimula = "pauSim";
	/** Detención de una simulación */
	public static final String stoSimula = "stopSim";
	/** Borrado de una simulación */
	public static final String borSimula = "delSim"; 
	/** Descarga de un modelo de red */
	public static final String desModelo = "dowMod";
	/** Descarga de los resultados de una simulación */
	public static final String desResult = "dowRes";
	
	/**
	 * Creaciones y modificaciones de una simulación
	 * 
	 * @param idAccion Id de la acción que se va a registrar.
	 * @param idGrupo Id del grupo que ha iniciado la acción.
	 * @param idModelo Id del proyecto donde se ha realizado la acción.
	 * @param idSimula Id de la simulación donde se ha realizado la acción.
	 * @param tiempo Tiempo que se ha tomado el usuario para realizar la simulación. Si este número es negativo no se incluye en el log
	 * @param extra Parámetros extra que se quieran registrar.
	 */
	public static void registra(String idAccion, String idGrupo, Object idModelo, Object idSimu, double tiempo, String extra) {
		ObjectArrayMessage objectArrayMessage;
		extra = extra.trim();
		if(tiempo > 0) 
			objectArrayMessage = new ObjectArrayMessage(Instant.now(), idAccion, idGrupo, idModelo.toString(), idSimu.toString(), tiempo, extra);
		else
			objectArrayMessage = new ObjectArrayMessage(Instant.now(), idAccion, idGrupo, idModelo.toString(), idSimu.toString(), "", extra);
		
		DNSE3OrchestrationApplication.dameLogger().info(
				MarkerManager.getMarker("DNSE3"),
				objectArrayMessage);
	}
	
	/**
	 * Inicio de una simulación
	 * 
	 * @param idAccion Id de la acción que se va a registrar.
	 * @param idGrupo Id del grupo que ha iniciado la acción.
	 * @param idModelo Id del proyecto donde se ha realizado la acción.
	 * @param idSimula Id de la simulación donde se ha realizado la acción.
	 * @param extra Parámetros extra que se quieran registrar.
	 */
	public static void registra(String idAccion, String idGrupo, Object idModelo, Object idSimu, String extra) {
		registra(idAccion, idGrupo, idModelo, idSimu, -1, extra);
	}
	
	/**
	 * Eliminaciones de una simulación y descarga de resultados de una simulación
	 * 
	 * @param idAccion Id de la acción que se va a registrar.
	 * @param idGrupo Id del grupo que ha iniciado la acción.
	 * @param idModelo Id del proyecto donde se ha realizado la acción.
	 * @param idSimula Id de la simulación donde se ha realizado la acción.
	 */
	public static void registra(String idAccion, String idGrupo, Object idModelo, Object idSimula) {
		registra(idAccion, idGrupo, idModelo, idSimula, "");
	}

	/**
	 * Descarga de un modelo
	 * 
	 * @param idAccion Id de la acción que se va a registrar.
	 * @param idGrupo Id del grupo que ha iniciado la acción.
	 * @param idModelo Id del proyecto donde se ha realizado la acción.
	 */
	public static void registra(String idAccion, String idGrupo, Object idModelo) {
		registra(idAccion, idGrupo, idModelo, "");
	}
	
	
	

}
