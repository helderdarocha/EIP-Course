package br.com.argonavis.eipcourse.channel.p2p.jms;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageReceiver {
	
	private ConnectionFactory factory;
	private Destination queue;
	
	private String name;
	private long delay;
	
	public MessageReceiver(String name, long delay) {
		this.name = name;
		this.delay = delay;
	}

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		this.queue = (Destination)ctx.lookup("simple-p2p-channel");
	}
	
	public void receiveJms11() {
		try (Connection con = factory.createConnection()) {
			con.start();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = session.createConsumer(queue);
			
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
		Executor thread = Executors.newFixedThreadPool(4);
		
		System.out.println("Waiting for messages... (^C to cancel)");
		
		MessageReceiver receiver1 = new MessageReceiver("Receiver 1", 1500);
		receiver1.init();
		receiver1.run(thread);
		
		MessageReceiver receiver2 = new MessageReceiver("Receiver 2", 1000);
		receiver2.init();
		receiver2.run(thread);
		
		MessageReceiver receiver3 = new MessageReceiver("Receiver 3", 200);
		receiver3.init();
		receiver3.run(thread);
		
		MessageReceiver receiver4 = new MessageReceiver("Receiver 4", 500);
		receiver4.init();
		receiver4.run(thread);
		
	}

}
