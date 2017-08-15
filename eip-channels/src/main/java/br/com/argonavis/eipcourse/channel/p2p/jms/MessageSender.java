package br.com.argonavis.eipcourse.channel.p2p.jms;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageSender {
	
	private ConnectionFactory factory;
	private Destination queue;

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		this.queue = (Destination)ctx.lookup("simple-p2p-channel");
	}
	
	public void sendJms11() {
		try (Connection con = factory.createConnection()) {
			con.start();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			
			System.out.println("Provider: " + con.getMetaData().getJMSProviderName() + " " 
                                            + con.getMetaData().getProviderVersion());
			
			for(int i = 0; i < 10; i++) {
				System.out.println("Sending message " + (i+1));
				TextMessage message = session.createTextMessage("Message number " + (i+1) + " sent " +  new Date());
				producer.send(message);
			}
			
			System.out.println("All messages sent!");
		} catch(JMSException e){
			System.err.println("JMS Exception: " + e);
		}
	}

	public static void main(String[] args) throws NamingException {
		MessageSender sender = new MessageSender();
		sender.init();

		sender.sendJms11();
		
	}

}
