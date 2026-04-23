package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: (Boletín MensajesASCII) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con el
	 * directorio (valores posibles del campo "operation").
	 */
	public static final String OPERATION_INVALID = "invalid_operation";
	public static final String OPERATION_PING = "ping";
	
	
	public static final String OPERATION_PING_BAD = "pingBad";
	public static final String OPERATION_PING_OK = "pingOk";
	
	// Operaciones de FILELIST (obtener lista de ficheros del directorio)
		public static final String OPERATION_FILELIST = "fileList";
		public static final String OPERATION_FILELIST_OK = "fileListOk";
		public static final String OPERATION_FILELIST_BAD = "fileListBad";
	// Operaciones EXTRA para dirfiles multi-datagrama
		public static final String OPERATION_FILELIST_NEXT = "fileListNext";

		
		// Operaciones de REGISTER (registrar peer como servidor)
		public static final String OPERATION_REGISTER = "register";
		public static final String OPERATION_REGISTER_OK = "registerOk";
		public static final String OPERATION_REGISTER_BAD = "registerBad";
		
		// Operaciones de PEERLIST (obtener lista de peers registrados)
		public static final String OPERATION_PEERLIST = "peerList";
		public static final String OPERATION_PEERLIST_OK = "peerListOk";
		public static final String OPERATION_PEERLIST_BAD = "peerListBad";
		
		// Operaciones EXTRA para dirdl (descarga desde directorio)
		public static final String OPERATION_DIRDL       = "dirDl";
		public static final String OPERATION_DIRDL_OK    = "dirDlOk";
		public static final String OPERATION_DIRDL_BAD   = "dirDlBad";
	
	
	// TODO: definir las operaciones del protocolo de directorio




}
