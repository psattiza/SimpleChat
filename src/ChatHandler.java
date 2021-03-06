import java.net.*;
import java.io.*;
import java.util.*;

public class ChatHandler extends Thread {
	protected Socket s;
	protected DataInputStream i;
	protected DataOutputStream o;
	public static Vector<ChatHandler> handlers = new Vector<ChatHandler>();
	
	public ChatHandler(Socket s) throws IOException {
		this.s = s;
		i = new DataInputStream(new BufferedInputStream(s.getInputStream()));
		o = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));
	}

	

	public void run() {
		try {
			handlers.addElement(this);
			String msg;
			
			while ((msg = i.readUTF()) != null) {
				broadcast(msg);
				System.out.print(".");
			}
		} catch (IOException ex) {
			System.out.println("Client has left or error has occured. "+(handlers.size()-1)+ " remaining clients");
			//ex.printStackTrace();
		} finally {
			handlers.removeElement(this);
			try {
				s.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	protected static void broadcast(String message) {
		synchronized (handlers) {
			Enumeration<ChatHandler> e = handlers.elements();
			while (e.hasMoreElements()) {
				ChatHandler c = (ChatHandler) e.nextElement();
				try {
					synchronized (c.o) {
						c.o.writeUTF(message);
					}
					c.o.flush();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}