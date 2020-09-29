package net.jteeproxy.testtools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EchoClient {
	private static final Logger logger = LogManager.getLogger("EchoClient");

	public static void main(String[] args) {
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		try (Socket socket = new Socket(host, port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {
			String msg = in.readLine();
			logger.info(msg);
			String line;
			while (!Thread.currentThread().isInterrupted()) {
				line = input.readLine();
				if (line == null || line.equals("q"))
					break;
				out.println(line);
				logger.info("Response from Server: {}", in.readLine());
			}
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

}
