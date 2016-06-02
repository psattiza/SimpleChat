import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {

	public ChatServer(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		while (true) {
			Socket client = server.accept();
			System.out.println("Accepted from " + client.getInetAddress());
			ChatHandler c = new ChatHandler(client);
			c.start();
		}
	}

	public static void main(String args[]) throws IOException {
		if (args.length != 1)
			throw new RuntimeException("Syntax: ChatServer <port>");
		new ChatServer(Integer.parseInt(args[0]));
	}

}