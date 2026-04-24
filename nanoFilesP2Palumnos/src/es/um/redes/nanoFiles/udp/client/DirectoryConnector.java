package es.um.redes.nanoFiles.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Arrays;
import java.util.LinkedHashMap;
import es.um.redes.nanoFiles.tcp.client.NFConnector;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	/**
	 * Puerto en el que atienden los servidores de directorio
	 */
	private static final int DIRECTORY_PORT = 6868;
	/**
	 * Tiempo máximo en milisegundos que se esperará a recibir una respuesta por el
	 * socket antes de que se deba lanzar una excepción SocketTimeoutException para
	 * recuperar el control
	 */
	private static final int TIMEOUT = 1000;
	/**
	 * Número de intentos máximos para obtener del directorio una respuesta a una
	 * solicitud enviada. Cada vez que expira el timeout sin recibir respuesta se
	 * cuenta como un intento.
	 */
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Socket UDP usado para la comunicación con el directorio
	 */
	private DatagramSocket socket;
	/**
	 * Dirección de socket del directorio (IP:puertoUDP)
	 */
	private InetSocketAddress directoryAddress;
	/**
	 * Nombre/IP del host donde se ejecuta el directorio
	 */
	private String directoryHostname;

	public static class DownloadedFile {
		public final String filename;
		public final long filesize;
		public final byte[] data;
		public final String filehash;

		public DownloadedFile(String filename, long fsize, byte[] data, String filehash) {
			this.filename = filename;
			this.filesize = fsize;
			this.data = data;
			this.filehash = filehash;
		}
	}

	public DirectoryConnector(String hostname) throws IOException {
		// Guardamos el string con el nombre/IP del host
		directoryHostname = hostname;
		/*
		 * TODO: (Boletín SocketsUDP) Convertir el string 'hostname' a InetAddress y
		 * guardar la dirección de socket (address:DIRECTORY_PORT) del directorio en el
		 * atributo directoryAddress, para poder enviar datagramas a dicho destino.
		 */

		InetAddress serverIp = InetAddress.getByName(hostname);
		directoryAddress = new InetSocketAddress(serverIp, DIRECTORY_PORT);

		/*
		 * TODO: (Boletín SocketsUDP) Crea el socket UDP en cualquier puerto para enviar
		 * datagramas al directorio
		 */
		socket = new DatagramSocket();

	}

	/**
	 * Método para enviar y recibir datagramas al/del directorio
	 * 
	 * @param requestData los datos a enviar al directorio (mensaje de solicitud)
	 * @return los datos recibidos del directorio (mensaje de respuesta)
	 */
//nuevo metodo para comando ping el viejo lo dejo abajo comentado
	private byte[] sendAndReceiveDatagrams(byte[] requestData) {
		byte responseData[] = new byte[DirMessage.PACKET_MAX_SIZE];
		byte response[] = null;

		// 1. Configuramos el timeout del socket (solo hay que hacerlo una vez)
		try {
			socket.setSoTimeout(TIMEOUT);
		} catch (IOException e) {
			System.err.println("Error configurando el timeout del socket.");
			System.exit(-1);
		}

		DatagramPacket packetToServer = new DatagramPacket(requestData, requestData.length, directoryAddress);
		DatagramPacket packetFromServer = new DatagramPacket(responseData, responseData.length);

		int attempts = 0;
		boolean received = false;

		
		while (attempts < MAX_NUMBER_OF_ATTEMPTS && !received) {
			try {
				attempts++;
				// Enviamos el mensaje
				socket.send(packetToServer);

				// Intentamos recibir la respuesta
				socket.receive(packetFromServer);

				// Si llegamos aquí, es que no ha saltado el SocketTimeoutException
				received = true;
			} catch (SocketTimeoutException e) {
				System.err.println("Intento " + attempts + " fallido: el servidor no responde (Timeout).");
				if (attempts >= MAX_NUMBER_OF_ATTEMPTS) {
					System.err.println("Se ha alcanzado el máximo número de reintentos. Abortando.");
				}
			} catch (IOException e) {
				System.err.println("Error de E/S irrecuperable en el socket.");
				System.exit(-1);
			}
		}

		// Si recibimos respuesta, extraemos solo los bytes útiles
		if (received) {
			response = Arrays.copyOf(packetFromServer.getData(), packetFromServer.getLength());
		}

		return response;
	}

	
	/*
	 * TODO: (Boletín SocketsUDP) Una vez el envío y recepción asumiendo un canal
	 * confiable (sin pérdidas) esté terminado y probado, debe implementarse un
	 * mecanismo de retransmisión usando temporizador, en caso de que no se reciba
	 * respuesta en el plazo de TIMEOUT. En caso de salte el timeout, se debe volver
	 * a enviar el datagrama y tratar de recibir respuestas, reintentando como
	 * máximo en MAX_NUMBER_OF_ATTEMPTS ocasiones.
	 */
	/*
	 * TODO: (Boletín SocketsUDP) Las excepciones que puedan lanzarse al
	 * leer/escribir en el socket deben ser capturadas y tratadas en este método. Si
	 * se produce una excepción de entrada/salida (error del que no es posible
	 * recuperarse), se debe informar y terminar el programa.
	 */
	/*
	 * NOTA: Las excepciones deben tratarse de la más concreta a la más genérica.
	 * SocketTimeoutException es más concreta que IOException.
	 */
	

	/**
	 * Método para probar la comunicación con el directorio mediante el envío y
	 * recepción de mensajes sin formatear ("en crudo")
	 * 
	 * @return verdadero si se ha enviado un datagrama y recibido una respuesta
	 */
	public boolean testSendAndReceive() {
		/*
		 * TODO: (Boletín SocketsUDP) Probar el correcto funcionamiento de
		 * sendAndReceiveDatagrams. Se debe enviar un datagrama con la cadena "ping" y
		 * comprobar que la respuesta recibida empieza por "pingok". En tal caso,
		 * devuelve verdadero, falso si la respuesta no contiene los datos esperados.
		 */
		boolean success = false;

		byte[] requestData = new String("ping").getBytes();
		byte[] response = sendAndReceiveDatagrams(requestData);
		if (response != null) {
			String receivedMessage = new String(response, 0, response.length);
			System.out.println("Receiving.." + receivedMessage);
			if (receivedMessage.equals("welcome")) {
				success = true;
			}
		}

		return success;
	}

	public String getDirectoryHostname() {
		return directoryHostname;
	}

	/**
	 * Método para "hacer ping" al directorio, comprobar que está operativo y que
	 * usa un protocolo compatible. Este método no usa mensajes bien formados.
	 * 
	 * @return Verdadero si
	 */
	public boolean pingDirectoryRaw() {
		boolean success = false;
		/*
		 * TODO: (Boletín EstructuraNanoFiles) Basándose en el código de
		 * "testSendAndReceive", contactar con el directorio, enviándole nuestro
		 * PROTOCOL_ID (ver clase NanoFiles). Se deben usar mensajes "en crudo" (sin un
		 * formato bien definido) para la comunicación.
		 * 		
		 */
		

		byte[] requestData = new String("ping&" + NanoFiles.PROTOCOL_ID).getBytes();
		byte[] response = sendAndReceiveDatagrams(requestData);

		if (response != null) {
			String receivedMessage = new String(response, 0, response.length);
			System.out.println("Receiving..." + receivedMessage);
			if (receivedMessage.equals("welcome")) {
				success = true;
			}
		}
		return success;
	}

	/**
	 * Método para "hacer ping" al directorio, comprobar que está operativo y que es
	 * compatible.
	 * 
	 * @return Verdadero si el directorio está operativo y es compatible
	 */
	public boolean pingDirectory() {
		boolean success = false;
		/*
		 * TODO: (Boletín MensajesASCII) Hacer ping al directorio 1.Crear el mensaje a
		 * enviar (objeto DirMessage) con atributos adecuados (operation, etc.) NOTA:
		 * Usar como operaciones las constantes definidas en la clase DirMessageOps :
		 * 2.Convertir el objeto DirMessage a enviar a un string (método toString)
		 * 3.Crear un datagrama con los bytes en que se codifica la cadena : 4.Enviar
		 * datagrama y recibir una respuesta (sendAndReceiveDatagrams). : 5.Convertir
		 * respuesta recibida en un objeto DirMessage (método DirMessage.fromString)
		 * 6.Extraer datos del objeto DirMessage y procesarlos 7.Devolver éxito/fracaso
		 * de la operación
		 */
		// 1. Creamos el mensaje DirMessage de tipo PING
		DirMessage pingMessage = new DirMessage(DirMessageOps.OPERATION_PING);

		// 2. Establecem0s el protocolId usando el setter
		pingMessage.setProtocolID(NanoFiles.PROTOCOL_ID);

		// 3. Convertimos el objeto DirMessage a String
		String messageString = pingMessage.toString();

		// 4. Convertimos el String a bytes para enviar en el datagrama
		byte[] requestData = messageString.getBytes();

		// 5. Enviamos datagrama y recibir respuesta
		byte[] responseData = sendAndReceiveDatagrams(requestData);

		// 6 Procesamos la respuesta recibida
		if (responseData != null) {
			// Convertimos los bytes recibidos a String
			String responseString = new String(responseData);

			// Convertimos el String a objeto DirMessage usando fromString
			DirMessage responseMessage = DirMessage.fromString(responseString);

			// 7. Extraemos la operación del mensaje de respuesta y verificar
			String operation = responseMessage.getOperation();

			if (operation.equals(DirMessageOps.OPERATION_PING_OK)) {
				System.out.println("✓ Ping successful: Directory is compatible");
				success = true;
			} else if (operation.equals(DirMessageOps.OPERATION_PING_BAD)) {
				System.err.println("✗ Ping failed: " + responseMessage.getErrorMessage());
			} else {
				System.err.println("✗ Ping failed: Unexpected response operation: " + operation);
			}
		} else {
			System.err.println("✗ Ping failed: No response received from directory");
		}

		return success;
	}

	/**
	 * Método para dar de alta como servidor de ficheros en el puerto indicado.
	 * 
	 * @param serverPort El puerto TCP en el que este peer sirve ficheros a otros
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y acepta la lista de ficheros, falso en caso contrario.
	 */

	// EJE 7 ASCII
	public boolean registerFileServer(int serverPort) {
		boolean success = false;


		// 1. Creamos el mensaje de solicitud
		DirMessage requestMessage = new DirMessage(DirMessageOps.OPERATION_REGISTER);

		// 2. Establecemos nickname y puerto
		requestMessage.setNickname(NanoFiles.peerNickname);
		requestMessage.setPort(serverPort);

		// 3. Convertimos a String
		String messageString = requestMessage.toString();

		// 4. Convertimos a bytes
		byte[] requestData = messageString.getBytes();

		// 5. Envimosr y recibimos respuesta
		byte[] responseData = sendAndReceiveDatagrams(requestData);

		// 6. Procesar respuesta
		if (responseData != null) {
			String responseString = new String(responseData);
			DirMessage responseMessage = DirMessage.fromString(responseString);

			String operation = responseMessage.getOperation();

			if (operation.equals(DirMessageOps.OPERATION_REGISTER_OK)) {
				System.out.println("✓ Successfully registered as file server at port " + serverPort);
				success = true;
			} else if (operation.equals(DirMessageOps.OPERATION_REGISTER_BAD)) {
				String errorCode = responseMessage.getErrorCode();
				String errorMessage = responseMessage.getErrorMessage();

				if (errorCode.equals("2")) {
					// Nickname ya en uso  necesitamos generar uno nuevo
					System.err.println("✗ Registration failed: " + errorMessage);
					System.out.println("→ Nickname '" + NanoFiles.peerNickname + "' is already in use");
					
				} else {
					System.err.println("✗ Registration failed: " + errorMessage);
				}
			} else {
				System.err.println("✗ Unexpected response operation: " + operation);
			}
		} else {
			System.err.println("✗ No response received from directory");
		}

		return success;
	}

	/**
	 * Método para obtener la lista de ficheros alojados en el directorio. Para cada
	 * fichero se debe obtener un objeto FileInfo con nombre, tamaño y hash.
	 * 
	 * @return Los ficheros disponibles en el directorio, o null si el directorio no
	 *         pudo satisfacer nuestra solicitud
	 */

	// EXTRA IMPLEMENTADO DESDE EJE 7 MENSAJE ASCII ** MODIFICADO PARA EL COMANDO DIRFILES y MODIFICADO DE NUEVO PARA LA AMPLIACION
	public FileInfo[] getFileList() {
	    FileInfo[] filelist = new FileInfo[0];

	    DirMessage request = new DirMessage(DirMessageOps.OPERATION_FILELIST);
	    byte[] responseData = sendAndReceiveDatagrams(request.toString().getBytes());

	    if (responseData == null) {
	        System.err.println("✗ No response received from directory (chunk 0)");
	        return filelist;
	    }

	    DirMessage response = DirMessage.fromString(new String(responseData));
	    if (!DirMessageOps.OPERATION_FILELIST_OK.equals(response.getOperation())) {
	        System.err.println("✗ Error getting file list: " + response.getErrorMessage());
	        return filelist;
	    }

	    int numchunks = response.getNumchunks();
	    StringBuilder fullList = new StringBuilder();
	    if (response.getFileList() != null) fullList.append(response.getFileList());

	    for (int seq = 1; seq < numchunks; seq++) {
	        DirMessage nextReq = new DirMessage(DirMessageOps.OPERATION_FILELIST_NEXT);
	        nextReq.setSeqnum(seq);
	        byte[] nextData = sendAndReceiveDatagrams(nextReq.toString().getBytes());
	        if (nextData == null) {
	            System.err.println("✗ No response for chunk " + seq);
	            return filelist;
	        }
	        DirMessage nextResp = DirMessage.fromString(new String(nextData));
	        if (!DirMessageOps.OPERATION_FILELIST_OK.equals(nextResp.getOperation())) {
	            System.err.println("✗ Error in chunk " + seq + ": " + nextResp.getErrorMessage());
	            return filelist;
	        }
	        // Añadimos coma separadora si el acumulado no está vacío
	        if (fullList.length() > 0 && nextResp.getFileList() != null && !nextResp.getFileList().isEmpty()) {
	            fullList.append(",");
	        }
	        if (nextResp.getFileList() != null) fullList.append(nextResp.getFileList());
	    }

	    String all = fullList.toString();
	    if (all.isBlank()) return filelist;

	    String[] entries = all.split(",");
	    filelist = new FileInfo[entries.length];
	    for (int i = 0; i < entries.length; i++) {
	        String[] parts = entries[i].split("&");
	        if (parts.length != 3) {
	            System.err.println("✗ Invalid entry: " + entries[i]);
	            return new FileInfo[0];
	        }
	        filelist[i] = new FileInfo(parts[0], parts[1], Long.parseLong(parts[2]), null);
	    }
	    System.out.println("✓ Received " + filelist.length + " files in " + numchunks + " chunk(s)");
	    return filelist;
	}

	// EJE 7 ASCII
	public Map<String, InetSocketAddress> getPeerList() {
		Map<String, InetSocketAddress> peers = new LinkedHashMap<String, InetSocketAddress>();

		// 1. Creamos el mensaje de solicitud
		DirMessage requestMessage = new DirMessage(DirMessageOps.OPERATION_PEERLIST);

		// 2. Convertimos a String
		String messageString = requestMessage.toString();

		// 3 Convertimos a bytes
		byte[] requestData = messageString.getBytes();

		// 4. Enviamos y recibimos respuesta
		byte[] responseData = sendAndReceiveDatagrams(requestData);

		// 5. Procesamos respuesta
		if (responseData != null) {
			String responseString = new String(responseData);
			DirMessage responseMessage = DirMessage.fromString(responseString);

			String operation = responseMessage.getOperation();

			if (operation.equals(DirMessageOps.OPERATION_PEERLIST_OK)) {
				// Parseamos la lista de peers
				String peerListString = responseMessage.getPeerList();

				if (peerListString != null && !peerListString.isEmpty()) {
					String[] peerEntries = peerListString.split(",");

					for (String peerEntry : peerEntries) {
						String[] parts = peerEntry.split("&");
						if (parts.length == 2) {
							String nickname = parts[0];
							String addressString = parts[1]; // IP:puerto

							String[] addressParts = addressString.split(":");
							if (addressParts.length == 2) {
								String ip = addressParts[0];
								int port = Integer.parseInt(addressParts[1]);

								InetSocketAddress address = new InetSocketAddress(ip, port);
								peers.put(nickname, address);
							}
						}
					}

					System.out.println("✓ Received list of " + peers.size() + " registered peers");
				} else {
					System.out.println("✓ No peers registered in directory");
				}
			} else if (operation.equals(DirMessageOps.OPERATION_PEERLIST_BAD)) {
				System.err.println("✗ Error getting peer list: " + responseMessage.getErrorMessage());
			} else {
				System.err.println("✗ Unexpected response operation: " + operation);
			}
		} else {
			System.err.println("✗ No response received from directory");
		}

		return peers;
	}

	public Map<String, InetSocketAddress[]> searchFilesByHash(String hashSubstring) {
		Map<String, InetSocketAddress[]> results = new LinkedHashMap<String, InetSocketAddress[]>();

		return results;
	}

	//EXTRA AMPLIACION DEL COMANDO DIRFILES
	public DownloadedFile downloadFileFromDirectory(String hashSubstring) {
	    // 1) Construimos y enviamos petición
	    DirMessage request = new DirMessage(DirMessageOps.OPERATION_DIRDL);
	    request.setHashSubstring(hashSubstring);
	    byte[] responseData = sendAndReceiveDatagrams(request.toString().getBytes());

	    if (responseData == null) {
	        System.err.println("✗ No response from directory for dirdl");
	        return null;
	    }

	    DirMessage response = DirMessage.fromString(new String(responseData));
	    String op = response.getOperation();

	    if (DirMessageOps.OPERATION_DIRDL_OK.equals(op)) {
	        byte[] fileBytes = java.util.Base64.getDecoder().decode(response.getFileData());
	        System.out.println("✓ Received file: " + response.getDirDlFilename()
	                + " (" + fileBytes.length + " bytes)");
	        return new DownloadedFile(
	            response.getDirDlFilename(),
	            response.getDirDlFilesize(),
	            fileBytes,
	            response.getDirDlFilehash()
	        );
	    } else if (DirMessageOps.OPERATION_DIRDL_BAD.equals(op)) {
	        System.err.println("✗ Directory error [" + response.getErrorCode() + "]: "
	                + response.getErrorMessage());
	        return null;
	    } else {
	        System.err.println("✗ Unexpected response: " + op);
	        return null;
	    }
	}

	/**
	 * Método para darse de baja como servidor de ficheros.
	 * 
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y ha dado de baja sus ficheros.
	 */
	public boolean unregisterFileServer() {
		boolean success = false;

		return success;
	}

}
