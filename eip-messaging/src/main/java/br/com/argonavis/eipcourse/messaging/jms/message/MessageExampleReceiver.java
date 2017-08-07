package br.com.argonavis.eipcourse.messaging.jms.message;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessageExampleReceiver {

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination destination = (Queue) ctx.lookup(MessageExampleSender.DESTINATION);

		Connection con = factory.createConnection();
		con.start();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(destination);
		
		consumer.setMessageListener(new AsyncConsumer());

	}

}
