package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import es.um.redes.nanoFiles.util.FileInfo;

public class PeerMessage {

	private byte opcode;

	/*
	 * TODO: (Boletín MensajesBinarios) Añadir atributos u otros constructores
	 * específicos para crear mensajes con otros campos, según sea necesario
	 * 
	 */

	private FileInfo[] fileList; // Atributo para la lista de ficheros
	private String fileHash; // NUEVO: Para pedir un fichero
	private String fileName; // NUEVO: Para saber el nombre al descargar

	// ... (tus constructores y getters existentes) ...

	public void setFileHash(String hash) { this.fileHash = hash; }
	public String getFileHash() { return fileHash; }
	public void setFileName(String name) { this.fileName = name; }
	public String getFileName() { return fileName; }

	public void setFileList(FileInfo[] files) {
		this.fileList = files;
	}

	public FileInfo[] getFileList() {
		return fileList;
	}

	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	
	

	/*
	 * TODO: (Boletín MensajesBinarios) Crear métodos getter y setter para obtener
	 * los valores de los atributos de un mensaje. Se aconseja incluir código que
	 * compruebe que no se modifica/obtiene el valor de un campo (atributo) que no
	 * esté definido para el tipo de mensaje dado por "operation".
	 */
	public byte getOpcode() {
		return opcode;
	}

	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: (Boletín MensajesBinarios) En función del tipo de mensaje, leer del
		 * socket a través del "dis" el resto de campos para ir extrayendo con los
		 * valores y establecer los atributos del un objeto DirMessage que contendrá
		 * toda la información del mensaje, y que será devuelto como resultado. NOTA:
		 * Usar dis.readFully para leer un array de bytes, dis.readInt para leer un
		 * entero, etc.
		 */
		byte opcode = dis.readByte();
		PeerMessage message = new PeerMessage(opcode);
		switch (opcode) {
			case PeerMessageOps.OPCODE_GET_FILE_LIST:
				break;
			case PeerMessageOps.OPCODE_FILE_LIST:
				int numFiles = dis.readInt();
				FileInfo[] files = new FileInfo[numFiles];
				for (int i = 0; i < numFiles; i++) {
					files[i] = FileInfo.fromInputStream(dis);
				}
				message.setFileList(files);
				break;
			case PeerMessageOps.OPCODE_DOWNLOAD_FILE: // Cliente pide hash
				message.setFileHash(dis.readUTF());
				break;
			case PeerMessageOps.OPCODE_FILE_NOT_FOUND: // Servidor no lo tiene
				break;
			case PeerMessageOps.OPCODE_FILE_CHUNK: // Servidor envía info previa al chorro de bytes
				message.setFileName(dis.readUTF());
				break;
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO (Boletín MensajesBinarios): Escribir los bytes en los que se codifica el
		 * mensaje en el socket a través del "dos", teniendo en cuenta opcode del
		 * mensaje del que se trata y los campos relevantes en cada caso. NOTA: Usar
		 * dos.write para leer un array de bytes, dos.writeInt para escribir un entero,
		 * etc.
		 */

		dos.writeByte(opcode);
		switch (opcode) {
			case PeerMessageOps.OPCODE_GET_FILE_LIST:
				break;
			case PeerMessageOps.OPCODE_FILE_LIST:
				dos.writeInt(fileList.length);
				for (FileInfo file : fileList) {
					file.toOutputStream(dos);
				}
				break;
			case PeerMessageOps.OPCODE_DOWNLOAD_FILE:
				dos.writeUTF(fileHash);
				break;
			case PeerMessageOps.OPCODE_FILE_CHUNK:
				dos.writeUTF(fileName);
				break;
		}
	
	}

}
