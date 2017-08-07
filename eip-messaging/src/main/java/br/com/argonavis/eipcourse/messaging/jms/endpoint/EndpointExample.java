package br.com.argonavis.eipcourse.messaging.jms.endpoint;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class EndpointExample {
	
	public static final String INBOX = "/tmp/jms/inbox";

	private File dataFile = null;
	private File directory = new File(INBOX);

	/**
	 * Waits for file, opens it when received, extracts first line and sends it in a message.
	 */
	public void start() {
		System.out.print("Waiting for file:");
		while (dataFile == null) {
			dataFile = loadFile();
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
			System.out.print(".");
		}
		System.out.println("\nLoaded file. Will now process it and send message.");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(dataFile));
			String data = reader.readLine();
			dataFile.delete();
			sendMessage(data);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			try {
				reader.close();
			} catch (IOException e) {}
		}
	}

	private File loadFile() {
		String[] files = directory.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".txt");
			}
		});
		if (files.length > 0) {
			return new File(directory, files[0]);
		}
		return null;
	}

	private void sendMessage(String data) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Destination destination = (Queue) ctx.lookup("contagem");
		Connection con = factory.createConnection();
		con.start();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(destination);
		TextMessage message = session.createTextMessage(data);
		producer.send(message);
		System.out.println("Mensagem enviada!");
	}
	
	public static void main(String[] args) {
		new EndpointExample().start();
	}
}
