package br.com.argonavis.eipcourse.channel.datatype.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SimpleHeaderTypeRouter implements MessageListener {

	private ConnectionFactory factory;
	Destination datatypeChannel1;
	Destination datatypeChannel2;
	Destination invalidMessageChannel;
	Destination inboundChannel;
	
	public SimpleHeaderTypeRouter() throws NamingException {
		init();
	}

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		this.datatypeChannel1 = (Destination) ctx.lookup("type-1-channel");
		this.datatypeChannel2 = (Destination) ctx.lookup("type-2-channel");
		this.invalidMessageChannel = (Destination) ctx
				.lookup("invalid-message-channel");
		this.inboundChannel = (Topic) ctx.lookup("inbound-ps-channel");
	}
	

	@Override
	public void onMessage(Message message) {
		try {
			String type = message.getStringProperty("data-type");
			System.out.println("Inbound channel: Received message: " + type);
			Destination destination;

			if (type != null && type.equals("type-1")) { // route to
													     // type-1-channel
				destination = datatypeChannel1;
			} else if (type != null && type.equals("type-2")) { // route to
																// type-2-channel
				destination = datatypeChannel2;
			} else { // route to invalid-message-channel
				destination = invalidMessageChannel;
			}
			routeMessage(destination, message);

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void routeMessage(Destination destination, Message message)
			throws JMSException {
		try (Connection con = factory.createConnection()) {
			con.start();
			Session session = con
					.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(destination);
			producer.send(message);
		}
	}



}
