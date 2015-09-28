package br.com.argonavis.eipcourse.channel.pubsub.jms;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessagePublisher {

	private ConnectionFactory factory;
	private Topic topic;

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		this.topic = (Topic) ctx.lookup("simple-ps-channel");
	}

	public void sendJms11() {
		try (Connection con = factory.createConnection()) {
			con.start();
			Session session = con
					.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(topic);

			System.out.println("Provider: "
					+ con.getMetaData().getJMSProviderName() + " "
					+ con.getMetaData().getProviderVersion());

			for (int i = 0; i < 10; i++) {
				System.out.println("Sending message " + (i + 1));
				TextMessage message = session.createTextMessage("Message number " + (i + 1) + " sent " + new Date());
				producer.send(message);
			}

			System.out.println("All messages sent!");
		} catch (JMSException e) {
			System.err.println("JMS Exception: " + e);
		}
	}

	public static void main(String[] args) throws NamingException {
		MessagePublisher publisher = new MessagePublisher();
		publisher.init();

		publisher.sendJms11();

	}

}
