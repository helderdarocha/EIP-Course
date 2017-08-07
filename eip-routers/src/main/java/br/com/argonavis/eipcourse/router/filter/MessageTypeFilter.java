package br.com.argonavis.eipcourse.router.filter;

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

public class MessageTypeFilter implements MessageListener {

	MessageProducer producer;
	String messageType;
	
	public MessageTypeFilter(String messageType) {
		this.messageType = messageType;
	}

	public void init(Connection con, Destination inTopic, Destination outQueue) throws NamingException, JMSException {
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(inTopic);
		producer = session.createProducer(outQueue);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			String typeProperty     = message.getStringProperty("Type");
			String filenameProperty = message.getStringProperty("Name");
			System.out.println(messageType.toUpperCase() + " Filter received: " + filenameProperty);

			if (typeProperty != null && typeProperty.equals(messageType)) { 
				producer.send(message);
				System.out.println(messageType.toUpperCase() + " Filter selected : "  + filenameProperty);
			} else {
				System.out.println(messageType.toUpperCase() + " Filter discarded: " + filenameProperty);
			}
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination inTopic  = (Destination) ctx.lookup("files-topic");
		Connection con = factory.createConnection();
		
		Destination imageChannel = (Destination) ctx.lookup("image-channel");
		Destination textChannel  = (Destination) ctx.lookup("text-channel");
		
		MessageTypeFilter imageFilter = new MessageTypeFilter("png");
		imageFilter.init(con, inTopic, imageChannel);
		
		MessageTypeFilter textFilter = new MessageTypeFilter("txt");
		textFilter.init(con, inTopic, textChannel);
		
		MessageTypeFilter xmlFilter = new MessageTypeFilter("xml");
		xmlFilter.init(con, inTopic, textChannel);
		
		System.out.println("Waiting 60 seconds for messages...");
		
        Thread.sleep(60000); // Will wait one minute for files
        con.close();
	}
}
