package net.stenschmidt.jteeproxy.threading;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.stenschmidt.jteeproxy.ServerType;

public class Server2ClientForwarder implements Runnable {
	private static final int BUFFER_SIZE = 8192;
	InputStream _inputStreamServer;
	OutputStream _outputStreamClient;
	ClientConnectionManager _parent;
	String _serverName;
	ServerType _serverType;

	/**
	 * Creates a new thread to forward the response from ServerA to Client, response
	 * form ServerB will not forward to Client
	 * 
	 * @param parent
	 * @param inputStreamServer  Response-Stream form Server (primary)
	 * @param outputStreamClient Client-OutputStream
	 */
	public Server2ClientForwarder(ClientConnectionManager parent, InputStream inputStreamServer,
			OutputStream outputStreamClient, String serverName, ServerType serverType) {
		_parent = parent;
		_inputStreamServer = inputStreamServer;
		_outputStreamClient = outputStreamClient;
		_serverName = serverName;
		_serverType = serverType;
	}

	public void run() {
		byte[] buffer = new byte[BUFFER_SIZE];

		try {
			while (!Thread.currentThread().isInterrupted()) {
				int bytesRead = -1;

				if (_inputStreamServer != null) {
					bytesRead = _inputStreamServer.read(buffer);
					if (bytesRead > 0) {
						String bufferInfo = (new String(buffer, 0, bytesRead)).trim();
						System.out.println(_serverName + " response: " + bufferInfo);
					}
				}

				if (bytesRead == -1)
					break;

				if (_outputStreamClient != null) {
					_outputStreamClient.write(buffer, 0, bytesRead);
					_outputStreamClient.flush();
				}
			}
		} catch (IOException e) {
			System.out.println(String.format("Info %s (%s): connection is broken or was closed (%s)", _serverName,
					_serverType.toString(), e.toString()));
		}

		if (_serverType.equals(ServerType.PRIMARY)) {
			_parent.setConnectionErrorState();
		}
	}

}
