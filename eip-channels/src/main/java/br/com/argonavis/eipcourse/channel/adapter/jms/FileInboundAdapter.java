package br.com.argonavis.eipcourse.channel.adapter.jms;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FileInboundAdapter {
	private File directory;

	public FileInboundAdapter(File directory) throws JMSException {
		this.directory = directory;
	}

	public void send(List<Message> messages, MessageProducer producer) {
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

	public List<Message> createMessages(Session session, List<File> files) throws JMSException {
		List<Message> messages = new ArrayList<>();
		TextMessage message = session.createTextMessage();
		for (File file : files) {
			String xmlDocument = readContents(file);
			if (xmlDocument != null) {
				message.setStringProperty("Length", "" + file.length());
				message.setText(xmlDocument);
				messages.add(message);
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
				return name.endsWith(".xml");
			}
		});
		if (fileNames != null && fileNames.length > 0) {
			for (String fileName : fileNames) {
				files.add(new File(directory, fileName));
			}
		}
		return files;
	}

	public String readContents(File file) {
		Reader reader = null;
		Writer writer = null;
		try {
			reader = new FileReader(file);
			writer = new StringWriter();
			char[] buffer = new char[4096];
			int len = reader.read(buffer);
			while (len > 0) {
				writer.write(buffer, 0, len);
				len = reader.read(buffer);
			}
			writer.flush();
			return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {
			}
		}
	}

	public static void main(String[] args) throws Exception {
		FileInboundAdapter adapter = new FileInboundAdapter(new File("/tmp/jms/inbox"));
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination queue = (Destination) ctx.lookup("inbound-channel");
		Connection con = factory.createConnection();
		con.start();
		int polls = 12;
		while (polls > 0) {
			System.out.println("Checking for files... " + polls + " polls left.");
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			List<File> files = adapter.loadFiles();
			if (!files.isEmpty()) {
				List<Message> messages = adapter.createMessages(session, files);
				adapter.send(messages, producer);
			}
			Thread.sleep(5000); // wait 5 seconds
			--polls;
		}
	}
}
