package net.jteeproxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.jteeproxy.threading.ClientConnectionManager;

public class JTeeProxy {
	private static final Logger LOGGER = LogManager.getLogger("JTeeProxy");
	private static int sourcePort = 0;
	private static String primaryDestinationHost = "";
	private static int primaryDestinationPort = 0;
	private static String secondaryDestinationHost = "";
	private static int secondaryDestinationPort = 0;

	public static void main(String[] args) throws IOException {

		if (args.length < 3) {
			LOGGER.info(
					"Usage: java Proxy <SourcePort> <PrimaryDestinationHost> <PrimaryDestinationPort> <SecondaryDestinationHost> <SecondaryDestinationPort>");
			LOGGER.info("SecondaryDestination is Optional");
			System.exit(1);
		}

		sourcePort = Integer.parseInt(args[0]);
		primaryDestinationHost = args[1];
		primaryDestinationPort = Integer.parseInt(args[2]);

		if (args.length == 5) {
			secondaryDestinationHost = args[3];
			secondaryDestinationPort = Integer.parseInt(args[4]);
		}

		new JTeeProxy().startServer();
	}

	public void startServer() throws IOException {
		LOGGER.info("JTeeProxy started with SourcePort {}", sourcePort);
		try (ServerSocket serverSocket = new ServerSocket(sourcePort)) {
			while (!Thread.currentThread().isInterrupted()) {
				Socket clientSocket = serverSocket.accept();
				ClientConnectionManager clientConnectionManager = new ClientConnectionManager(clientSocket, sourcePort,
						new Destination(primaryDestinationHost, primaryDestinationPort),
						new Destination(secondaryDestinationHost, secondaryDestinationPort));
				Thread clientConnectionManagerThread = new Thread(clientConnectionManager,
						clientConnectionManager.getClass().getName());
				clientConnectionManagerThread.start();
			}
		}
	}

	public static int getSourcePort() {
		return sourcePort;
	}

	public static void setSourcePort(int sourcePort) {
		JTeeProxy.sourcePort = sourcePort;
	}

	public static String getPrimaryDestinationHost() {
		return primaryDestinationHost;
	}

	public static void setPrimaryDestinationHost(String primaryDestinationHost) {
		JTeeProxy.primaryDestinationHost = primaryDestinationHost;
	}

	public static int getPrimaryDestinationPort() {
		return primaryDestinationPort;
	}

	public static void setPrimaryDestinationPort(int primaryDestinationPort) {
		JTeeProxy.primaryDestinationPort = primaryDestinationPort;
	}

	public static String getSecondaryDestinationHost() {
		return secondaryDestinationHost;
	}

	public static void setSecondaryDestinationHost(String secondaryDestinationHost) {
		JTeeProxy.secondaryDestinationHost = secondaryDestinationHost;
	}

	public static int getSecondaryDestinationPort() {
		return secondaryDestinationPort;
	}

	public static void setSecondaryDestinationPort(int secondaryDestinationPort) {
		JTeeProxy.secondaryDestinationPort = secondaryDestinationPort;
	}

}
