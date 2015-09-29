package br.com.argonavis.eipcourse.exercises.ch3;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class FileInboundAdapter {
	
	public static String INBOX = "/tmp/jms/inbox";
	
	private File directory;
	boolean delete = false;
	private Set<String> namesRead = new HashSet<>();
	
	private Session session;
	private MessageProducer producer;

	public FileInboundAdapter(Connection con, Destination destination, File directory, boolean delete) throws JMSException {
		this.directory = directory;
		this.delete = delete;
		
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(destination);
	}

	public void send(List<Message> messages) {
		try {
			for (Message message : messages) {
				System.out.println("Sending message");
				producer.send(message);
			}
			System.out.println(messages.size() + " messages sent!");
		} catch (JMSException e) {
			System.err.println("JMS Exception: " + e);
		}
	}

	public List<Message> createMessages(List<File> files) throws JMSException {
		List<Message> messages = new ArrayList<>();
		for (File file : files) {
			Message message = null;

			String type = file.getName()
					.substring(file.getName().lastIndexOf('.') + 1)
					.toLowerCase();
			if (type.equals("xml") || type.equals("txt")) {
				String data = FileUtils.readChars(file);
				message = session.createTextMessage(data);
			} else {
				byte[] data = FileUtils.readBytes(file);
				message = session.createBytesMessage();
				((BytesMessage) message).writeBytes(data);
			}

			message.setLongProperty("Length", file.length());
			message.setStringProperty("Filename", file.getName());
			message.setStringProperty("Tipo", type);
			messages.add(message);

			if (delete == true) {
				file.delete();
			}
		}
		return messages;
	}

	public List<File> loadFiles() {
		List<File> files = new ArrayList<>();
		String[] fileNames = directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return (name.endsWith(".xml") || name.endsWith(".png")
						|| name.endsWith(".jpg") || name.endsWith(".jpeg")
						|| name.endsWith(".pdf") || name.endsWith(".txt"))
						&& !namesRead.contains(name);
			}
		});
		if (fileNames != null && fileNames.length > 0) {
			for (String fileName : fileNames) {
				files.add(new File(directory, fileName));
				namesRead.add(fileName);
			}
		}
		System.out.println(files.size() + " files loaded!.");
		return files;
	}
	
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		
		Destination queue = (Destination) ctx.lookup("inbound");
		Connection con = factory.createConnection();
		con.start();
		
		FileInboundAdapter adapter = new FileInboundAdapter(con, queue, new File(FileInboundAdapter.INBOX), false);

		int polls = 12;
		while (polls > 0) {
			System.out.println("Checking for files... " + polls + " polls left.");
			List<File> files = adapter.loadFiles();
			if (!files.isEmpty()) {
				List<Message> messages = adapter.createMessages(files);
				adapter.send(messages);
			}
			Thread.sleep(5000); // wait 5 seconds
			--polls;
		}
		con.close();
	}
}
