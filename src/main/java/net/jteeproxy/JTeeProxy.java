package net.jteeproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.jteeproxy.threading.ClientConnectionManager;

public class JTeeProxy {
	private static final Logger LOGGER = LogManager.getLogger("JTeeProxy");
	public static int SOURCE_PORT = 0;
	public static String PRIMARY_DESTINATION_HOST = "";
	public static int PRIMARY_DESTINATION_PORT = 0;
	public static String SECONDARY_DESTINATION_HOST = "";
	public static int SECONDARY_DESTINATION_PORT = 0;
	
	public static void main(String[] args) throws IOException {

		if (args.length < 3) {
			LOGGER.info(
					"Usage: java Proxy <SourcePort> <PrimaryDestinationHost> <PrimaryDestinationPort> <SecondaryDestinationHost> <SecondaryDestinationPort>");
			LOGGER.info("SecondaryDestination is Optional");
			System.exit(1);
		}

		SOURCE_PORT = Integer.parseInt(args[0]);
		PRIMARY_DESTINATION_HOST = args[1];
		PRIMARY_DESTINATION_PORT = Integer.parseInt(args[2]);

		if (args.length == 5) {
			SECONDARY_DESTINATION_HOST = args[3];
			SECONDARY_DESTINATION_PORT = Integer.parseInt(args[4]);
		}
		
			
		startServer();
	}
	
	public static void startServer() throws IOException {
		LOGGER.info(String.format("JTeeProxy started with SourcePort %s", SOURCE_PORT));
		try (ServerSocket serverSocket = new ServerSocket(SOURCE_PORT)) {
			while (!Thread.currentThread().isInterrupted()) {
				Socket clientSocket = serverSocket.accept();
				ClientConnectionManager clientConnectionManager = new ClientConnectionManager(clientSocket, SOURCE_PORT,
						new Destination(PRIMARY_DESTINATION_HOST, PRIMARY_DESTINATION_PORT),
						new Destination(SECONDARY_DESTINATION_HOST, SECONDARY_DESTINATION_PORT));
				Thread clientConnectionManagerThread = new Thread(clientConnectionManager, clientConnectionManager.getClass().getName());
				clientConnectionManagerThread.start();
			}
		}
	}
}
