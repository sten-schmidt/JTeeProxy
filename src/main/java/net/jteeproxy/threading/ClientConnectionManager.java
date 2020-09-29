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
	private final Logger logger = LogManager.getLogger("ClientConnectionManager");
	private Socket clientSocket;
	private Socket serverSocketPrimary;
	private Socket serverSocketSecundary;
	private boolean forwardingActivePrimary = false;
	private boolean forwardingActiveSecondary = false;
	private int sourcePort;
	private Destination primaryDestination;
	private Destination secundaryDestination;
	private Client2ServerForwarder clientForwarder;
	private Thread clientForwardThread;
	private Server2ClientForwarder primaryServerForwarder;
	private Thread primaryServerForwardThread;
	private Server2ClientForwarder secondaryServerForwarder;
	private Thread secondaryServerForwardThread;

	public ClientConnectionManager(Socket clientSocket, int sourcePort, Destination primaryDestination,
			Destination secundaryDestination) {
		setClientSocket(clientSocket);
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

				serverSocketPrimary = new Socket(this.getPrimaryDestination().getHost(),
						this.getPrimaryDestination().getPort());
				serverSocketPrimary.setKeepAlive(true);

				if (this.getSecondaryDestination().isEnabled()) {
					serverSocketSecundary = new Socket(this.getSecondaryDestination().getHost(),
							this.getSecondaryDestination().getPort());
					serverSocketSecundary.setKeepAlive(true);
				}

				clientSocket.setKeepAlive(true);

				primaryInputStreamClient = clientSocket.getInputStream();
				primaryOutputStreamClient = clientSocket.getOutputStream();

				primaryInputStreamServer = serverSocketPrimary.getInputStream();
				primaryOutputStreamServer = serverSocketPrimary.getOutputStream();

				if (this.getSecondaryDestination().isEnabled()) {
					secondaryInputStreamServer = serverSocketSecundary.getInputStream();
					secondaryOutputStreamServer = serverSocketSecundary.getOutputStream();
				}
			}

		} catch (IOException ioe) {
			logger.error("Can not connect to {}:{}", this.getPrimaryDestination().getHost(),
					this.getPrimaryDestination().getPort());
			setConnectionErrorState();
			return;
		}

		clientForwarder = new Client2ServerForwarder(this, primaryInputStreamClient, primaryOutputStreamServer,
				secondaryOutputStreamServer);
		clientForwardThread = new Thread(clientForwarder, clientForwarder.getClass().getName());
		clientForwardThread.start();

		primaryServerForwarder = new Server2ClientForwarder(this, primaryInputStreamServer, primaryOutputStreamClient,
				"ServerA", ServerType.PRIMARY);
		primaryServerForwardThread = new Thread(primaryServerForwarder,
				primaryServerForwarder.getClass().getName() + "_Primary");
		primaryServerForwardThread.start();
		forwardingActivePrimary = true;

		secondaryServerForwarder = new Server2ClientForwarder(this, secondaryInputStreamServer,
				null /* no client-forwarding */, "ServerB", ServerType.SECONDARY);
		secondaryServerForwardThread = new Thread(secondaryServerForwarder,
				secondaryServerForwarder.getClass().getName() + "_Secondary");
		secondaryServerForwardThread.start();
		forwardingActiveSecondary = true;

		if (this.getPrimaryDestination().isEnabled()) {
			logger.info("TCP Forwarding {}:{} <---> {}:{} ( PRIMARY ) started.",
					clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(),
					serverSocketPrimary.getInetAddress().getHostAddress(), serverSocketPrimary.getPort());
		}

		if (this.getSecondaryDestination().isEnabled()) {
			logger.info("TCP Forwarding {}:{} <---> {}:{} (SECONDARY) started.",
					clientSocket.getInetAddress().getHostAddress(), clientSocket.getPort(),
					serverSocketSecundary.getInetAddress().getHostAddress(), serverSocketSecundary.getPort());
		}
	}

	public synchronized void setConnectionErrorState() {
		try {
			if (serverSocketPrimary != null)
				serverSocketPrimary.close();
			if (serverSocketSecundary != null)
				serverSocketSecundary.close();
		} catch (Exception e) {
		}
		try {
			if (serverSocketPrimary != null)
				serverSocketPrimary.close();
			if (serverSocketSecundary != null)
				serverSocketSecundary.close();
		} catch (Exception e) {
		}
		if (forwardingActivePrimary) {
			forwardingActivePrimary = false;

			if (this.getPrimaryDestination().isEnabled()) {
				logger.info("TCP Forwarding {}:{} <---> {}:{} ( PRIMARY ) stopped.",
						clientSocket.getInetAddress().getHostAddress(), sourcePort,
						serverSocketPrimary.getInetAddress().getHostAddress(), serverSocketPrimary.getPort());
			}
		}

		if (forwardingActiveSecondary) {
			forwardingActiveSecondary = false;

			if (this.getSecondaryDestination().isEnabled()) {
				logger.info("TCP Forwarding {}:{} <---> {}:{} (SECONDARY) stopped.",
						clientSocket.getInetAddress().getHostAddress(), sourcePort,
						serverSocketSecundary.getInetAddress().getHostAddress(), serverSocketSecundary.getPort());

			}
		}
	}

	public int getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	public Destination getPrimaryDestination() {
		return primaryDestination;
	}

	public void setPrimaryDestination(Destination destination) {
		this.primaryDestination = destination;
	}

	public Destination getSecondaryDestination() {
		return secundaryDestination;
	}

	public void setSecundaryDestination(Destination destination) {
		this.secundaryDestination = destination;
	}

	public Socket getSecondaryServerSocket() {
		return serverSocketSecundary;
	}

	public void setSecondaryServerSocket(Socket serverSocket) {
		this.serverSocketSecundary = serverSocket;
	}

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

}
