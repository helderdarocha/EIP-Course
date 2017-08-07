package br.com.argonavis.eipcourse.exercises.ch4.solution;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class GenericMessageConsumer implements MessageListener {
	private Connection con;
	private Session session;
	private MessageConsumer consumer;

	GenericMessageConsumer(Connection con, Destination destination)
			throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(destination);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message m) {
		try {
			if (m instanceof TextMessage) {
				TextMessage message = (TextMessage) m;
				String tipo = message.getStringProperty("Tipo");
				if (tipo != null && tipo.equals("xml")) {
					System.out.println(message.getText());
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();

			Destination topic = (Destination) ctx.lookup("printable-topic"); // exercise
																				// 4.2
																				// and
																				// 4.5

			GenericMessageConsumer consumer = new GenericMessageConsumer(con, topic);

			System.out.println("Receiving messages for 60 seconds...");
			Thread.sleep(60000);
			System.out.println("Done.");
			con.close();

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
