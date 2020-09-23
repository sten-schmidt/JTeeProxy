package net.jteeproxy.testtools;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
	private int port;
	private int backlog;

	public EchoServer(int port, int backlog) {
		this.port = port;
		this.backlog = backlog;
	}

	public void startServer() {
		try (ServerSocket serverSocket = new ServerSocket(port, backlog)) {
			System.out.println("EchoServer on " + serverSocket.getLocalSocketAddress() + " started ...");
			while (true) {
				Socket socket = serverSocket.accept();
				new EchoServerThread(socket).start();
			}
		} catch (IOException e) {
			System.err.println(e);
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
