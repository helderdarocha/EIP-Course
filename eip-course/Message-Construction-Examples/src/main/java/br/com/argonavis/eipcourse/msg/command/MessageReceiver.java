package br.com.argonavis.eipcourse.msg.command;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
	
	public MessageReceiver(String name, long delay) {
		this.name = name;
		this.delay = delay;
	}

	public void init() throws NamingException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		this.queue = (Queue)ctx.lookup("inbound-queue");
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
		Executor thread = Executors.newFixedThreadPool(2);
		
		System.out.println("Waiting for messages... (^C to cancel)");
		
		MessageReceiver receiver1 = new MessageReceiver("Receiver 1", 500);
		receiver1.init();
		receiver1.run(thread);
		
		MessageReceiver receiver2 = new MessageReceiver("Receiver 2", 1000);
		receiver2.init();
		receiver2.run(thread);
		
	}

}
