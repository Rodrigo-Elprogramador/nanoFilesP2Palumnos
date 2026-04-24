package es.um.redes.nanoFiles.logic;

import java.net.InetSocketAddress;
import java.io.IOException;
import es.um.redes.nanoFiles.tcp.client.NFConnector;
import es.um.redes.nanoFiles.application.NanoFiles;



import es.um.redes.nanoFiles.tcp.server.NFServer;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFControllerLogicP2P {
	// Servidor TCP local para compartir ficheros con otros peers
	private NFServer fileServer = null;

	

	protected NFControllerLogicP2P() {
	}

	/**
	 * Método para ejecutar un servidor de ficheros en segundo plano. Debe arrancar
	 * el servidor en un nuevo hilo creado a tal efecto.
	 * 
	 * @return Verdadero si se ha arrancado en un nuevo hilo con el servidor de
	 *         ficheros, y está a la escucha en un puerto, falso en caso contrario.
	 * 
	 */
	protected boolean startFileServer() {
		boolean serverRunning = false;
		/*
		 * Comprobar que no existe ya un objeto NFServer previamente creado, en cuyo
		 * caso el servidor ya está en marcha.
		 */
		if (fileServer != null) {
			System.err.println("File server is already running");
			return true;
		} else {
			/*
			 * TODO: (Boletín Servidor TCP concurrente) Arrancar servidor en segundo plano
			 * creando un nuevo hilo, comprobar que el servidor está escuchando en un puerto
			 * válido (>0), imprimir mensaje informando sobre el puerto de escucha, y
			 * devolver verdadero. Las excepciones que puedan lanzarse deben ser capturadas
			 * y tratadas en este método. Si se produce una excepción de entrada/salida
			 * (error del que no es posible recuperarse), se debe informar sin abortar el
			 * programa
			 * 
			 */
			try {
	            // 1. Creamos la instancia del servidor (esto crea el ServerSocket)
	            fileServer = new NFServer();
	            
	            // 2. Creamos un hilo para que el servidor corra en segundo plano
	            Thread serverThread = new Thread(fileServer);
	            
	            // 3. Arrancamos el hilo
	            serverThread.start();
	            
	            // 4. Comprobamos que el puerto es válido
	            int port = fileServer.getPort();
	            if (port > 0) {
	                System.out.println("* TCP File Server running on port " + port);
	                serverRunning = true;
	            } else {
	                System.err.println("Error: File server bound to an invalid port.");
	                fileServer = null;
	            }
	            
	        } catch (IOException e) {
	            System.err.println("Cannot start the file server: " + e.getMessage());
	            fileServer = null;
	        }



		}
		return serverRunning;

	}
	

	protected void testTCPServer() {
		assert (NanoFiles.testModeTCP);
		/*
		 * Comprobar que no existe ya un objeto NFServer previamente creado, en cuyo
		 * caso el servidor ya está en marcha.
		 */
		assert (fileServer == null);
		try {

			fileServer = new NFServer();
			/*
			 * (Boletín SocketsTCP) Inicialmente, se creará un NFServer y se ejecutará su
			 * método "test" (servidor minimalista en primer plano, que sólo puede atender a
			 * un cliente conectado). Posteriormente, se desactivará "testModeTCP" para
			 * implementar un servidor en segundo plano, que se ejecute en un hilo
			 * secundario para permitir que este hilo (principal) siga procesando comandos
			 * introducidos mediante el shell.
			 */
			fileServer.test();
			
		} catch (IOException e1) {
			e1.printStackTrace();
			System.err.println("Cannot start the file server");
			fileServer = null;
		}
	}

	public void testTCPClient() {

		assert (NanoFiles.testModeTCP);
		/*
		 * (Boletín SocketsTCP) Inicialmente, se creará un NFConnector (cliente TCP)
		 * para conectarse a un servidor que esté escuchando en la misma máquina y un
		 * puerto fijo. Después, se ejecutará el método "test" para comprobar la
		 * comunicación mediante el socket TCP. Posteriormente, se desactivará
		 * "testModeTCP" para implementar la descarga de un fichero desde múltiples
		 * servidores.
		 */

		try {
			NFConnector nfConnector = new NFConnector(new InetSocketAddress(NFServer.PORT));
			nfConnector.test();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método para listar los ficheros de un peer concreto vía TCP e imprimirlos por
	 * pantalla.
	 * 
	 * @param La dirección del peer cuyos ficheros se quiere listar
	 * @return Verdadero si se ha obtenido exitosamente el listado de fichero del
	 *         peer
	 */
	protected boolean listPeerFiles(InetSocketAddress peerAddr) {
		boolean success = false;
		try {
            // 1. Creamos el conector con la dirección que recibimos por parámetro
            NFConnector connector = new NFConnector(peerAddr);
            
            // 2. Pedimos la lista de ficheros
            FileInfo[] files = connector.getPeerFileList();
            
            // 3. Si hemos recibido algo, lo imprimimos
            if (files != null) {
                System.out.println("\n* Files shared by peer at " + peerAddr + ":");
                if (files.length == 0) {
                    System.out.println("  (this peer is not sharing any files)");
                } else {
                    for (FileInfo file : files) {
                        System.out.println("  - " + file.fileName + " [" + file.fileSize + " bytes] (Hash: " + file.fileHash + ")");
                    }
                }
                success = true;
            } else {
                System.err.println("✗ Error: Received null file list from peer.");
            }
            
            
        } catch (IOException e) {
            System.err.println("✗ Error connecting to peer to get file list: " + e.getMessage());
        }

		return success;
	}

	/**
	 * Descarga un fichero identificado por subcadena de hash desde uno o varios
	 * peers. Si se pasa "*" como nickname, usa el directorio para localizar los
	 * peers que tienen el hash.
	 */
	protected boolean downloadFromPeers(NFControllerLogicDir dirLogic, String targetPeerNickname,
	        String targetHashSubstring) {
	    
	    // 1. Obtener el mapa de todos los peers del directorio
	    java.util.Map<String, InetSocketAddress> peers = dirLogic.fetchPeerList();
	    
	    // 2. Buscamos la dirección del peer que nos han pedido
	    InetSocketAddress peerAddr = peers.get(targetPeerNickname);

	    if (peerAddr == null) {
	        System.err.println("✗ Peer not found in directory: " + targetPeerNickname);
	        return false;
	    }

	    try {
	        // 3. Creamos el conector TCP hacia ese peer
	        NFConnector connector = new NFConnector(peerAddr);
	        
	        // 4. Descargamos en una ubicación temporal con un nombre único
	        String folder = NanoFiles.sharedDirname; 
	        String tempPath = folder + "/download_" + targetHashSubstring.substring(0, 5);

	        // 5. downloadFile() ahora devuelve el nombre del archivo
	        String remoteFileName = connector.downloadFile(targetHashSubstring, tempPath);
	        
	        if (remoteFileName != null) {
	            // 6. Renombra el archivo al nombre real 
	            java.nio.file.Path tempFile = java.nio.file.Paths.get(tempPath);
	            java.nio.file.Path finalPath = java.nio.file.Paths.get(folder, remoteFileName);
	            java.nio.file.Files.move(tempFile, finalPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	            
	            System.out.println("✓ File downloaded successfully to: " + finalPath);
	            return true;
	        } else {
	            System.err.println("✗ Peer " + targetPeerNickname + " does not have the requested file.");
	        }
	    } catch (IOException e) {
	        System.err.println("✗ Connection error with peer " + targetPeerNickname + ": " + e.getMessage());
	    }
	    return false;
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param serverAddressList   La lista de direcciones de los servidores a los
	 *                            que se conectará
	 * @param targetHashSubstring Subcadena del hash del fichero a descargar
	 */
	protected boolean downloadFileFromServers(InetSocketAddress[] serverAddressList, String targetHashSubstring) {
		boolean downloaded = false;

		if (serverAddressList.length == 0) {
			System.err.println("* Cannot start download - No list of server addresses provided");
			return false;
		}
		




		return downloaded;
	}

	private String toDisplayPath(java.nio.file.Path path) {
		java.nio.file.Path abs = path.toAbsolutePath().normalize();
		java.nio.file.Path cwd = java.nio.file.Paths.get("").toAbsolutePath().normalize();
		if (abs.startsWith(cwd)) {
			return cwd.relativize(abs).toString();
		}
		return path.toString();
	}

	/**
	 * Método para obtener el puerto de escucha de nuestro servidor de ficheros
	 * 
	 * @return El puerto en el que escucha el servidor, o 0 en caso de error.
	 */
	protected int getServerPort() {
		
		
		if (fileServer != null) {
	        return fileServer.getPort();
	    }
	    return 0;
	}

	/**
	 * Método para detener nuestro servidor de ficheros en segundo plano
	 * 
	 */
	protected void stopFileServer() {
		
		if (fileServer != null) {
	        fileServer.stopServer();
	        fileServer = null;
	        System.out.println("* TCP File Server stopped.");
	    }


	}

	protected boolean serving() {
		return (fileServer != null);

	}

}
