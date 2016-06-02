import java.net.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.io.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ChatClient extends JFrame implements Runnable {
	protected DataInputStream i;
	protected DataOutputStream o;
	protected JTextArea output;
	protected TextField name;
	protected TextField input;
	protected Thread listener;

	public ChatClient(String title, InputStream i, OutputStream o) {
		super(title);
		this.i = new DataInputStream(new BufferedInputStream(i));
		this.o = new DataOutputStream(new BufferedOutputStream(o));
		setLayout(new BorderLayout());
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		JScrollPane scrPane = new JScrollPane(container);
		add("Center", scrPane);
		container.add(output = new JTextArea());
		output.setEditable(false);
		output.setWrapStyleWord(true);
		output.setLineWrap(true);
		add("South", input = new TextField());
		add("North", name = new TextField());
		name.setText("Enter Name");
		pack();
		setVisible(true);
		setSize(500, 500);
		input.requestFocus();
		listener = new Thread(this);
		listener.start();
		
		
		addWindowListener(new WindowAdapter()
		{
		    public void windowClosing(WindowEvent e)
		    {
		    	try {
		    		
					ChatClient.this.o.writeUTF(name.getText() + " has left the chat! " );
					ChatClient.this.o.flush();
			    	Thread.sleep(100);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		    	
		    	System.exit(0);
		    	
		    	
		    }
		});
		
	}

	public void run() {
		try {
			while (true) {
				String line = i.readUTF();
				output.append("\n" + line);
				output.setCaretPosition(output.getDocument().getLength());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			listener = null;
			input.hide();
			validate();
			try {
				o.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public boolean handleEvent(Event e) {
		if ((e.target == input) && (e.id == Event.ACTION_EVENT)) {
			try {
				o.writeUTF(name.getText() + ": " + input.getText());
				input.setText("");
				o.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
				listener.stop();
			}
			return true;
		} else if ((e.target == this) && (e.id == Event.WINDOW_DESTROY)) {
			if (listener != null)
				listener.stop();
			setVisible(false);
			return true;
		}
		return super.handleEvent(e);
	}

	public static void main(String args[]) throws IOException {
		if (args.length != 2)
			throw new RuntimeException("Syntax: ChatClient  ");
		Socket s = new Socket(args[0], Integer.parseInt(args[1]));
		new ChatClient("Chat " + args[0] + ":" + args[1], s.getInputStream(), s.getOutputStream());
	}
}