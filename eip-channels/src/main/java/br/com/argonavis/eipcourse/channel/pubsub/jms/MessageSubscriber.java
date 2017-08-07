package br.com.argonavis.eipcourse.channel.pubsub.jms;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageSubscriber {
	
	private ConnectionFactory factory;
	private Topic topic;
	
	private String name;
	private long delay;
	
	public MessageSubscriber(String name, long delay) {
		this.name = name;
		this.delay = delay;
	}

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		this.topic = (Topic)ctx.lookup("simple-ps-channel");
	}
	
	public void receiveJms11() {
		try (Connection con = factory.createConnection()) {
			con.start();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(topic);
			
			while(true) {
			    TextMessage message = (TextMessage)consumer.receive();
			    System.out.println(name + " received: " + message.getText());
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

	public static void main(String[] args) throws NamingException {
		Executor thread = Executors.newFixedThreadPool(2);
		
		System.out.println("Waiting for messages... (^C to cancel)");
		
		MessageSubscriber subscriber1 = new MessageSubscriber("Subscriber 1", 500);
		subscriber1.init();
		subscriber1.run(thread);
		
		MessageSubscriber subscriber2 = new MessageSubscriber("Subscriber 2", 1000);
		subscriber2.init();
		subscriber2.run(thread);

	}
	
}
