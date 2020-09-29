package net.jteeproxy.threading;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.jteeproxy.ServerType;

public class Server2ClientForwarder implements Runnable {
	private static final int BUFFER_SIZE = 8192;
	private final Logger logger = LogManager.getLogger("Server2ClientForwarder");
	private InputStream inputStreamServer;
	private OutputStream outputStreamClient;
	private ClientConnectionManager clientConnectionManager;
	private String serverName;
	private ServerType serverType;

	/**
	 * Creates a new thread to forward the response from ServerA to Client, response
	 * form ServerB will not forward to Client
	 * 
	 * @param parent
	 * @param inputStreamServer
	 * @param outputStreamClient
	 * @param serverName
	 * @param serverType
	 */
	public Server2ClientForwarder(ClientConnectionManager parent, InputStream inputStreamServer,
			OutputStream outputStreamClient, String serverName, ServerType serverType) {
		this.clientConnectionManager = parent;
		this.inputStreamServer = inputStreamServer;
		this.outputStreamClient = outputStreamClient;
		this.serverName = serverName;
		this.serverType = serverType;
	}

	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			while (!Thread.currentThread().isInterrupted()) {
				int bytesRead = -1;

				if (inputStreamServer != null) {
					bytesRead = inputStreamServer.read(buffer);
					if (bytesRead > 0) {
						String bufferInfo = (new String(buffer, 0, bytesRead)).trim();
						logger.info("{} response: {}", serverName, bufferInfo);
					}
				}

				if (bytesRead == -1)
					break;

				if (outputStreamClient != null) {
					outputStreamClient.write(buffer, 0, bytesRead);
					outputStreamClient.flush();
				}
			}
		} catch (IOException e) {
			logger.info("Info {} ({}): connection is broken or was closed ({})", serverName, serverType.toString(),
					e.toString());
		}

		if (serverType.equals(ServerType.PRIMARY)) {
			clientConnectionManager.setConnectionErrorState();
		}
	}

}
