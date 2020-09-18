package net.stenschmidt.jteeproxy.threading;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Client2ServerForwarder implements Runnable {
	private static final int BUFFER_SIZE = 8192;
	InputStream _inputStreamClient;
	OutputStream _outputStreamPrimaryServer;
	OutputStream _outputStreamSecondaryServer;
	ClientConnectionManager _parent;

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
		_parent = parent;
		_inputStreamClient = inputStreamClient;
		_outputStreamPrimaryServer = outputStreamPrimaryServer;
		_outputStreamSecondaryServer = outputStreamSecondaryServer;
	}

	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			while (!Thread.currentThread().isInterrupted()) {
				int bytesRead = -1;

				if (_inputStreamClient != null) {
					bytesRead = _inputStreamClient.read(buffer);
					if (bytesRead > 0) {
						String bufferInfo = (new String(buffer, 0, bytesRead)).trim();
						System.out.println("Client request: " + bufferInfo);
					}
				}

				if (bytesRead == -1)
					break;

				if (_outputStreamPrimaryServer != null)
					_outputStreamPrimaryServer.write(buffer, 0, bytesRead);
				if (_outputStreamSecondaryServer != null)
					_outputStreamSecondaryServer.write(buffer, 0, bytesRead);

				if (_outputStreamPrimaryServer != null)
					_outputStreamPrimaryServer.flush();
				if (_outputStreamSecondaryServer != null)
					_outputStreamSecondaryServer.flush();

			}
		} catch (IOException e) {
			System.out.println(String.format("Info (Client): connection is broken or was closed (%s)", e.toString()));
		}
		
		
		_parent.setConnectionErrorState();
	}
}
