package es.um.redes.nanoFiles.tcp.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

//Esta clase proporciona la funcionalidad necesaria para intercambiar mensajes entre el cliente y el servidor
public class NFConnector {
	private Socket socket;
	private InetSocketAddress serverAddr;

	private DataInputStream dis;
    private DataOutputStream dos;


	public NFConnector(InetSocketAddress fserverAddr) throws UnknownHostException, IOException {
		serverAddr = fserverAddr;
		/*
		 * TODO: (Boletín SocketsTCP) Se crea el socket a partir de la dirección del
		 * servidor (IP, puerto). La creación exitosa del socket significa que la
		 * conexión TCP ha sido establecida.
		 */
		/*
		 * TODO: (Boletín SocketsTCP) Se crean los DataInputStream/DataOutputStream a
		 * partir de los streams de entrada/salida del socket creado. Se usarán para
		 * enviar (dos) y recibir (dis) datos del servidor.
		 */

		this.socket = new Socket(fserverAddr.getAddress(), fserverAddr.getPort());
        this.dos = new DataOutputStream(socket.getOutputStream());
        this.dis = new DataInputStream(socket.getInputStream());

	}
	
	
	public FileInfo[] getPeerFileList() throws IOException {
        // 1. Enviamos petición
        PeerMessage request = new PeerMessage(PeerMessageOps.OPCODE_GET_FILE_LIST);
        request.writeMessageToOutputStream(dos);
        
        // 2. Leemos respuesta
        PeerMessage response = PeerMessage.readMessageFromInputStream(dis);
        if (response.getOpcode() == PeerMessageOps.OPCODE_FILE_LIST) {
            return response.getFileList();
        }
        return null;
    }

	public void test() {
		/*
		 * TODO: (Boletín SocketsTCP) Enviar entero cualquiera a través del socket y
		 * después recibir otro entero, comprobando que se trata del mismo valor.
		 */
	}

	public String downloadFile(String hash, String localPath) throws IOException {
	    // 1. Pedir fichero
	    PeerMessage request = new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FILE);
	    request.setFileHash(hash);
	    request.writeMessageToOutputStream(dos);

	    // 2. Esperar respuesta
	    PeerMessage response = PeerMessage.readMessageFromInputStream(dis);
	    if (response.getOpcode() == PeerMessageOps.OPCODE_FILE_CHUNK) {
	        // AQUÍ ESTÁ EL NOMBRE REAL DEL ARCHIVO
	        String remoteFileName = response.getFileName();
	        
	        // El servidor va a enviar el tamaño del fichero justo después del mensaje
	        long fileSize = dis.readLong(); 
	        try (java.io.FileOutputStream fos = new java.io.FileOutputStream(localPath)) {
	            byte[] buffer = new byte[8192];
	            int bytesRead;
	            long totalRead = 0;
	            while (totalRead < fileSize && (bytesRead = dis.read(buffer, 0, (int)Math.min(buffer.length, fileSize - totalRead))) != -1) {
	                fos.write(buffer, 0, bytesRead);
	                totalRead += bytesRead;
	            }
	        }
	        // DEVUELVE EL NOMBRE DEL ARCHIVO DESCARGADO
	        return remoteFileName;
	    }
	    return null;
	}



	public InetSocketAddress getServerAddr() {
		return serverAddr;
	}

}
