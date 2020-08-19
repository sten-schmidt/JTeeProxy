package net.stenschmidt.jteeproxy.threading;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import net.stenschmidt.jteeproxy.Destination;
import net.stenschmidt.jteeproxy.ServerType;

public class ClientConnectionThread extends Thread {
	private Socket _clientSocket;
	private Socket _serverSocketPrimary;
	private Socket _serverSocketSecundary;
	private boolean _forwardingActive = false;
	private int _sourcePort;
	private Destination _primaryDestination;
	private Destination _secunradyDestinationB;

	public ClientConnectionThread(Socket clientSocket, int sourcePort, Destination primaryDestination,
			Destination secundaryDestination) {
		_clientSocket = clientSocket;
		setSourcePort(sourcePort);
		setDestinationA(primaryDestination);
		setDestinationB(secundaryDestination);
	}

	public void run() {
		InputStream clientInA = null;
		OutputStream clientOutA = null;
		InputStream serverInA = null;
		OutputStream serverOutA = null;
		InputStream serverInB = null;
		OutputStream serverOutB = null;
		try {

			if (this.getDestinationA().isEnabled()) {

				_serverSocketPrimary = new Socket(this.getDestinationA().getHost(), this.getDestinationA().getPort());
				_serverSocketPrimary.setKeepAlive(true);

				if (this.getDestinationB().isEnabled()) {
					_serverSocketSecundary = new Socket(this.getDestinationB().getHost(),
							this.getDestinationB().getPort());
					_serverSocketSecundary.setKeepAlive(true);
				}

				_clientSocket.setKeepAlive(true);

				clientInA = _clientSocket.getInputStream();
				clientOutA = _clientSocket.getOutputStream();

				serverInA = _serverSocketPrimary.getInputStream();
				serverOutA = _serverSocketPrimary.getOutputStream();

				if (this.getDestinationB().isEnabled()) {
					serverInB = _serverSocketSecundary.getInputStream();
					serverOutB = _serverSocketSecundary.getOutputStream();
				}
			}

		} catch (IOException ioe) {
			System.err.println(
					"Can not connect to " + this.getDestinationA().getHost() + ":" + this.getDestinationA().getPort());
			setConnectionErrorState();
			return;
		}

		_forwardingActive = true;

		Client2ServerForwardThread clientForward = new Client2ServerForwardThread(this, clientInA, serverOutA,
				serverOutB);
		clientForward.start();

		// Forward Server-Response from ServerA to Client, Server-Response from ServerB
		// will not forwared to Client
		Server2ClientForwardThread serverForward = new Server2ClientForwardThread(this, serverInA, clientOutA,
				"ServerA", ServerType.PRIMARY);
		serverForward.start();

		Server2ClientForwardThread serverForwardB = new Server2ClientForwardThread(this, serverInB,
				null /* no client-forwarding */, "ServerB", ServerType.SECONDARY);
		serverForwardB.start();

		if (this.getDestinationA().isEnabled()) {
			System.out.println(String.format("TCP Forwarding %s:%s <---> %s:%s ( PRIMARY ) started.",
					_clientSocket.getInetAddress().getHostAddress(), _clientSocket.getPort(),
					_serverSocketPrimary.getInetAddress().getHostAddress(), _serverSocketPrimary.getPort()));
		}

		if (this.getDestinationB().isEnabled()) {
			System.out.println(String.format("TCP Forwarding %s:%s <---> %s:%s (SECONDARY) started.",
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
		if (_forwardingActive) {
			_forwardingActive = false;

			if (this.getDestinationA().isEnabled()) {
				System.out.println(String.format("TCP Forwarding %s:%s <---> %s:%s ( PRIMARY ) stopped.",
						_clientSocket.getInetAddress().getHostAddress(), _clientSocket.getPort(),
						_serverSocketPrimary.getInetAddress().getHostAddress(), _serverSocketPrimary.getPort()));
			}

			if (this.getDestinationB().isEnabled()) {
				System.out.println(String.format("TCP Forwarding %s:%s <---> %s:%s (SECONDARY) stopped.",
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

	public Destination getDestinationA() {
		return _primaryDestination;
	}

	public void setDestinationA(Destination destinationA) {
		this._primaryDestination = destinationA;
	}

	public Destination getDestinationB() {
		return _secunradyDestinationB;
	}

	public void setDestinationB(Destination destinationB) {
		this._secunradyDestinationB = destinationB;
	}

	public Socket getServerSocketB() {
		return _serverSocketSecundary;
	}

	public void setServerSocketB(Socket serverSocketB) {
		this._serverSocketSecundary = serverSocketB;
	}

}
