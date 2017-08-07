package br.com.argonavis.eipcourse.messaging.jms.translator;

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

public class HtmlReceiver {

	public static void main(String[] args) throws Exception {

		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		final Session receiverSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		final Queue outboundQueue = (Queue) ctx.lookup(TransformerExample.OUTBOUND_CHANNEL);

		// 3) Receiver
		MessageConsumer receiver = receiverSession
				.createConsumer(outboundQueue);
		receiver.setMessageListener(new MessageListener() {
			public void onMessage(Message message) {
				TextMessage finalMessage = (TextMessage) message;
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
