package es.um.redes.nanoFiles.tcp.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;




public class NFServer implements Runnable {

	public static final int PORT = 10000;

	private boolean stopServer = false; // Para poder detener el servidor

	private ServerSocket serverSocket = null;
	
	

	public NFServer() throws IOException {
		/*
		 * TODO: (Boletín SocketsTCP) Crear una direción de socket a partir del puerto
		 * especificado (PORT)
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Crear un socket servidor y ligarlo a la dirección
		 * de socket anterior
		 */
		//comando ping 
		this.serverSocket = new ServerSocket(PORT);
	    System.out.println("* TCP File Server initialized on port " + PORT);


	}
	public int getPort() {
	    if (serverSocket != null && serverSocket.isBound()) {
	        return serverSocket.getLocalPort();
	    }
	    return -1;
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación.
	 * 
	 */
	public void test() {
		if (serverSocket == null || !serverSocket.isBound()) {
			System.err.println(
					"[fileServerTestMode] Failed to run file server, server socket is null or not bound to any port");
			return;
		} else {
			System.out
					.println("[fileServerTestMode] NFServer running on " + serverSocket.getLocalSocketAddress() + ".");
		}

		while (true) {
			/*
			 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
			 * otros peers que soliciten descargar ficheros.
			 */
			/*
			 * TODO: (Boletín SocketsTCP) Tras aceptar la conexión con un peer cliente, la
			 * comunicación con dicho cliente para servir los ficheros solicitados se debe
			 * implementar en el método serveFilesToClient, al cual hay que pasarle el
			 * socket devuelto por accept.
			 */

			try {
	            // (Boletín SocketsTCP) Esperar conexiones de otros peers
	            Socket clientSocket = serverSocket.accept();
	            System.out.println("* New client connected from " + clientSocket.getInetAddress());
	            
	            // Atender al cliente
	            serveFilesToClient(clientSocket);
	            
	        } catch (IOException e) {
	            System.err.println("Error accepting connection: " + e.getMessage());
	            break; 
	        }

		}
	}

	/**
	 * Método que ejecuta el hilo principal del servidor en segundo plano, esperando
	 * conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * TODO: (Boletín SocketsTCP) Usar el socket servidor para esperar conexiones de
		 * otros peers que soliciten descargar ficheros
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Al establecerse la conexión con un peer, la
		 * comunicación con dicho cliente se hace en el método
		 * serveFilesToClient(socket), al cual hay que pasarle el socket devuelto por
		 * accept
		 */
		/*
		 * TODO: (Boletín TCPConcurrente) Crear un hilo nuevo de la clase
		 * NFServerThread, que llevará a cabo la comunicación con el cliente que se
		 * acaba de conectar, mientras este hilo vuelve a quedar a la escucha de
		 * conexiones de nuevos clientes (para soportar múltiples clientes). Si este
		 * hilo es el que se encarga de atender al cliente conectado, no podremos tener
		 * más de un cliente conectado a este servidor.
		 */

		if (serverSocket == null || !serverSocket.isBound()) {
            return;
        }

        while (!stopServer) {
            try {
                // 1. Esperamos una nueva conexión de un cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("* New client connected from " + clientSocket.getInetAddress());

                /*
                 * TODO: (Boletín TCPConcurrente)
                 * En lugar de llamar a serveFilesToClient(clientSocket) directamente,
                 * creamos un hilo nuevo para este cliente específico.
                 */
                NFServerThread thread = new NFServerThread(this, clientSocket);
                thread.start(); //  lanzamos el run de NFServerThread y libera este bucle

            } catch (IOException e) {
                if (!stopServer) {
                    System.err.println("Error accepting connection: " + e.getMessage());
                }
            }
        }
        System.out.println("NFServer stopped.");


	}
	/*
	 * TODO: (Boletín SocketsTCP) Añadir métodos a esta clase para: 1) Arrancar el
	 * servidor en un hilo nuevo que se ejecutará en segundo plano 2) Detener el
	 * servidor (stopserver) 3) Obtener el puerto de escucha del servidor etc.
	 */

	/**
     * Método para detener el servidor de ficheros.
     * Cierra el socket para desbloquear el hilo que está en accept().
     */
    public void stopServer() {
        this.stopServer = true; 
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
               
                serverSocket.close(); 
            }
        } catch (IOException e) {
            System.err.println("Error while closing NFServer socket: " + e.getMessage());
        }
    }


	/**
	 * Método de clase que implementa el extremo del servidor del protocolo de
	 * transferencia de ficheros entre pares.
	 * 
	 * @param socket El socket para la comunicación con un cliente que desea
	 *               descargar ficheros.
	 */
	public static void serveFilesToClient(Socket socket) {
		/*
		 * TODO: (Boletín SocketsTCP) Crear dis/dos a partir del socket
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Mientras el cliente esté conectado, leer mensajes
		 * de socket, convertirlo a un objeto PeerMessage y luego actuar en función del
		 * tipo de mensaje recibido, enviando los correspondientes mensajes de
		 * respuesta.
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Para servir un fichero, hay que localizarlo a
		 * partir de su hash (o subcadena) en nuestra base de datos de ficheros
		 * compartidos. Los ficheros compartidos se pueden obtener con
		 * NanoFiles.db.getFiles(). Los métodos lookupHashSubstring y
		 * lookupFilenameSubstring de la clase FileInfo son útiles para buscar ficheros
		 * coincidentes con una subcadena dada del hash o del nombre del fichero. El
		 * método lookupFilePath() de FileDatabase devuelve la ruta al fichero a partir
		 * de su hash completo.
		 */

		try (DataInputStream dis = new DataInputStream(socket.getInputStream());
		         DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
		        
		        while (true) {
		            try {
		                PeerMessage msg = PeerMessage.readMessageFromInputStream(dis);
		                
		                if (msg.getOpcode() == PeerMessageOps.OPCODE_GET_FILE_LIST) {
		                    FileInfo[] myFiles = es.um.redes.nanoFiles.application.NanoFiles.db.getFiles();
		                    PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_LIST);
		                    response.setFileList(myFiles);
		                    response.writeMessageToOutputStream(dos);
		                } 
		                else if (msg.getOpcode() == PeerMessageOps.OPCODE_DOWNLOAD_FILE) {
		                    // 1. Obtenemos la subcadena de hash que nos pide el cliente
		                    String hashSubstring = msg.getFileHash();
		                    
		                    // 2. Buscamos entre todos nuestros ficheros compartidos
		                    FileInfo[] myFiles = es.um.redes.nanoFiles.application.NanoFiles.db.getFiles();
		                    FileInfo[] matches = FileInfo.lookupHashSubstring(myFiles, hashSubstring);
		                    
		                    if (matches.length == 0) {
		                        // Error: No coincide con ningún fichero
		                        System.err.println("✗ No file matches hash substring: " + hashSubstring);
		                        PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_NOT_FOUND);
		                        response.writeMessageToOutputStream(dos);
		                    } else if (matches.length > 1) {
		                        // Error: Ambiguo, hay múltiples coincidencias
		                        System.err.println("✗ Ambiguous hash substring: " + hashSubstring + " matches " + matches.length + " files");
		                        PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_NOT_FOUND);
		                        response.writeMessageToOutputStream(dos);
		                    } else {
		                        // Exactamente una coincidencia, servir el fichero
		                        FileInfo fileInfo = matches[0];
		                        java.io.File file = new java.io.File(fileInfo.filePath);
		                        
		                        if (file.exists()) {
		                            // 3. Confirmamos al cliente que el fichero existe
		                            PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_CHUNK);
		                            response.setFileName(file.getName());
		                            response.writeMessageToOutputStream(dos);
		                            
		                            // 4. Enviamos el tamaño
		                            dos.writeLong(file.length());
		                            
		                            // 5. Enviamos el contenido del fichero
		                            try (java.io.FileInputStream fis = new java.io.FileInputStream(file)) {
		                                byte[] buffer = new byte[8192];
		                                int bytesRead;
		                                while ((bytesRead = fis.read(buffer)) != -1) {
		                                    dos.write(buffer, 0, bytesRead);
		                                }
		                                dos.flush();
		                            }
		                        } else {
		                            PeerMessage response = new PeerMessage(PeerMessageOps.OPCODE_FILE_NOT_FOUND);
		                            response.writeMessageToOutputStream(dos);
		                    }
		                }
		                
		               }
		               }catch (java.io.EOFException e) {
		                break; 
		            }
		        }
		    } catch (IOException e) {
		        System.err.println("Error serving client: " + e.getMessage());
		    }

	}
}
