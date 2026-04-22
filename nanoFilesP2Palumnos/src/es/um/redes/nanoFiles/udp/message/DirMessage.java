package es.um.redes.nanoFiles.udp.message;




/**
 * Clase que modela los mensajes del protocolo de comunicación entre pares para
 * implementar el explorador de ficheros remoto (servidor de ficheros). Estos
 * mensajes son intercambiados entre las clases DirectoryServer y
 * DirectoryConnector, y se codifican como texto en formato "campo:valor".
 * 
 * @author rtitos
 *
 */
public class DirMessage {
	public static final int PACKET_MAX_SIZE = 65507; // 65535 - 8 (UDP header) - 20 (IP header)

	private static final char DELIMITER = ':'; // Define el delimitador
	private static final char END_LINE = '\n'; // Define el carácter de fin de línea

	/**
	 * Nombre del campo que define el tipo de mensaje (primera línea)
	 */
	private static final String FIELDNAME_OPERATION = "operation";
	/*
	 * TODO: (Boletín MensajesASCII) Definir de manera simbólica los nombres de
	 * todos los campos que pueden aparecer en los mensajes de este protocolo
	 * (formato campo:valor)
	 */
	private static final String FIELDNAME_PROTOCOL_ID = "protocolid"; // ← TODO EN MINÚSCULAS
	private static final String FIELDNAME_ERROR_CODE = "errorcode";    // ← TODO EN MINÚSCULAS
	private static final String FIELDNAME_ERROR_MESSAGE = "errormessage"; // ← TODO EN MINÚSCULAS PUES El servidor en fromString() convierte el nombre del campo a minúsculas
	//EJE 7 ASCII																		//En el switch busco "protocolId" pero llega "protocolid" → NO coincide → va al default → PANIC
	private static final String FIELDNAME_FILELIST = "filelist";
	private static final String FIELDNAME_NICKNAME = "nickname";
	private static final String FIELDNAME_PORT = "port";
	private static final String FIELDNAME_PEERLIST = "peerlist";



	/**
	 * Tipo del mensaje, de entre los tipos definidos en PeerMessageOps.
	 */
	private String operation = DirMessageOps.OPERATION_INVALID;
	/**
	 * Identificador de protocolo usado, para comprobar compatibilidad del directorio.
	 */
	private String protocolId;
	/*
	 * TODO: (Boletín MensajesASCII) Crear un atributo correspondiente a cada uno de
	 * los campos de los diferentes mensajes de este protocolo.
	 */
	
	private String errorCode;
	private String errorMessage;
	//EJE 7 ASCII
	private String fileList;
	private String nickname;
	private int port;
	private String peerList;




	public DirMessage(String op) {
		operation = op;
	}

	/*
	 * TODO: (Boletín MensajesASCII) Crear diferentes constructores adecuados para
	 * construir mensajes de diferentes tipos con sus correspondientes argumentos
	 * (campos del mensaje)
	 */




	public String getOperation() {
		return operation;
	}

	/*
	 * TODO: (Boletín MensajesASCII) Crear métodos getter y setter para obtener los
	 * valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public void setProtocolID(String protocolIdent) {
		if (!operation.equals(DirMessageOps.OPERATION_PING)) {
			throw new RuntimeException(
					"DirMessage: setProtocolId called for message of unexpected type (" + operation + ")");
		}
		protocolId = protocolIdent;
	}

	public String getProtocolId() {



		return protocolId;
	}
	
	// EJE 7 ASCII El problema está en el método setErrorCode() de DirMessage.java. Está validando que solo se puede usar con OPERATION_PING_BAD, 
	// pero también se necesita para otros mensajes de error como OPERATION_FILELIST_BAD.
	public void setErrorCode(String code) {
		if (!operation.endsWith("Bad")) {
			throw new RuntimeException(
					"DirMessage: setErrorCode called for message of unexpected type (" + operation + ")");
		}
		errorCode = code;
	}

	public String getErrorCode() {
		if (!operation.endsWith("Bad")) {
			throw new RuntimeException(
					"DirMessage: getErrorCode called for message of unexpected type (" + operation + ")");
		}
		return errorCode;
	}

	public void setErrorMessage(String message) {
		if (!operation.endsWith("Bad")) {
			throw new RuntimeException(
					"DirMessage: setErrorMessage called for message of unexpected type (" + operation + ")");
		}
		errorMessage = message;
	}

	public String getErrorMessage() {
		if (!operation.endsWith("Bad")) {
			throw new RuntimeException(
					"DirMessage: getErrorMessage called for message of unexpected type (" + operation + ")");
		}
		return errorMessage;
	}
	
	//EJE 7 ASCII
	

	// Getters y setters para FILELIST
	public void setFileList(String list) {
		if (!operation.equals(DirMessageOps.OPERATION_FILELIST_OK)) {
			throw new RuntimeException(
					"DirMessage: setFileList called for message of unexpected type (" + operation + ")");
		}
		fileList = list;
	}

	public String getFileList() {
		if (!operation.equals(DirMessageOps.OPERATION_FILELIST_OK)) {
			throw new RuntimeException(
					"DirMessage: getFileList called for message of unexpected type (" + operation + ")");
		}
		return fileList;
	}

	// Getters y setters para REGISTER
	public void setNickname(String nick) {
		if (!operation.equals(DirMessageOps.OPERATION_REGISTER)) {
			throw new RuntimeException(
					"DirMessage: setNickname called for message of unexpected type (" + operation + ")");
		}
		nickname = nick;
	}

	public String getNickname() {
		if (!operation.equals(DirMessageOps.OPERATION_REGISTER)) {
			throw new RuntimeException(
					"DirMessage: getNickname called for message of unexpected type (" + operation + ")");
		}
		return nickname;
	}

	public void setPort(int tcpPort) {
		if (!operation.equals(DirMessageOps.OPERATION_REGISTER)) {
			throw new RuntimeException(
					"DirMessage: setPort called for message of unexpected type (" + operation + ")");
		}
		port = tcpPort;
	}

	public int getPort() {
		if (!operation.equals(DirMessageOps.OPERATION_REGISTER)) {
			throw new RuntimeException(
					"DirMessage: getPort called for message of unexpected type (" + operation + ")");
		}
		return port;
	}

	// Getters y setters para PEERLIST
	public void setPeerList(String list) {
		if (!operation.equals(DirMessageOps.OPERATION_PEERLIST_OK)) {
			throw new RuntimeException(
					"DirMessage: setPeerList called for message of unexpected type (" + operation + ")");
		}
		peerList = list;
	}

	public String getPeerList() {
		if (!operation.equals(DirMessageOps.OPERATION_PEERLIST_OK)) {
			throw new RuntimeException(
					"DirMessage: getPeerList called for message of unexpected type (" + operation + ")");
		}
		return peerList;
	}



	/**
	 * Método que convierte un mensaje codificado como una cadena de caracteres, a
	 * un objeto de la clase PeerMessage, en el cual los atributos correspondientes
	 * han sido establecidos con el valor de los campos del mensaje.
	 * 
	 * @param message El mensaje recibido por el socket, como cadena de caracteres
	 * @return Un objeto PeerMessage que modela el mensaje recibido (tipo, valores,
	 *         etc.)
	 */
	public static DirMessage fromString(String message) {
		/*
		 * TODO: (Boletín MensajesASCII) Usar un bucle para parsear el mensaje línea a
		 * línea, extrayendo para cada línea el nombre del campo y el valor, usando el
		 * delimitador DELIMITER, y guardarlo en variables locales.
		 */

		// System.out.println("DirMessage read from socket:");
		// System.out.println(message);
		String[] lines = message.split(END_LINE + "");
		// Local variables to save data during parsing
		DirMessage m = null;



		for (String line : lines) {
			// Saltar líneas vacías (incluida la línea final del mensaje)
			if (line.isEmpty()) {
				continue;
			}
			
			int idx = line.indexOf(DELIMITER); // Posición del delimitador
			
			// Verificar que el delimitador existe en la línea
			if (idx == -1) {
				System.err.println("WARNING: DirMessage.fromString - line without delimiter: \"" + line + "\"");
				continue; // Saltar esta línea
			}
			
			String fieldName = line.substring(0, idx).toLowerCase(); // minúsculas
			String value = line.substring(idx + 1).trim();

			switch (fieldName) {
			case FIELDNAME_OPERATION: {
				assert (m == null);
				m = new DirMessage(value);
				break;
			}
			case FIELDNAME_PROTOCOL_ID: {
				assert (m != null);
				m.protocolId = value;
				break;
			}
			case FIELDNAME_ERROR_CODE: {
				assert (m != null);
				m.errorCode = value;
				break;
			}
			case FIELDNAME_ERROR_MESSAGE: {
				assert (m != null);
				m.errorMessage = value;
				break;
			}
			//EJE 7 ASCII
			case FIELDNAME_FILELIST: {
				assert (m != null);
				m.fileList = value;
				break;
			}
			case FIELDNAME_NICKNAME: {
				assert (m != null);
				m.nickname = value;
				break;
			}
			case FIELDNAME_PORT: {
				assert (m != null);
				m.port = Integer.parseInt(value);
				break;
			}
			case FIELDNAME_PEERLIST: {
				assert (m != null);
				m.peerList = value;
				break;
			}




			default:
				System.err.println("PANIC: DirMessage.fromString - message with unknown field name " + fieldName);
				System.err.println("Message was:\n" + message);
				System.exit(-1);
			}
		}




		return m;
	}

	/**
	 * Método que devuelve una cadena de caracteres con la codificación del mensaje
	 * según el formato campo:valor, a partir del tipo y los valores almacenados en
	 * los atributos.
	 * 
	 * @return La cadena de caracteres con el mensaje a enviar por el socket.
	 */
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append(FIELDNAME_OPERATION + DELIMITER + operation + END_LINE); // Construimos el campo
		/*
		 * TODO: (Boletín MensajesASCII) En función de la operación del mensaje, crear
		 * una cadena la operación y concatenar el resto de campos necesarios usando los
		 * valores de los atributos del objeto.
		 */
		
		switch (operation) {
		case DirMessageOps.OPERATION_PING:
			sb.append(FIELDNAME_PROTOCOL_ID + DELIMITER + protocolId + END_LINE);
			break;
		case DirMessageOps.OPERATION_PING_BAD:
			sb.append(FIELDNAME_ERROR_CODE + DELIMITER + errorCode + END_LINE);
			sb.append(FIELDNAME_ERROR_MESSAGE + DELIMITER + errorMessage + END_LINE);
			break;
		case DirMessageOps.OPERATION_PING_OK:
			// No hay campos adicionales
			break;
		// EJE 7 ASCII
		case DirMessageOps.OPERATION_FILELIST:
			// No hay campos adicionales
			break;
		case DirMessageOps.OPERATION_FILELIST_OK:
			if (fileList != null && !fileList.isEmpty()) {
				sb.append(FIELDNAME_FILELIST + DELIMITER + fileList + END_LINE);
			}
			break;
		case DirMessageOps.OPERATION_FILELIST_BAD:
			sb.append(FIELDNAME_ERROR_CODE + DELIMITER + errorCode + END_LINE);
			sb.append(FIELDNAME_ERROR_MESSAGE + DELIMITER + errorMessage + END_LINE);
			break;
		case DirMessageOps.OPERATION_REGISTER:
			sb.append(FIELDNAME_NICKNAME + DELIMITER + nickname + END_LINE);
			sb.append(FIELDNAME_PORT + DELIMITER + port + END_LINE);
			break;
		case DirMessageOps.OPERATION_REGISTER_OK:
			// No hay campos adicionales
			break;
		case DirMessageOps.OPERATION_REGISTER_BAD:
			sb.append(FIELDNAME_ERROR_CODE + DELIMITER + errorCode + END_LINE);
			sb.append(FIELDNAME_ERROR_MESSAGE + DELIMITER + errorMessage + END_LINE);
			break;
		case DirMessageOps.OPERATION_PEERLIST:
			// No hay campos adicionales
			break;
		case DirMessageOps.OPERATION_PEERLIST_OK:
			if (peerList != null && !peerList.isEmpty()) {
				sb.append(FIELDNAME_PEERLIST + DELIMITER + peerList + END_LINE);
			}
			break;
		case DirMessageOps.OPERATION_PEERLIST_BAD:
			sb.append(FIELDNAME_ERROR_CODE + DELIMITER + errorCode + END_LINE);
			sb.append(FIELDNAME_ERROR_MESSAGE + DELIMITER + errorMessage + END_LINE);
			break;
		
		}
		



		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}
	
	//comando ping 
	public byte[] toByteArray() {
	    return this.toString().getBytes();
	}
}
