package es.um.redes.nanoFiles.shell;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

import es.um.redes.nanoFiles.application.NanoFiles;

public class NFShell {
	/**
	 * Scanner para leer comandos de usuario de la entrada estándar
	 */
	private Scanner reader;

	byte command = NFCommands.COM_INVALID;
	String[] commandArgs = new String[0];

	boolean enableComSocketIn = false;
	private boolean skipValidateArgs;

	
	public static final String FILENAME_TEST_SHELL = ".nanofiles-test-shell";
	public static boolean enableVerboseShell = false;

	public NFShell() {
		reader = new Scanner(System.in);

		System.out.println("NanoFiles shell");
		System.out.println("For help, type 'help'");
	}

	public byte getCommand() {
		return command;
	}

	public String[] getCommandArguments() {
		return commandArgs;
	}

	// Esperamos hasta obtener un comando válido entre los comandos existentes
	public void readGeneralCommand() {
		boolean validArgs;
		do {
			commandArgs = readGeneralCommandFromStdIn();
			validArgs = validateCommandArguments(commandArgs);
		} while (!validArgs);
	}

	public String chooseDirectory(String defaultDirectory) {
		char response;
		String directory = null;
		do {
			System.out.print(
					"Do you want to use '" + defaultDirectory + "' as location of the directory server? (y/n): ");
			String input = reader.nextLine().trim().toLowerCase();
			if (input.length() == 1) { // Verificamos que la entrada es un solo carácter
				response = input.charAt(0);
				if (response == 'y') {
					directory = defaultDirectory;
				} else if (response == 'n') {
					System.out.print("Enter the directory hostname/IP:");
					directory = reader.nextLine().trim().toLowerCase();
				} else {
					System.out.println("Invalid key! Please, answer 'y' or 'n'.");
				}
			}
		} while (directory == null);
		System.out.println("Using directory location: " + directory);
		return directory;
	}

	// Usamos la entrada estándar para leer comandos y procesarlos
	private String[] readGeneralCommandFromStdIn() {
		String[] args = new String[0];
		Vector<String> vargs = new Vector<String>();
		while (true) {
			System.out.print("(nanoFiles@" + NanoFiles.sharedDirname + ") ");
			// obtenemos la línea tecleada por el usuario
			String input = reader.nextLine();
			StringTokenizer st = new StringTokenizer(input);
			// si no hay ni comando entonces volvemos a empezar
			if (st.hasMoreTokens() == false) {
				continue;
			}
			// traducimos la cadena del usuario en el código de comando correspondiente
			command = NFCommands.stringToCommand(st.nextToken());
			if (enableVerboseShell) {
				System.out.println(input);
			}
			skipValidateArgs = false;
			switch (command) {
			case NFCommands.COM_INVALID:
				System.out.println("Invalid command");
				continue;
			case NFCommands.COM_HELP:
				NFCommands.printCommandsHelp();
				continue;
			case NFCommands.COM_QUIT:
			case NFCommands.COM_FILELIST_DIR:
			case NFCommands.COM_MYFILES:
			case NFCommands.COM_SERVE:
			case NFCommands.COM_PEERLIST:
			case NFCommands.COM_PING:
				// Estos comandos son válidos sin parámetros
				break;
			case NFCommands.COM_FILELIST_PEER:
			case NFCommands.COM_DOWNLOAD_DIR:
			case NFCommands.COM_DOWNLOAD_PEER:
			case NFCommands.COM_NICK:
				// Estos requieren parámetros
				while (st.hasMoreTokens()) {
					vargs.add(st.nextToken());
				}
				break;
			default:
				skipValidateArgs = true;
				System.out.println("Invalid command");
				;
			}
			break;
		}
		return vargs.toArray(args);
	}

	// Algunos comandos requieren un parámetro
	// Este método comprueba si se proporciona parámetro para los comandos
	private boolean validateCommandArguments(String[] args) {
		if (skipValidateArgs)
			return false;
		switch (this.command) {
		case NFCommands.COM_DOWNLOAD_DIR:
			if (args.length != 1) {
				System.out.println(
						"Correct use:" + NFCommands.commandToString(command) + " <hash_substring>");
				return false;
			}
			break;
		case NFCommands.COM_DOWNLOAD_PEER:
			if (args.length != 2) {
				System.out.println("Correct use:" + NFCommands.commandToString(command)
						+ " <peer_nickname> <hash_substring>");
				return false;
			}
			break;
		case NFCommands.COM_FILELIST_PEER:
			if (args.length != 1) {
				System.out.println("Correct use:" + NFCommands.commandToString(command) + " <peer_nickname>");
				return false;
			}
			break;
		case NFCommands.COM_NICK:
			if (args.length != 1) {
				System.out.println("Correct use:" + NFCommands.commandToString(command) + " <new_nickname>");
				return false;
			}
			break;
		default:
		}
		// El resto no requieren parámetro
		return true;
	}

	public static void enableVerboseShell() {
		enableVerboseShell = true;
	}
}
