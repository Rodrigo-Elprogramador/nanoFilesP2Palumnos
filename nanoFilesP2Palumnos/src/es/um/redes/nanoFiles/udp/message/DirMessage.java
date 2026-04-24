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
	public static final int PACKET_MAX_SIZE = 65507; 

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
	private static final String FIELDNAME_ERROR_MESSAGE = "errormessage"; // ← TODO EN MINÚSCULAS 
	//EJE 7 ASCII																		
	private static final String FIELDNAME_FILELIST = "filelist";
	private static final String FIELDNAME_NICKNAME = "nickname";
	private static final String FIELDNAME_PORT = "port";
	private static final String FIELDNAME_PEERLIST = "peerlist";
	
	//EXTRA 
	private static final String FIELDNAME_SEQNUM         = "seqnum";
	private static final String FIELDNAME_NUMCHUNKS       = "numchunks";
	private static final String FIELDNAME_HASH_SUBSTRING  = "hashsubstring";
	private static final String FIELDNAME_FILENAME        = "filename";
	private static final String FIELDNAME_FILESIZE        = "filesize";
	private static final String FIELDNAME_FILEHASH        = "filehash";
	private static final String FIELDNAME_FILEDATA        = "filedata";


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
	
	//EXTRAS
	private int    seqnum    = 0;
	private int    numchunks = 1;
	private String hashSubstring;
	private String fileName2;   //
	private long   fileSize2;
	private String fileHash2;
	private String fileData;    // datos del fichero en Base64



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

		
		String[] lines = message.split(END_LINE + "");
		DirMessage m = null;



		for (String line : lines) {
			// Saltamos líneas vacias 
			if (line.isEmpty()) {
				continue;
			}
			
			int idx = line.indexOf(DELIMITER); // Posición del delimitador
			
			// Verificamos que el delimitador existe en la linea
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
			//EXTRAS
			case FIELDNAME_SEQNUM:
			    assert (m != null);
			    m.seqnum = Integer.parseInt(value);
			    break;

			case FIELDNAME_NUMCHUNKS:
			    assert (m != null);
			    m.numchunks = Integer.parseInt(value);
			    break;

			case FIELDNAME_HASH_SUBSTRING:
			    assert (m != null);
			    m.hashSubstring = value;
			    break;

			case FIELDNAME_FILENAME:
			    assert (m != null);
			    m.fileName2 = value;
			    break;

			case FIELDNAME_FILESIZE:
			    assert (m != null);
			    m.fileSize2 = Long.parseLong(value);
			    break;

			case FIELDNAME_FILEHASH:
			    assert (m != null);
			    m.fileHash2 = value;
			    break;

			case FIELDNAME_FILEDATA:
			    assert (m != null);
			    m.fileData = value;
			    break;




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
			break;
		// EJE 7 ASCII
		case DirMessageOps.OPERATION_FILELIST:
			break;
		case DirMessageOps.OPERATION_FILELIST_OK:
		    sb.append(FIELDNAME_SEQNUM    + DELIMITER + seqnum    + END_LINE);
		    sb.append(FIELDNAME_NUMCHUNKS + DELIMITER + numchunks + END_LINE);
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
			break;
		case DirMessageOps.OPERATION_REGISTER_BAD:
			sb.append(FIELDNAME_ERROR_CODE + DELIMITER + errorCode + END_LINE);
			sb.append(FIELDNAME_ERROR_MESSAGE + DELIMITER + errorMessage + END_LINE);
			break;
		case DirMessageOps.OPERATION_PEERLIST:
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
		//EXTRAS
				case DirMessageOps.OPERATION_FILELIST_NEXT:   
		    sb.append(FIELDNAME_SEQNUM + DELIMITER + seqnum + END_LINE);
		    break;

		case DirMessageOps.OPERATION_DIRDL:           
		    sb.append(FIELDNAME_HASH_SUBSTRING + DELIMITER + hashSubstring + END_LINE);
		    break;

		case DirMessageOps.OPERATION_DIRDL_OK:       
		    sb.append(FIELDNAME_FILENAME + DELIMITER + fileName2 + END_LINE);
		    sb.append(FIELDNAME_FILESIZE + DELIMITER + fileSize2 + END_LINE);
		    sb.append(FIELDNAME_FILEHASH + DELIMITER + fileHash2 + END_LINE);
		    sb.append(FIELDNAME_FILEDATA + DELIMITER + fileData  + END_LINE);
		    break;

		case DirMessageOps.OPERATION_DIRDL_BAD:       
		    sb.append(FIELDNAME_ERROR_CODE    + DELIMITER + errorCode    + END_LINE);
		    sb.append(FIELDNAME_ERROR_MESSAGE + DELIMITER + errorMessage + END_LINE);
		    break;
		
		}
		



		sb.append(END_LINE); // Marcamos el final del mensaje
		return sb.toString();
	}
	
	//EXTRAS
	public void setSeqnum(int s)    { this.seqnum = s; }
	public int  getSeqnum()         { return seqnum; }
	public void setNumchunks(int n) { this.numchunks = n; }
	public int  getNumchunks()      { return numchunks; }

	public void setHashSubstring(String hs) {
	    if (!operation.equals(DirMessageOps.OPERATION_DIRDL))
	        throw new RuntimeException("setHashSubstring: tipo incorrecto: " + operation);
	    hashSubstring = hs;
	}
	public String getHashSubstring() {
	    if (!operation.equals(DirMessageOps.OPERATION_DIRDL))
	        throw new RuntimeException("getHashSubstring: tipo incorrecto: " + operation);
	    return hashSubstring;
	}

	// campos de respuesta dirDlOk 
	public void setDirDlFilename(String name) { this.fileName2 = name; }
	public String getDirDlFilename()          { return fileName2; }
	public void setDirDlFilesize(long size)   { this.fileSize2 = size; }
	public long getDirDlFilesize()            { return fileSize2; }
	public void setDirDlFilehash(String hash) { this.fileHash2 = hash; }
	public String getDirDlFilehash()          { return fileHash2; }
	public void setFileData(String data)      { this.fileData = data; }
	public String getFileData()               { return fileData; }
	
	//comando ping 
	public byte[] toByteArray() {
	    return this.toString().getBytes();
	}
}
