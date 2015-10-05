package br.com.argonavis.eipcourse.endpoint.txclient;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import br.com.argonavis.eipcourse.endpoint.mapper.Product;

public class SynchronizedMessageProducer implements MessageListener{

	private Connection con;
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;

	SynchronizedMessageProducer(Connection con, Destination inQueue) throws JMSException {
		// Transactional session
		session = con.createSession(true, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(inQueue);
		consumer.setMessageListener(this);
	}

	@Override
	public void onMessage(Message request) {
		try {
			System.out.println("Received message: " + request);
			Destination outQueue = request.getJMSReplyTo();
			producer = session.createProducer(outQueue);

			Message reply = session.createMessage();
			reply.setJMSCorrelationID(request.getJMSMessageID());
			reply.setStringProperty("Status", "OK");

			System.out.println("Sending response" + reply);
			producer.send(reply);

			System.out.println("Committing session");
			session.commit();

		} catch (Exception e) {
			System.out.println("Rolling back session!");
			try {
				session.rollback();
			} catch (JMSException e1) {
				e1.printStackTrace();
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
			Destination queue = (Destination) ctx.lookup("produtos");

			new SynchronizedMessageProducer(con, queue);
			System.out.println("Willwait 60 seconds...");
			Thread.sleep(60000);
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
