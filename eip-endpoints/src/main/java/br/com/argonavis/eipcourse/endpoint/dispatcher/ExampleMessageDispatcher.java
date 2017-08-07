package br.com.argonavis.eipcourse.endpoint.dispatcher;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class ExampleMessageDispatcher implements MessageListener {

	private Session session;
	private MessageConsumer consumer;
	private MessageProducer invalidChannelProducer;
	
	public ExampleMessageDispatcher(Connection con, Destination queue,
			Destination invalidChannel) throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(queue);
		invalidChannelProducer = session.createProducer(invalidChannel);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			String type     = message.getStringProperty("Type");
			String filename = message.getStringProperty("Filename");
			System.out.println("Received message with file " + filename);
			
			MessageProcessor processor;
			Executor thread = Executors.newFixedThreadPool(3);
			
			if (type != null && type.equals("png")) { 
				processor = new ImageProcessor(message);
			} else if (type != null && type.equals("txt")) { 
				processor = new TextProcessor(message);
			} else if (type != null && type.equals("xml")) { 
				processor = new XMLProcessor(message);
			} else { 
				processor = null;
			}
			
			if(processor == null) { // cannot process - send to invalid channel
				invalidChannelProducer.send(message);
			} else {
				processor.run(thread);
			}
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Destination from      = (Destination) ctx.lookup("inbound");
		Destination invalidTo = (Destination) ctx.lookup("invalid");
		Connection con = factory.createConnection();

		System.out.println("Waiting for messages for 60 seconds... (^C to cancel)");

		new ExampleMessageDispatcher(con, from, invalidTo);

		Thread.sleep(60000);
	}
}
