package net.stenschmidt.jteeproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import net.stenschmidt.jteeproxy.threading.ClientConnectionThread;

public class JTeeProxy {
	public static int SOURCE_PORT = 0;
	public static String PRIMARY_DESTINATION_HOST = "";
	public static int PRIMARY_DESTINATION_PORT = 0;
	public static String SECONDARY_DESTINATION_HOST = "";
	public static int SECONDARY_DESTINATION_PORT = 0;

	public static void main(String[] args) throws IOException {

		if (args.length < 3) {
			System.out.println(
					"Usage: java Proxy <SourcePort> <PrimaryDestinationHost> <PrimaryDestinationPort> <SecondaryDestinationHost> <SecondaryDestinationPort>");
			System.out.println("SecondaryDestination is Optional");
			System.exit(1);
		}

		SOURCE_PORT = Integer.parseInt(args[0]);
		PRIMARY_DESTINATION_HOST = args[1];
		PRIMARY_DESTINATION_PORT = Integer.parseInt(args[2]);

		if (args.length == 5) {
			SECONDARY_DESTINATION_HOST = args[3];
			SECONDARY_DESTINATION_PORT = Integer.parseInt(args[4]);
		}

		try (ServerSocket serverSocket = new ServerSocket(SOURCE_PORT)) {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ClientConnectionThread clientThread = new ClientConnectionThread(clientSocket, SOURCE_PORT,
						new Destination(PRIMARY_DESTINATION_HOST, PRIMARY_DESTINATION_PORT),
						new Destination(SECONDARY_DESTINATION_HOST, SECONDARY_DESTINATION_PORT));
				clientThread.start();
			}
		}
	}
}
