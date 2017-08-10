package br.com.argonavis.eipcourse.router.cbr;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ContentBasedRouter implements MessageListener {

	private ConnectionFactory factory;
	Destination imageChannel;
	Destination textChannel;
	Destination xmlChannel;
	Destination invalidMessageChannel;
	Destination inboundChannel;

	public Connection init() throws NamingException, JMSException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		this.imageChannel = (Destination) ctx.lookup("image-channel");
		this.textChannel = (Destination) ctx.lookup("text-channel");
		this.xmlChannel = (Destination) ctx.lookup("xml-channel");
		this.invalidMessageChannel = (Destination) ctx
				.lookup("invalid-message-channel");
		this.inboundChannel = (Destination) ctx.lookup("inbound-channel");
		
		Connection con = factory.createConnection();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(inboundChannel);
		consumer.setMessageListener(this);
		con.start();
		return con;
	}

	@Override
	public void onMessage(Message message) {
		try {
			String type = message.getStringProperty("Type");
			System.out.println("Inbound channel: Received message: " + type);
			Destination destination;

			if (type != null && (type.equals("png"))) { 
				destination = imageChannel;
			} else if (type != null && type.equals("txt")) { 
				destination = textChannel;
			} else if (type != null && type.equals("xml")) { 
				destination = xmlChannel;
			} else { 
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
	
	public static void main(String[] args) throws Exception {
		ContentBasedRouter router = new ContentBasedRouter();
		Connection con = router.init();
		
        Thread.sleep(60000); // Will wait one minute for files
        con.close();

	}

}
