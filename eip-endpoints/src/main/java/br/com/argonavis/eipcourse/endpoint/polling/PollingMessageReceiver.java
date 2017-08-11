package br.com.argonavis.eipcourse.endpoint.polling;

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

public class PollingMessageReceiver {
	
	private Session session;
	private MessageConsumer consumer;
	private String name;
	private long delay;
	
	public PollingMessageReceiver(Connection con, Destination queue, String name, long delay) throws JMSException {
		this.name = name;
		this.delay = delay;
		
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(queue);
		con.start();
	}
	
	public void receive() throws JMSException {
			while(true) {
			    TextMessage message = (TextMessage)consumer.receive();
			    System.out.println(name + " received: " + message.getText());
			    try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {}
			}
	}
	
	public void run(Executor thread) {
		thread.execute(new Runnable() {
			public void run() {
				try {
					receive();
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static void main(String[] args) throws NamingException, JMSException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		Destination from = (Destination)ctx.lookup("inbound");
		Connection con = factory.createConnection();
		
		Executor thread = Executors.newFixedThreadPool(2);
		System.out.println("Waiting for messages... (^C to cancel)");
		
		PollingMessageReceiver receiver1 = new PollingMessageReceiver(con, from, "Receiver 1", 10000);
		receiver1.run(thread);
		
		PollingMessageReceiver receiver2 = new PollingMessageReceiver(con, from, "Receiver 2", 20000);
		receiver2.run(thread);
	}
}
