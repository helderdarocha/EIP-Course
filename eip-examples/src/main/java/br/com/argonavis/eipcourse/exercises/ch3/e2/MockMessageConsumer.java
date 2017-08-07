package br.com.argonavis.eipcourse.exercises.ch3.e2;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MockMessageConsumer {
	private Connection con;
	private Session session;
	private MessageConsumer consumer;

	MockMessageConsumer(Connection con, Destination destination)
			throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(destination);
		con.start();
	}

	public void consume() throws JMSException {
		Message m = null;
		while ((m = consumer.receive(1000)) != null) {
			if (m instanceof TextMessage) {
				TextMessage message = (TextMessage) m;
				String tipo = message.getStringProperty("Tipo");
				if (tipo != null && tipo.equals("xml")) {
					System.out.println(message.getText());
				}

			}
		}
	}

	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			// Destination queue = (Destination) ctx.lookup("inbound"); // exercise 3
			Destination queue = (Destination) ctx.lookup("printable-queue"); // exercise 6
			

			MockMessageConsumer consumer = new MockMessageConsumer(con, queue);
			System.out.println("Will consume messages...");
			consumer.consume();
			System.out.println("Done!");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (JMSException e) {
				}
			}
		}
	}
}
