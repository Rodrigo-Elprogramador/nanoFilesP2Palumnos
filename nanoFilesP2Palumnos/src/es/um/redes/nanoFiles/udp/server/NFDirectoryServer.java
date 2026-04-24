package es.um.redes.nanoFiles.udp.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.LinkedHashMap;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;
import es.um.redes.nanoFiles.util.NickGenerator;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros alojados, servidores
	 * registrados, etc.
	 */
	/**
	 * Lista de ficheros alojados en el directorio.
	 */
	private FileInfo[] directoryFiles;
	/**
	 * Lista de servidores registrados (IP, puerto TCP).
	 */
	private LinkedHashMap<String, InetSocketAddress> registeredPeers;

	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability, String directoryFilesPath) throws SocketException {
		/*
		 * Guardamos la probabilidad de pérdida de datagramas 
		 * 
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * Cargarmoslos ficheros del directorio compartido
		 */
		File dir = new File(directoryFilesPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		directoryFiles = FileInfo.loadFilesFromFolder(directoryFilesPath);
		System.out.println("* Directory loaded " + directoryFiles.length + " files from " + directoryFilesPath);
		/*
		 * TODO: (Boletín SocketsUDP) Inicializar el atributo socket: Crear un socket
		 * UDP ligado al puerto especificado por el argumento directoryPort en la
		 * máquina local,
		 */
		
		socket = new DatagramSocket(DIRECTORY_PORT); //EJER 4
		System.out.println("* Directory server socket bound to port " + DIRECTORY_PORT); //BOLETIN UDP EJER4
		
		/*
		 * TODO: (Boletín SocketsUDP) Inicializar atributos que mantienen el estado del
		 * servidor de directorio: peers registrados, etc.)
		 */
		
		registeredPeers = new LinkedHashMap<String, InetSocketAddress>();//BOLETIN UDP EJER4


		if (NanoFiles.testModeUDP) {
			if (socket == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public DatagramPacket receiveDatagram() throws IOException {
		DatagramPacket datagramReceivedFromClient = null;
		boolean datagramReceived = false;
		while (!datagramReceived) {
			/*
			 * TODO: (Boletín SocketsUDP) Crear un búfer para recibir datagramas y un
			 * datagrama asociado al búfer (datagramReceivedFromClient)
			 */
			byte[] recvBuffer = new byte[DirMessage.PACKET_MAX_SIZE];
			datagramReceivedFromClient = new DatagramPacket(recvBuffer, recvBuffer.length); // BOLETIN UDP EJER4
			/*
			 * TODO: (Boletín SocketsUDP) Recibimos a través del socket un datagrama
			 */
			
			socket.receive(datagramReceivedFromClient); //BOLETIN UDP EJER4



			if (datagramReceivedFromClient == null) {
				System.err.println("[testMode] NFDirectoryServer.receiveDatagram: code not yet fully functional.\n"
						+ "Check that all TODOs have been correctly addressed!");
				System.exit(-1);
			} else {
				// Vemos si el mensaje debe ser ignorado
				double rand = Math.random();
				if (rand < messageDiscardProbability) {
					System.err.println(
							"Directory ignored datagram from " + datagramReceivedFromClient.getSocketAddress());
				} else {
					datagramReceived = true;
				}
			}

		}

		return datagramReceivedFromClient;
	}

	public void runTest() throws IOException {

		System.out.println("[testMode] Directory starting...");

		System.out.println("[testMode] Attempting to receive 'ping' message...");
		DatagramPacket rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);

		System.out.println("[testMode] Attempting to receive 'ping&PROTOCOL_ID' message...");
		rcvDatagram = receiveDatagram();
		sendResponseTestMode(rcvDatagram);
	}

	private void sendResponseTestMode(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín SocketsUDP) Construir un String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración.
		 */
		
		String receivedMessage = new String(pkt.getData(), 0, pkt.getLength()); // BOLETIN UDP EJER4
		System.out.println("[testMode] Data received from client: \"" + receivedMessage + "\"");// BOLETIN UDP EJER4

		/*
		 * TODO: (Boletín SocketsUDP) Después, usar la cadena para comprobar que su
		 * valor es "ping"; en ese caso, enviar como respuesta un datagrama con la
		 * cadena "pingok". Si el mensaje recibido no es "ping", se informa del error y
		 * se envía "invalid" como respuesta.
		 */
		
		
		/*
		 * TODO: (Boletín Estructura-NanoFiles) Ampliar el código para que, en el caso
		 * de que la cadena recibida no sea exactamente "ping", comprobar si comienza
		 * por "ping&" (es del tipo "ping&PROTOCOL_ID", donde PROTOCOL_ID será el
		 * identificador del protocolo diseñado por el grupo de prácticas (ver
		 * NanoFiles.PROTOCOL_ID). Se debe extraer el "protocol_id" de la cadena
		 * recibida y comprobar que su valor coincide con el de NanoFiles.PROTOCOL_ID,
		 * en cuyo caso se responderá con "welcome" (en otro caso, "denied").
		 */
		
		//BOLETIN 3 EJERCICIO 3 SE HA CAMBIADO COSAS QUE ESTABAN DEL BOLETIN 2 EJER 4
		
		
		InetSocketAddress clientAddr = (InetSocketAddress) pkt.getSocketAddress();
		String messageToClient;

		if (receivedMessage.equals("ping")) {
		    messageToClient = new String("pingok");
		} else {
		    if (receivedMessage.startsWith("ping&")) {
		        String protocolId = receivedMessage.substring(5);
		        if (protocolId.equals(NanoFiles.PROTOCOL_ID)) {
		            messageToClient = new String("welcome");
		        } else {
		            messageToClient = new String("denied");
		        }
		    } else {
		        messageToClient = new String("invalid");
		    }
		}

		byte dataToClient[] = messageToClient.getBytes();
		DatagramPacket packetToClient = new DatagramPacket(dataToClient, dataToClient.length, clientAddr);
		socket.send(packetToClient);
	}

		



	

	public void run() throws IOException {

		System.out.println("Directory starting...");

		while (true) { // Bucle principal del servidor de directorio
			DatagramPacket rcvDatagram = receiveDatagram();

			sendResponse(rcvDatagram);

		}
	}

	private void sendResponse(DatagramPacket pkt) throws IOException {
		/*
		 * TODO: (Boletín MensajesASCII) Construir String partir de los datos recibidos
		 * en el datagrama pkt. A continuación, imprimir por pantalla dicha cadena a
		 * modo de depuración. Después, usar la cadena para construir un objeto
		 * DirMessage que contenga en sus atributos los valores del mensaje. A partir de
		 * este objeto, se podrá obtener los valores de los campos del mensaje mediante
		 * métodos "getter" para procesar el mensaje y consultar/modificar el estado del
		 * servidor.
		 */
		// 1. Construimos String a partir de los datos recibidos en el datagrama
		String receivedMessage = new String(pkt.getData(), 0, pkt.getLength());
		System.out.println("→ Received message from " + pkt.getSocketAddress() + ":");
		System.out.println(receivedMessage);
		
		// 2. Construimos un objeto DirMessage a partir de la cadena recibida
		DirMessage messageFromClient = DirMessage.fromString(receivedMessage);
		
		// 3. Obtenemos el tipo de operación del mensaje recibido
		String operation = messageFromClient.getOperation();
		
		// 4. Variable para almacenar el mensaje de respuesta
		DirMessage msgToSend = null;
		
		/*
		 * TODO: Una vez construido un objeto DirMessage con el contenido del datagrama
		 * recibido, obtener el tipo de operación solicitada por el mensaje y actuar en
		 * consecuencia, enviando uno u otro tipo de mensaje en respuesta.
		 */
		

		/*
		 * TODO: (Boletín MensajesASCII) Construir un objeto DirMessage (msgToSend) con
		 * la respuesta a enviar al cliente, en función del tipo de mensaje recibido,
		 * leyendo/modificando según sea necesario el "estado" guardado en el servidor
		 * de directorio (atributos files, etc.). Los atributos del objeto DirMessage
		 * contendrán los valores adecuados para los diferentes campos del mensaje a
		 * enviar como respuesta (operation, etc.)
		 */

		switch (operation) {
		case DirMessageOps.OPERATION_PING: {
			/*
			 * TODO: (Boletín MensajesASCII) Comprobamos si el protocolId del mensaje del
			 * cliente coincide con el nuestro.
			 */
			/*
			 * TODO: (Boletín MensajesASCII) Construimos un mensaje de respuesta que indique
			 * el éxito/fracaso del ping (compatible, incompatible), y lo devolvemos como
			 * resultado del método.
			 */
			/*
			 * TODO: (Boletín MensajesASCII) Imprimimos por pantalla el resultado de
			 * procesar la petición recibida (éxito o fracaso) con los datos relevantes, a
			 * modo de depuración en el servidor
			 */
			// Obtenemos el protocolId del mensaje recibido
			String receivedProtocolId = messageFromClient.getProtocolId();
			System.out.println("→ Processing PING with protocol ID: " + receivedProtocolId);
			
			// Comprobamos si el protocolId coincide con el nuestro
			if (receivedProtocolId != null && receivedProtocolId.equals(NanoFiles.PROTOCOL_ID)) {
				// Protocolo compatible con responder con PING_OK
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PING_OK);
				System.out.println("✓ Protocol compatible, responding with PING_OK");
			} else {
				// Protocolo incompatible con responder con PING_BAD
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PING_BAD);
				msgToSend.setErrorCode("1");
				msgToSend.setErrorMessage("Incompatible protocol. Expected " + NanoFiles.PROTOCOL_ID 
				                          + " but received " + receivedProtocolId);
				System.err.println("✗ Protocol incompatible, responding with PING_BAD");
			}



			break;
		}
		
		//EJE 7 ASCII **MODIFICADO el case PARA DIRFILES** y OTRA VEZ MODIFICADO PARA EXTRA DIRFILES
		
		case DirMessageOps.OPERATION_FILELIST: {
		    System.out.println("→ Processing FILELIST request (chunk 0)");
		    String[] chunks = buildFileListChunks();
		    msgToSend = new DirMessage(DirMessageOps.OPERATION_FILELIST_OK);
		    msgToSend.setSeqnum(0);
		    msgToSend.setNumchunks(chunks.length);
		    msgToSend.setFileList(chunks[0]);
		    System.out.println("✓ Sending chunk 0/" + chunks.length + " (" + chunks[0].length() + " bytes)");
		    break;
		}
		
		//EJE 7 ASCII
		
		case DirMessageOps.OPERATION_REGISTER: {
			// Obtenemos nickname y puerto del mensaje
			String clientNickname = messageFromClient.getNickname();
			int clientPort = messageFromClient.getPort();
			
			System.out.println("→ Processing REGISTER request: nickname=" + clientNickname + ", port=" + clientPort);
			
			// Obtenemos la IP del cliente desde el datagrama
			InetSocketAddress clientAddress = (InetSocketAddress) pkt.getSocketAddress();
			InetAddress clientIP = clientAddress.getAddress();
			
			// Comprobamos si el nickname ya está registrado
			if (registeredPeers.containsKey(clientNickname)) {
				// Nickname ya existe
				msgToSend = new DirMessage(DirMessageOps.OPERATION_REGISTER_BAD);
				msgToSend.setErrorCode("2");
				msgToSend.setErrorMessage("Nickname '" + clientNickname + "' already in use");
				System.err.println("✗ Registration failed: Nickname already in use");
			} else {
				// Verificamos que el puerto es válido
				if (clientPort <= 0 || clientPort > 65535) {
					msgToSend = new DirMessage(DirMessageOps.OPERATION_REGISTER_BAD);
					msgToSend.setErrorCode("4");
					msgToSend.setErrorMessage("Invalid port number: " + clientPort);
					System.err.println("✗ Registration failed: Invalid port");
				} else {
					// Registramos el peer
					InetSocketAddress peerAddress = new InetSocketAddress(clientIP, clientPort);
					registeredPeers.put(clientNickname, peerAddress);
					
					msgToSend = new DirMessage(DirMessageOps.OPERATION_REGISTER_OK);
					System.out.println("✓ Peer registered successfully: " + clientNickname + " at " + peerAddress);
					System.out.println("  Total registered peers: " + registeredPeers.size());
				}
			}
			
			break;
		}
		
		//EJE 7 ASCII
		
		case DirMessageOps.OPERATION_PEERLIST: {
			System.out.println("→ Processing PEERLIST request");
			
			// Construimos la respuesta con la lista de peers
			if (registeredPeers != null && !registeredPeers.isEmpty()) {
				// Creamos mensaje de respuesta exitosa
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PEERLIST_OK);
				
				// Construimos la cadena con la lista de peers
				StringBuilder peerListBuilder = new StringBuilder();
				int count = 0;
				for (String nick : registeredPeers.keySet()) {
					InetSocketAddress address = registeredPeers.get(nick);
					
					peerListBuilder.append(nick);
					peerListBuilder.append("&");
					peerListBuilder.append(address.getAddress().getHostAddress());
					peerListBuilder.append(":");
					peerListBuilder.append(address.getPort());
					
					// Añadimos coma si no es el último peer
					if (count < registeredPeers.size() - 1) {
						peerListBuilder.append(",");
					}
					count++;
				}
				
				msgToSend.setPeerList(peerListBuilder.toString());
				System.out.println("✓ Sending list of " + registeredPeers.size() + " registered peers");
			} else {
				// No hay peers registrados
				msgToSend = new DirMessage(DirMessageOps.OPERATION_PEERLIST_BAD);
				msgToSend.setErrorCode("5");
				msgToSend.setErrorMessage("No peers registered in directory");
				System.out.println("✗ No peers registered");
			}
			
			break;
		}
		
		//EXTRA DIRFILES
		case DirMessageOps.OPERATION_FILELIST_NEXT: {
		    int requestedSeq = messageFromClient.getSeqnum();
		    System.out.println("→ Processing FILELIST_NEXT request, chunk " + requestedSeq);
		    String[] chunks = buildFileListChunks();
		    if (requestedSeq < 0 || requestedSeq >= chunks.length) {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_FILELIST_BAD);
		        msgToSend.setErrorCode("6");
		        msgToSend.setErrorMessage("Invalid chunk seqnum: " + requestedSeq);
		    } else {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_FILELIST_OK);
		        msgToSend.setSeqnum(requestedSeq);
		        msgToSend.setNumchunks(chunks.length);
		        msgToSend.setFileList(chunks[requestedSeq]);
		        System.out.println("✓ Sending chunk " + requestedSeq + "/" + chunks.length);
		    }
		    break;
		}
		//EXTRA DIRDL
		case DirMessageOps.OPERATION_DIRDL: {
		    String sub = messageFromClient.getHashSubstring();
		    System.out.println("→ Processing DIRDL request, hash substring: " + sub);

		    // Buscam0s coincidencias
		    java.util.List<FileInfo> matches = new java.util.ArrayList<>();
		    for (FileInfo f : directoryFiles) {
		        if (f.fileHash.contains(sub)) matches.add(f);
		    }

		    if (matches.size() == 0) {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_DIRDL_BAD);
		        msgToSend.setErrorCode("10");
		        msgToSend.setErrorMessage("No file matches hash substring: " + sub);
		        System.err.println("✗ No match for: " + sub);
		    } else if (matches.size() > 1) {
		        msgToSend = new DirMessage(DirMessageOps.OPERATION_DIRDL_BAD);
		        msgToSend.setErrorCode("11");
		        msgToSend.setErrorMessage("Ambiguous hash substring '" + sub + "' matches " + matches.size() + " files");
		        System.err.println("✗ Ambiguous substring: " + sub);
		    } else {
		        FileInfo target = matches.get(0);
		        // Comprobamos si cabe en un datagrama 
		        long encodedSize = (long) Math.ceil(target.fileSize * 4.0 / 3.0);
		        int overhead = 300; 
		        if (encodedSize + overhead > DirMessage.PACKET_MAX_SIZE) {
		            msgToSend = new DirMessage(DirMessageOps.OPERATION_DIRDL_BAD);
		            msgToSend.setErrorCode("12");
		            msgToSend.setErrorMessage("File too large for single datagram: " + target.fileSize + " bytes");
		            System.err.println("✗ File too large: " + target.fileName);
		        } else {
		            // Leemos el fichero y codificarlo en Base64
		            try {
		                byte[] fileBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(target.filePath));
		                String base64Data = java.util.Base64.getEncoder().encodeToString(fileBytes);
		                msgToSend = new DirMessage(DirMessageOps.OPERATION_DIRDL_OK);
		                msgToSend.setDirDlFilename(target.fileName);
		                msgToSend.setDirDlFilesize(target.fileSize);
		                msgToSend.setDirDlFilehash(target.fileHash);
		                msgToSend.setFileData(base64Data);
		                System.out.println("✓ Sending file: " + target.fileName + " (" + fileBytes.length + " bytes)");
		            } catch (java.io.IOException e) {
		                msgToSend = new DirMessage(DirMessageOps.OPERATION_DIRDL_BAD);
		                msgToSend.setErrorCode("13");
		                msgToSend.setErrorMessage("Error reading file: " + e.getMessage());
		                System.err.println("✗ Error reading file: " + target.filePath);
		            }
		        }
		    }
		    break;
		}
		
		



		default:
			System.err.println("Unexpected message operation: \"" + operation + "\"");
			System.exit(-1);
		}

		/*
		 * TODO: (Boletín MensajesASCII) Convertir a String el objeto DirMessage
		 * (msgToSend) con el mensaje de respuesta a enviar, extraer los bytes en que se
		 * codifica el string y finalmente enviarlos en un datagrama
		 */
		
		// 5. Convertimos el objeto DirMessage de respuesta a String
		String responseString = msgToSend.toString();
		System.out.println("→ Sending response:");
		System.out.println(responseString);
		
		// 6. Convertimos el String a bytes
		byte[] responseData = responseString.getBytes();
		
		// 7. Crearmos el datagrama de respuesta y enviarlo al cliente
		InetSocketAddress clientAddr = (InetSocketAddress) pkt.getSocketAddress();
		DatagramPacket responsePacket = new DatagramPacket(
			responseData, 
			responseData.length, 
			clientAddr
		);
		
		socket.send(responsePacket);
		System.out.println("✓ Response sent to " + clientAddr + "\n");
	



	}
	
	//EXTRA el método auxiliar para dividir la lista en chunks
	/**
	 * Divide la lista de ficheros en trozos que quepan en un datagrama.
	 * Devuelve un array de Strings, cada uno siendo un trozo de la lista.
	 */
	private String[] buildFileListChunks() {
	    if (directoryFiles == null || directoryFiles.length == 0) {
	        return new String[]{ "" };
	    }
	    // Construimos la lista completa
	    StringBuilder full = new StringBuilder();
	    for (int i = 0; i < directoryFiles.length; i++) {
	        FileInfo f = directoryFiles[i];
	        full.append(f.fileHash).append("&").append(f.fileName).append("&").append(f.fileSize);
	        if (i < directoryFiles.length - 1) full.append(",");
	    }
	    String fullList = full.toString();

	    // Overhead estimado del mensaje (cabeceras DirMessage + seqnum + numchunks + filelist)
	    int overhead = 200;
	    int maxChunkBytes = DirMessage.PACKET_MAX_SIZE - overhead;

	    if (fullList.length() <= maxChunkBytes) {
	        return new String[]{ fullList };
	    }

	    // Dividimos en trozos
	    java.util.List<String> chunks = new java.util.ArrayList<>();
	    int start = 0;
	    while (start < fullList.length()) {
	        int end = Math.min(start + maxChunkBytes, fullList.length());
	        if (end < fullList.length()) {
	            int lastComma = fullList.lastIndexOf(',', end);
	            if (lastComma > start) end = lastComma; // no incluir la coma
	        }
	        chunks.add(fullList.substring(start, end));
	        // Saltamos la coma separadora
	        start = (end < fullList.length() && fullList.charAt(end) == ',') ? end + 1 : end;
	    }
	    return chunks.toArray(new String[0]);
	}




}
