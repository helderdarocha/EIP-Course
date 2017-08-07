package br.com.argonavis.eipcourse.messaging.jms.pipesfilters;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class FilterReceiverExample {
	
	public static final String INBOUND_CHANNEL  = "inbound-channel";
	public static final String OUTBOUND_CHANNEL = "outbound-channel";

	public static void main(String[] args) throws Exception {

		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		final Session transformerSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Session receiverSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		final Queue inboundQueue = (Queue) ctx.lookup(INBOUND_CHANNEL);
		final Queue outboundQueue = (Queue) ctx.lookup(OUTBOUND_CHANNEL);
		
		// 2) Filter
		MessageConsumer transformerIn = transformerSession
				.createConsumer(inboundQueue);
		transformerIn.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				try {
					TextMessage inMessage = (TextMessage) message;
					String transformedContents = inMessage.getText()
							.toUpperCase();
					TextMessage outMessage = transformerSession
							.createTextMessage(transformedContents);
					MessageProducer transformerOut = transformerSession
							.createProducer(outboundQueue);
					transformerOut.send(outMessage);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});

		// 3) Receiver
		MessageConsumer receiver = receiverSession
				.createConsumer(outboundQueue);
		receiver.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				TextMessage finalMessage = (TextMessage) message;
				System.out.println("Received message: " + finalMessage);
				try {
					System.out.println("Contents: " + finalMessage.getText());
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});

		con.start();
	}
}
