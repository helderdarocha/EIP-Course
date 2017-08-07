package br.com.argonavis.eipcourse.router.list;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RecipientList implements MessageListener {

	private Session session;
	private Destination imageChannel;
	private Destination textChannel;
	private Destination xmlChannel;
	private Destination allChannel;
	private Destination inboundChannel;

	public void init(Connection con) throws NamingException, JMSException {
		Context ctx = new InitialContext();
		this.imageChannel = (Destination) ctx.lookup("image-channel");
		this.textChannel = (Destination) ctx.lookup("text-channel");
		this.xmlChannel = (Destination) ctx.lookup("xml-channel");
		this.allChannel = (Destination) ctx.lookup("all-channel");
		this.inboundChannel = (Destination) ctx.lookup("inbound-channel");
		
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(inboundChannel);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			String filename = message.getStringProperty("Name");
			String type = message.getStringProperty("Type");
			System.out.println("Received message: " + filename);

			if (type != null && type.equals("png")) { 
				routeMessage(imageChannel, message);
				System.out.println("Routing " + filename + " to dt-queue-1");
			} 
			if (type != null && type.equals("txt") || type.equals("xml")) { 
				routeMessage(textChannel, message);
				System.out.println("Routing " + filename + " to dt-queue-2");
			} 
            if (type != null && type.equals("xml")) { 
            	routeMessage(xmlChannel, message);
            	System.out.println("Routing " + filename + " to dt-queue-3");
			} 
            routeMessage(allChannel, message);
            System.out.println("Routing " + filename + " to all-queue");

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void routeMessage(Destination destination, Message message) throws JMSException {
			MessageProducer producer = session.createProducer(destination);
			producer.send(message);
	}
	
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		
		RecipientList router = new RecipientList();
		router.init(con);
		
		System.out.println("Waiting 60 seconds for messages...");
        Thread.sleep(60000); // Will wait one minute for files
        con.close();
	}

}
