package br.com.argonavis.eipcourse.channel.pubsub.jms;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.naming.NamingException;

public class TopicExample {

	public static void main(String[] args) throws NamingException {
		Executor thread = Executors.newFixedThreadPool(2);
		
		System.out.println("1) Setting up two subscribers...");
		
		MessageSubscriber subscriber1 = new MessageSubscriber("Subscriber 1", 500);
		subscriber1.init();
		subscriber1.run(thread);
		
		MessageSubscriber subscriber2 = new MessageSubscriber("Subscriber 2", 1000);
		subscriber2.init();
		subscriber2.run(thread);
		
		System.out.println("2) Publishing 10 messages...");
		
		MessagePublisher publisher = new MessagePublisher();
		publisher.init();

		publisher.sendJms11();
		
		System.out.println("Press ^C to stop application.");
	}

}
