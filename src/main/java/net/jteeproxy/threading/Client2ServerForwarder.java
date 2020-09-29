package net.jteeproxy.threading;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client2ServerForwarder implements Runnable {
	private static final int BUFFER_SIZE = 8192;
	private final Logger logger = LogManager.getLogger("Client2ServerForwarder");
	private InputStream inputStreamClient;
	private OutputStream outputStreamPrimaryServer;
	private OutputStream outputStreamSecondaryServer;
	private ClientConnectionManager clientConnectionManager;

	/**
	 * Creates a new thread to forward the Client-InputStream to one or optional to
	 * two Server-OutputStreams.
	 * 
	 * @param parent
	 * @param inputStreamClient           Client-InputStream
	 * @param outputStreamPrimaryServer   Server-OutputStream (primary)
	 * @param outputStreamSecondaryServer Server-OutputStream (secondary)
	 */
	public Client2ServerForwarder(ClientConnectionManager parent, InputStream inputStreamClient,
			OutputStream outputStreamPrimaryServer, OutputStream outputStreamSecondaryServer) {
		this.clientConnectionManager = parent;
		this.inputStreamClient = inputStreamClient;
		this.outputStreamPrimaryServer = outputStreamPrimaryServer;
		this.outputStreamSecondaryServer = outputStreamSecondaryServer;
	}

	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			while (!Thread.currentThread().isInterrupted()) {
				int bytesRead = -1;

				if (inputStreamClient != null) {
					bytesRead = inputStreamClient.read(buffer);
					if (bytesRead > 0) {
						String bufferInfo = (new String(buffer, 0, bytesRead)).trim();
						logger.info("Client request: {}", bufferInfo);
					}
				}

				if (bytesRead == -1)
					break;

				if (outputStreamPrimaryServer != null)
					outputStreamPrimaryServer.write(buffer, 0, bytesRead);

				try {
					if (outputStreamSecondaryServer != null)
						outputStreamSecondaryServer.write(buffer, 0, bytesRead);
				} catch (IOException e1) {
					logger.info("Info (Client): secondary connection is broken or was closed ({})", e1.toString());
				}

				if (outputStreamPrimaryServer != null)
					outputStreamPrimaryServer.flush();

				try {
					if (outputStreamSecondaryServer != null)
						outputStreamSecondaryServer.flush();
				} catch (IOException e1) {
					logger.info("Info (Client): secondary connection is broken or was closed ({})", e1.toString());
				}

			}
		} catch (IOException e) {
			logger.info("Info (Client): primary connection is broken or was closed ({})", e.toString());
		}

		clientConnectionManager.setConnectionErrorState();
	}
}
