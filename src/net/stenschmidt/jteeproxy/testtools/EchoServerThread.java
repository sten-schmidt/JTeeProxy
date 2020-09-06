package net.stenschmidt.jteeproxy.testtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketAddress;

public class EchoServerThread extends Thread {
	private Socket socket;

	public EchoServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		SocketAddress socketAddress = socket.getRemoteSocketAddress();
		System.out.println("Connection to " + socketAddress + " established");
		try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
			out.println("Server is ready ...");
			String input;
			while ((input = in.readLine()) != null) {
				out.println(input);
			}
		} catch (IOException e) {
			System.err.println(e);
		} finally {
			System.out.println("Connection to " + socketAddress + " closed");
		}
	}
}
