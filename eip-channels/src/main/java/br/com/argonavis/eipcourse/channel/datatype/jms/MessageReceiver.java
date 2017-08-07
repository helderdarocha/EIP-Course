package br.com.argonavis.eipcourse.channel.datatype.jms;

import java.util.concurrent.Executor;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageReceiver {
	
	private ConnectionFactory factory;
	private Queue queue;
	
	private String name;
	private long delay;
	private String channel;
	
	public MessageReceiver(String name, long delay, String channel) {
		this.name = name;
		this.delay = delay;
		this.channel = channel;
	}

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		this.queue = (Queue)ctx.lookup(channel);
		
		System.out.println("Channel: " + queue);
	}
	
	public void receiveJms11() { 
		try (Connection con = factory.createConnection()) {
			con.start();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(queue);
			
			while(true) {
			    TextMessage message = (TextMessage)consumer.receive();
			    System.out.println(name + " received: " + message.getText() + " of type " + message.getStringProperty("data-type"));
			    Thread.sleep(delay);
			}
		} catch(JMSException e){
			System.err.println("JMS Exception: " + e);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void run(Executor thread) {
		thread.execute(new Runnable() {
			public void run() {
				receiveJms11();
			}
		});
	}

}
