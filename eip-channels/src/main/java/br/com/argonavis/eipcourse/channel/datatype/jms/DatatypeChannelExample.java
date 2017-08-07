package br.com.argonavis.eipcourse.channel.datatype.jms;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.naming.NamingException;

public class DatatypeChannelExample {

	public static void main(String[] args) throws NamingException, InterruptedException {

		// 1) Publish messages to inbound channel & initialize router
		SimpleHeaderTypeRouter router = new SimpleHeaderTypeRouter();
		MessagePublisher publisher = new MessagePublisher(router);
		publisher.init();
		publisher.sendJms11();

		// 2) Read messages per channel

		Executor thread = Executors.newFixedThreadPool(3);

		System.out.println("Waiting for messages...");

		MessageReceiver receiver1 = new MessageReceiver("Receiver 1", 500,
				"type-1-channel");
		receiver1.init();
		receiver1.run(thread);

		MessageReceiver receiver2 = new MessageReceiver("Receiver 2", 500,
				"type-2-channel");
		receiver2.init();
		receiver2.run(thread);

		MessageReceiver receiver3 = new MessageReceiver("Invalid Messages",
				500, "invalid-message-channel");
		receiver3.init();
		receiver3.run(thread);
		
		Thread.currentThread().join();
		
		System.out.println("Press ^C to stop application.");
	}

}
