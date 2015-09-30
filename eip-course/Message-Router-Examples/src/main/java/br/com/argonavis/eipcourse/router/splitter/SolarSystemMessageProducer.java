package br.com.argonavis.eipcourse.router.splitter;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class SolarSystemMessageProducer {

	private Session session;
	private MessageProducer producer;

	SolarSystemMessageProducer(Connection con, Destination destination)
			throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(destination);
	}

	public void send() throws JMSException, IOException {
		String data = XMLUtils.loadFile("sol.xml");
		TextMessage message = session.createTextMessage(data);
		message.setStringProperty("Type", "Solar System");
		producer.send(message);
	}

	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			Destination queue = (Destination) ctx.lookup("a-channel");

			SolarSystemMessageProducer producer = new SolarSystemMessageProducer(con, queue);
			System.out.println("Will send messages...");
			producer.send();
			System.out.println("Done.");

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
