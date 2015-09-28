package br.com.argonavis.eipcourse.channel.gd.jms;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ExpiredMessageSender {
	
	private ConnectionFactory factory;
	private Queue queue;

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		this.queue = (Queue)ctx.lookup("simple-p2p-channel");
	}
	
	public void sendJms11() {
		try (Connection con = factory.createConnection()) {
			con.start();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			producer.setDeliveryMode(javax.jms.DeliveryMode.PERSISTENT);
			producer.setTimeToLive(500); // message lives 1/2 second
			
			System.out.println("Provider: " + con.getMetaData().getJMSProviderName() + " " 
                                            + con.getMetaData().getProviderVersion());
			
			for(int i = 0; i < 10; i++) {
				System.out.println("Sending message " + (i+1));
				TextMessage message = session.createTextMessage("Message number " + (i+1) + " sent " +  new Date());
				Thread.sleep(1001);
				producer.send(message);
			}
			
			System.out.println("All messages sent!");
		} catch(JMSException e){
			System.err.println("JMS Exception: " + e);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws NamingException {
		ExpiredMessageSender sender = new ExpiredMessageSender();
		sender.init();

		sender.sendJms11();
		
	}

}
