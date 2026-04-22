package es.um.redes.nanoFiles.tcp.message;

import java.util.Map;
import java.util.TreeMap;

public class PeerMessageOps {

	public static final byte OPCODE_INVALID_CODE = 0;

	/*
	 * TODO: (Boletín MensajesBinarios) Añadir aquí todas las constantes que definen
	 * los diferentes tipos de mensajes del protocolo de comunicación con un par
	 * servidor de ficheros (valores posibles del campo "operation").
	 */

	public static final byte OPCODE_GET_FILE_LIST = 1;  // Cliente pide lista
	public static final byte OPCODE_FILE_LIST = 2;      // Servidor envía lista
	public static final byte OPCODE_DOWNLOAD_FILE = 3;  // Cliente pide un fichero (por hash)
	public static final byte OPCODE_FILE_NOT_FOUND = 4; // Servidor dice que no tiene el fichero
	public static final byte OPCODE_FILE_CHUNK = 5;     // Servidor envía un trozo del fichero


	/*
	 * TODO: (Boletín MensajesBinarios) Definir constantes con nuevos opcodes de
	 * mensajes definidos anteriormente, añadirlos al array "valid_opcodes" y añadir
	 * su representación textual a "valid_operations_str" EN EL MISMO ORDEN.
	 */
	private static final Byte[] _valid_opcodes = { 
			OPCODE_INVALID_CODE,
			OPCODE_GET_FILE_LIST,
			OPCODE_FILE_LIST,
			OPCODE_DOWNLOAD_FILE,
			OPCODE_FILE_NOT_FOUND,
			OPCODE_FILE_CHUNK
		};

		
		private static final String[] _valid_operations_str = { 
			"INVALID_OPCODE",
			"GET_FILE_LIST",
			"FILE_LIST",
			"DOWNLOAD_FILE",
			"FILE_NOT_FOUND",
			"FILE_CHUNK"
		};

	private static Map<String, Byte> _operation_to_opcode;
	private static Map<Byte, String> _opcode_to_operation;

	static {
		_operation_to_opcode = new TreeMap<>();
		_opcode_to_operation = new TreeMap<>();
		for (int i = 0; i < _valid_operations_str.length; ++i) {
			_operation_to_opcode.put(_valid_operations_str[i].toLowerCase(), _valid_opcodes[i]);
			_opcode_to_operation.put(_valid_opcodes[i], _valid_operations_str[i]);
		}
	}

	/**
	 * Transforma una cadena en el opcode correspondiente
	 */
	protected static byte operationToOpcode(String opStr) {
		return _operation_to_opcode.getOrDefault(opStr.toLowerCase(), OPCODE_INVALID_CODE);
	}

	/**
	 * Transforma un opcode en la cadena correspondiente
	 */
	public static String opcodeToOperation(byte opcode) {
		return _opcode_to_operation.getOrDefault(opcode, null);
	}
}
