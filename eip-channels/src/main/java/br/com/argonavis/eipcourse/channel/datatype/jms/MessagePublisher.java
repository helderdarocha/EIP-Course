package br.com.argonavis.eipcourse.channel.datatype.jms;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessagePublisher {

	private ConnectionFactory factory;
	private Topic inboundChannel;
	SimpleHeaderTypeRouter router;
	
	public MessagePublisher(SimpleHeaderTypeRouter router) {
		this.router = router;
	}

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		this.inboundChannel = (Topic) ctx.lookup("inbound-ps-channel");
	}

	public void sendJms11() {
		try (Connection con = factory.createConnection()) {
			
			Session session = con
					.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			// set router
			MessageConsumer consumer = session.createConsumer(inboundChannel);
			consumer.setMessageListener(router);
			con.start();
			
			MessageProducer producer = session.createProducer(inboundChannel);
			for (int i = 0; i < 10; i++) {
				Thread.sleep(100);
				TextMessage message = session.createTextMessage("Message number " + (i + 1) + " sent " + new Date());
				int type = (int) Math.ceil(Math.random() * 3); // 1, 2, 3
				message.setStringProperty("data-type", "type-" + type); // generates random type message
				System.out.println("Sending message " + (i + 1) + ", type " + type);
				producer.send(message);
			}
			
			System.out.println("All messages sent!");

		} catch (JMSException e) {
			System.err.println("JMS Exception: " + e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
