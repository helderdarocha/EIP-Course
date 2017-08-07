package br.com.argonavis.eipcourse.messaging.jms.translator;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class TransformerExample {
	
	public static final String INBOUND_CHANNEL  = "inbound-channel";
	public static final String OUTBOUND_CHANNEL = "outbound-channel";

	public static void main(String[] args) throws NamingException, JMSException {
        Context ctx = new InitialContext();
		
		ConnectionFactory factory = 
				(ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination input = (Destination) ctx.lookup(INBOUND_CHANNEL);
		Connection con = factory.createConnection();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(input);
		consumer.setMessageListener(new TweetTransformer());
		con.start();
		
		System.out.println("Transformer is running.");
	}

}
