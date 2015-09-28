package br.com.argonavis.eipcourse.msg.reqres;

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
import javax.naming.NamingException;

/**
 * This example is adapted from the book Enterprise Integration Patterns,
 * chapter 6.
 */
public class RespondingListener implements MessageListener {

	private Session session;
	private MessageProducer replyProducer;
	private MessageConsumer requestConsumer;
	
	private ExampleProcessor processor;

	protected void init(Connection con, Destination requestQueue) throws NamingException, JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		requestConsumer = session.createConsumer(requestQueue);
		requestConsumer.setMessageListener(this);
		processor = new ExampleProcessor();
	}

	public void onMessage(Message message) {
		try {
			if((message instanceof TextMessage) && (message.getJMSReplyTo() != null)) {
				TextMessage requestMessage = (TextMessage) message;
				System.out.println("Received request.");
				Utils.printMessage(requestMessage);
				
				String result = processor.process(requestMessage);
				
				Destination replyDestination = requestMessage.getJMSReplyTo();
				replyProducer = session.createProducer(replyDestination);
				
				TextMessage replyMessage = session.createTextMessage();
				replyMessage.setText(result);
				replyMessage.setJMSCorrelationID(requestMessage.getJMSMessageID());
				replyProducer.send(replyMessage);
				
				System.out.println("Reply was sent.");
				Utils.printMessage(replyMessage);
			} else {
				System.out.println("Bad message.");
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		Destination requestQueue = (Destination) ctx.lookup("request-queue");

		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Connection con = factory.createConnection();

		RespondingListener replier = new RespondingListener();
		replier.init(con, requestQueue);
		
		con.start();
		System.out.println("Replier started.");
		
		Thread.sleep(30000);
		// close the connections
		System.out.println("Done.");
		con.close();
	}

}
