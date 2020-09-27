package net.jteeproxy.testtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EchoServerThread extends Thread {
	private final Logger LOGGER = LogManager.getLogger("EchoServerTread");
	private Socket socket;

	public EchoServerThread(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		SocketAddress socketAddress = socket.getRemoteSocketAddress();
		LOGGER.info("Connection to {} established", socketAddress);
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
			out.println("Server is ready ...");
			String input;
			while ((input = in.readLine()) != null) {
				out.println(input);
			}
		} catch (IOException e) {
			System.err.println(e);
			LOGGER.error(e.toString());
		} finally {
			LOGGER.info("Connection to {} closed", socketAddress);
		}
	}
	
}
