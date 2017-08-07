package br.com.argonavis.eipcourse.messaging.jms.pipesfilters;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * 
 * Pipes and Filters - Sender
 *
 */
public class SenderExample {
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Queue queue = (Queue) ctx.lookup(FilterReceiverExample.INBOUND_CHANNEL);
		
		Connection con = factory.createConnection();
		con.start();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer sender = session.createProducer(queue);

		TextMessage message = session.createTextMessage("Hello World!");
		sender.send(message);
		
		con.close();
	}
}
