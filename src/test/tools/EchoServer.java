package test.tools;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class EchoServer {
	private int port;
	private int backlog;

	public EchoServer(int port, int backlog) {
		this.port = port;
		this.backlog = backlog;
	}

	public void startServer() {
		try (ServerSocket serverSocket = new ServerSocket(port, backlog)) {
			System.out.println("EchoServer1 on " + serverSocket.getLocalSocketAddress() + " started ...");
			process(serverSocket);
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	private void process(ServerSocket server) throws IOException {
		while (true) {
			SocketAddress socketAddress = null;
			try (Socket socket = server.accept();
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
				socketAddress = socket.getRemoteSocketAddress();
				System.out.println("Connection to " + socketAddress + " established");
				out.println("Server is ready ...");
				String input;
				while ((input = in.readLine()) != null) {
					System.out.println("Input from Client: " + input);
					out.println(input);
				}
			} catch (IOException e) {
				System.err.println(e);
			} finally {
				System.out.println("Connection to " + socketAddress + " closed");
			}
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
