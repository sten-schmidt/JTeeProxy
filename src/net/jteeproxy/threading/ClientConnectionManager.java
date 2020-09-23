package net.jteeproxy.threading;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.jteeproxy.Destination;
import net.jteeproxy.ServerType;

public class ClientConnectionManager implements Runnable {
	private final Logger LOGGER = LogManager.getLogger("ClientConnectionManager");
	private Socket _clientSocket;
	private Socket _serverSocketPrimary;
	private Socket _serverSocketSecundary;
	private boolean _forwardingActivePrimary = false;
	private boolean _forwardingActiveSecondary = false;
	private int _sourcePort;
	private Destination _primaryDestination;
	private Destination _secundaryDestination;
	private Client2ServerForwarder _clientForwarder;
	private Thread _clientForwardThread;
	private Server2ClientForwarder _primaryServerForwarder;
	private Thread _primaryServerForwardThread;
	private Server2ClientForwarder _secondaryServerForwarder;
	private Thread _secondaryServerForwardThread;

	public ClientConnectionManager(Socket clientSocket, int sourcePort, Destination primaryDestination,
			Destination secundaryDestination) {
		_clientSocket = clientSocket;
		setSourcePort(sourcePort);
		setPrimaryDestination(primaryDestination);
		setSecundaryDestination(secundaryDestination);
	}

	public void run() {
		InputStream primaryInputStreamClient = null;
		OutputStream primaryOutputStreamClient = null;
		InputStream primaryInputStreamServer = null;
		OutputStream primaryOutputStreamServer = null;
		InputStream secondaryInputStreamServer = null;
		OutputStream secondaryOutputStreamServer = null;
		try {

			if (this.getPrimaryDestination().isEnabled()) {

				_serverSocketPrimary = new Socket(this.getPrimaryDestination().getHost(),
						this.getPrimaryDestination().getPort());
				_serverSocketPrimary.setKeepAlive(true);

				if (this.getSecondaryDestination().isEnabled()) {
					_serverSocketSecundary = new Socket(this.getSecondaryDestination().getHost(),
							this.getSecondaryDestination().getPort());
					_serverSocketSecundary.setKeepAlive(true);
				}

				_clientSocket.setKeepAlive(true);

				primaryInputStreamClient = _clientSocket.getInputStream();
				primaryOutputStreamClient = _clientSocket.getOutputStream();

				primaryInputStreamServer = _serverSocketPrimary.getInputStream();
				primaryOutputStreamServer = _serverSocketPrimary.getOutputStream();

				if (this.getSecondaryDestination().isEnabled()) {
					secondaryInputStreamServer = _serverSocketSecundary.getInputStream();
					secondaryOutputStreamServer = _serverSocketSecundary.getOutputStream();
				}
			}

		} catch (IOException ioe) {
			LOGGER.error("Can not connect to " + this.getPrimaryDestination().getHost() + ":"
					+ this.getPrimaryDestination().getPort());
			setConnectionErrorState();
			return;
		}

		_clientForwarder = new Client2ServerForwarder(this, primaryInputStreamClient, primaryOutputStreamServer,
				secondaryOutputStreamServer);
		_clientForwardThread = new Thread(_clientForwarder, _clientForwarder.getClass().getName());
		_clientForwardThread.start();

		_primaryServerForwarder = new Server2ClientForwarder(this, primaryInputStreamServer, primaryOutputStreamClient,
				"ServerA", ServerType.PRIMARY);
		_primaryServerForwardThread = new Thread(_primaryServerForwarder,
				_primaryServerForwarder.getClass().getName() + "_Primary");
		_primaryServerForwardThread.start();
		_forwardingActivePrimary = true;

		_secondaryServerForwarder = new Server2ClientForwarder(this, secondaryInputStreamServer,
				null /* no client-forwarding */, "ServerB", ServerType.SECONDARY);
		_secondaryServerForwardThread = new Thread(_secondaryServerForwarder,
				_secondaryServerForwarder.getClass().getName() + "_Secondary");
		_secondaryServerForwardThread.start();
		_forwardingActiveSecondary = true;

		if (this.getPrimaryDestination().isEnabled()) {
			LOGGER.info(String.format("TCP Forwarding %s:%s <---> %s:%s ( PRIMARY ) started.",
					_clientSocket.getInetAddress().getHostAddress(), _clientSocket.getPort(),
					_serverSocketPrimary.getInetAddress().getHostAddress(), _serverSocketPrimary.getPort()));
		}

		if (this.getSecondaryDestination().isEnabled()) {
			LOGGER.info(String.format("TCP Forwarding %s:%s <---> %s:%s (SECONDARY) started.",
					_clientSocket.getInetAddress().getHostAddress(), _clientSocket.getPort(),
					_serverSocketSecundary.getInetAddress().getHostAddress(), _serverSocketSecundary.getPort()));
		}
	}

	public synchronized void setConnectionErrorState() {
		try {
			if (_serverSocketPrimary != null)
				_serverSocketPrimary.close();
			if (_serverSocketSecundary != null)
				_serverSocketSecundary.close();
		} catch (Exception e) {
		}
		try {
			if (_serverSocketPrimary != null)
				_serverSocketPrimary.close();
			if (_serverSocketSecundary != null)
				_serverSocketSecundary.close();
		} catch (Exception e) {
		}
		if (_forwardingActivePrimary) {
			_forwardingActivePrimary = false;

			if (this.getPrimaryDestination().isEnabled()) {
				LOGGER.info(String.format("TCP Forwarding %s:%s <---> %s:%s ( PRIMARY ) stopped.",
						_clientSocket.getInetAddress().getHostAddress(), _clientSocket.getPort(),
						_serverSocketPrimary.getInetAddress().getHostAddress(), _serverSocketPrimary.getPort()));
			}
		}

		if (_forwardingActiveSecondary) {
			_forwardingActiveSecondary = false;

			if (this.getSecondaryDestination().isEnabled()) {
				LOGGER.info(String.format("TCP Forwarding %s:%s <---> %s:%s (SECONDARY) stopped.",
						_clientSocket.getInetAddress().getHostAddress(), _clientSocket.getPort(),
						_serverSocketSecundary.getInetAddress().getHostAddress(), _serverSocketSecundary.getPort()));

			}
		}
	}

	public int getSourcePort() {
		return _sourcePort;
	}

	public void setSourcePort(int sourcePort) {
		this._sourcePort = sourcePort;
	}

	public Destination getPrimaryDestination() {
		return _primaryDestination;
	}

	public void setPrimaryDestination(Destination destination) {
		this._primaryDestination = destination;
	}

	public Destination getSecondaryDestination() {
		return _secundaryDestination;
	}

	public void setSecundaryDestination(Destination destination) {
		this._secundaryDestination = destination;
	}

	public Socket getSecondaryServerSocket() {
		return _serverSocketSecundary;
	}

	public void setSecondaryServerSocket(Socket serverSocket) {
		this._serverSocketSecundary = serverSocket;
	}

}
