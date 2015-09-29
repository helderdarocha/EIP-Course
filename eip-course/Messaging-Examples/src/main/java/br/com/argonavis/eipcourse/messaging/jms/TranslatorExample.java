package br.com.argonavis.eipcourse.messaging.jms;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
import javax.naming.NamingException;

public class TranslatorExample {

	public static void main(String[] args) throws Exception {

		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		final Session transformerSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final Session receiverSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		final Queue inboundQueue = (Queue) ctx.lookup("inbound-channel");
		final Queue outboundQueue = (Queue) ctx.lookup("outbound-channel");
		
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

		MessageConsumer receiver = receiverSession
				.createConsumer(outboundQueue);
		receiver.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				TextMessage finalMessage = (TextMessage) message;
				System.out.println("Received message: " + finalMessage);
			}
		});

		con.start();
	}
}
