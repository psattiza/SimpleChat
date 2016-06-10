import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.image.BufferedImage;

import net.sf.jcarrierpigeon.*;
import org.jdesktop.*;

public class ChatClient extends JFrame implements Runnable {
	private static final Color POP_UP_COLOR = new Color(125,82,48);
	protected DataInputStream i;
	protected DataOutputStream o;
	protected JTextArea output;
	protected TextField name;
	protected TextField subject;
	protected TextField input;
	protected Thread listener;
	protected TextField introName;
	protected JFrame nameScreen;

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
		JPanel top = new JPanel();
		top.setLayout(new FlowLayout());
		add("North", top);
		top.add(name = new TextField());
		top.add(subject = new TextField());
		subject.setText("Enter chat subject");
		name.setText("Enter name here");
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		setSize(500, 500);
		input.requestFocus();
		listener = new Thread(this);
		listener.start();
		
		// iconURL is null when not found
		ImageIcon icon = new ImageIcon("src/bear2.png");
		setIconImage(icon.getImage());

		nameScreen = new JFrame();
		introName = new TextField();
		introName.setEditable(true);
		introName.addActionListener(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				ChatClient.this.name.setText(ChatClient.this.introName.getText());
				try {
					ChatClient.this.o.writeUTF(ChatClient.this.name.getText() + " has entered the chat! ");
					ChatClient.this.o.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ChatClient.this.nameScreen.dispose();
			}

		});
		introName.setText("Enter Name, Ya Dingus!");
		nameScreen.setLayout(new BorderLayout());
		nameScreen.add(introName);
		nameScreen.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		nameScreen.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				ChatClient.this.name.setText(ChatClient.this.introName.getText());
				try {
					ChatClient.this.o.writeUTF(ChatClient.this.name.getText() + " has entered the chat! ");
					ChatClient.this.o.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		JButton b = new JButton();
		nameScreen.add("South", b);
		b.setText("SetName");
		b.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ChatClient.this.name.setText(ChatClient.this.introName.getText());
				try {
					ChatClient.this.o.writeUTF(ChatClient.this.name.getText() + " has entered the chat! ");
					ChatClient.this.o.flush();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				ChatClient.this.nameScreen.dispose();
			}

		});
		nameScreen.setLocationRelativeTo(null);
		nameScreen.setVisible(true);
		nameScreen.setSize(200, 100);
		nameScreen.toFront();
		nameScreen.repaint();

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {

					ChatClient.this.o.writeUTF(name.getText() + " has left the chat! ");
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
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm ");
			while (true) {
				cal = Calendar.getInstance();
				String line = i.readUTF();
				output.append(sdf.format(cal.getTime())+line + "\n");
				output.setCaretPosition(output.getDocument().getLength());
				if (getState() == Frame.ICONIFIED) {
					JFrame popup = new JFrame();
					JTextArea msg = new JTextArea();
					popup.setLayout(new BorderLayout());
					msg.setFont(new Font("Arial Black", 80, 12));
					msg.setText(sdf.format(cal.getTime()) + line + "\n");
					msg.setWrapStyleWord(true);
					msg.setLineWrap(true);
					msg.setEditable(false);
					msg.setBorder(BorderFactory.createEmptyBorder(3, 7, 7, 7));
					msg.setBackground(POP_UP_COLOR);
					msg.setForeground(Color.WHITE);
					popup.add(msg);
					popup.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					popup.setLocationRelativeTo(null);
					popup.setUndecorated(true);
					popup.setSize(200,100);
					//popup.pack();
					popup.toFront();
					popup.repaint();
					popup.transferFocusBackward();
					popup.setFocusableWindowState(false);
					
					popup.getRootPane().setBorder(BorderFactory.createLineBorder(POP_UP_COLOR,10,true));
					Notification note = new Notification(popup, WindowPosition.BOTTOMRIGHT, 25, 25, 1000);
					NotificationQueue queue = new NotificationQueue();
					queue.add(note);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			listener = null;
			input.setVisible(false);
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
			}
			return true;
		} else if ((e.target == subject) && (e.id == Event.ACTION_EVENT)) {
			try{
				o.writeUTF(name.getText() + " changed chat subject to: " + subject.getText());
				this.setTitle(subject.getText());
				o.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
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

class ImagePanel extends JComponent {
    private Image image;
    public ImagePanel(Image image) {
        this.image = image;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, this);
    }
}