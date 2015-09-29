package br.com.argonavis.eipcourse.exercises.ch3;

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
		// EXERCICIO 3
	}

	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			Destination queue = (Destination) ctx.lookup("inbound");

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
