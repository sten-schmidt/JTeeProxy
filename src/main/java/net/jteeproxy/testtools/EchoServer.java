package net.jteeproxy.testtools;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EchoServer {
	private final Logger LOGGER = LogManager.getLogger("EchoServer");
	private int port;
	private int backlog;

	public EchoServer(int port, int backlog) {
		this.port = port;
		this.backlog = backlog;
	}

	public void startServer() {
		try (ServerSocket serverSocket = new ServerSocket(port, backlog)) {
			LOGGER.info("EchoServer on {} started ...", serverSocket.getLocalSocketAddress());
			while (true) {
				Socket socket = serverSocket.accept();
				new EchoServerThread(socket).start();
			}
		} catch (IOException e) {
			LOGGER.error(e.toString());
		}
	}

	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		int backlog = 50;
		if (args.length == 2)
			backlog = Integer.parseInt(args[1]);
		new EchoServer(port, backlog).startServer();
	}
}
