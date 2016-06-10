import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {
	public String chatName="";

	public ChatServer(int port) throws IOException {
		ServerSocket server = new ServerSocket(port);
		while (server.isBound()) {
			Socket client = server.accept();
			System.out.println("Accepted from '" + client.getInetAddress()+"' Clients:" +(ChatHandler.handlers.size()+1));
			ChatHandler c = new ChatHandler(client);
			c.start();
		}
		System.out.println("Socket Disconneted");
		server.close();
	}

	public static void main(String args[]) throws IOException {
		if (args.length != 1)
			throw new RuntimeException("Syntax: ChatServer <port>");
		new ChatServer(Integer.parseInt(args[0]));
	}

}