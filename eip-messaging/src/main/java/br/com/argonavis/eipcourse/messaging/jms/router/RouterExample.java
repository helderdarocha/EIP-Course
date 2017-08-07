package br.com.argonavis.eipcourse.messaging.jms.router;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RouterExample {

	public static void main(String[] args) throws JMSException, NamingException {
		Context ctx = new InitialContext();
		
		ConnectionFactory factory = 
				(ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination earth = (Destination) ctx.lookup("mixed-queue");
		Connection con = factory.createConnection();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(earth);
		consumer.setMessageListener(new GoodEvilRouter(con));
		con.start();
		
		System.out.println("Router is running.");
	}

}
