package br.com.argonavis.eipcourse.translator.enricher;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class PojoMessageProducer {

	private Connection con;
	private Session session;
	private MessageProducer producer;
	
	PojoMessageProducer(Connection con, Destination destination) throws JMSException {
        session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
        producer = session.createProducer(destination);
	}
	
	public void send() throws JMSException {
		Produto p = new Produto(3);
		ObjectMessage message = session.createObjectMessage(p);
		message.setStringProperty("Tipo", "Produto");
		System.out.println("Sending " + p);
		producer.send(message);
	}

	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
			con = factory.createConnection();
			Destination queue = (Destination) ctx.lookup("produtos");
			
			PojoMessageProducer producer = new PojoMessageProducer(con, queue);
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
