package lab3.jms;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class FileInboundAdapter {
	private File directory;
	private Destination channel;
	private Connection con;
	
	public FileInboundAdapter(File directory, Destination channel, Connection con) throws JMSException {
		this.directory = directory;
		this.channel = channel;
		this.con = con;
	}

	public void run(Executor thread) {
		thread.execute(new Runnable() {
			public void run() {
				Session session;
				MessageProducer producer;
				try {
					session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
					producer = session.createProducer(channel);

					List<File> files = loadFiles();
					if (!files.isEmpty()) {
						List<Message> messages = createMessages(session, files);
						send(messages, producer);
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void send(List<Message> messages, MessageProducer producer) {
		try {
			for (Message message : messages) {
				producer.send(message);
			}
			System.out.println(messages.size() + " messages sent!\n");
		} catch (JMSException e) {
			System.err.println("JMS Exception: " + e);
		}
	}

	public List<Message> createMessages(Session session, List<File> files) throws JMSException {
		List<Message> messages = new ArrayList<>();
		for (File file : files) {
			String contents = readContents(file);
			if (contents != null) {
				TextMessage message = session.createTextMessage();
				message.setStringProperty("FileName", "" + file.getName());
				message.setStringProperty("FileType", "" + file.getName().substring(file.getName().lastIndexOf('.')+1, file.getName().length()));
				message.setText(contents);
				messages.add(message);
			}
		}
		return messages;
	}

	public List<File> loadFiles() {
		List<File> files = new ArrayList<>();
		String[] fileNames = directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml") || name.endsWith(".csv") || name.endsWith(".txt");
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
		try (Reader reader = new FileReader(file); Writer writer = new StringWriter()) {
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
		}
	}

}
