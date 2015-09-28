package br.com.argonavis.eipcourse.channel.p2p.jms;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.naming.NamingException;

public class QueueExample {

	public static void main(String[] args) throws NamingException {
		Executor thread = Executors.newFixedThreadPool(2);
		
        System.out.println("1) Setting up two receivers...");
		
		MessageReceiver receiver1 = new MessageReceiver("Receiver 1", 500);
		receiver1.init();
		receiver1.run(thread);
		
		MessageReceiver receiver2 = new MessageReceiver("Receiver 2", 1000);
		receiver2.init();
		receiver2.run(thread);
		
		System.out.println("2) Sending 10 messages...");
		
		MessageSender sender = new MessageSender();
		sender.init();

		sender.sendJms11();
		
		System.out.println("Press ^C to stop application.");

	}
}
