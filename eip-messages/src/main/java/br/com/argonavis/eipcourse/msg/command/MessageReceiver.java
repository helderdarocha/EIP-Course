package br.com.argonavis.eipcourse.msg.command;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageReceiver {

	private ConnectionFactory factory;
	private Queue queue;
	private String channel;
	private MessageListener listener;

	private String name;

	public MessageReceiver(String name, MessageListener listener, String channel) {
		this.name = name;
		this.listener = listener;
		this.channel = channel;
	}

	public void init() throws NamingException, JMSException {
		Context ctx = new InitialContext();
		this.factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		this.queue = (Queue) ctx.lookup(channel);

		Connection con = factory.createConnection();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(queue);
		consumer.setMessageListener(listener);
		con.start();
		System.out.println(name + " esperando chamadas.");
	}

	public static void main(String[] args) throws Exception {

		MessageReceiver receiver1 = new MessageReceiver("Receiver 1", new CommandMessageListener(), "command-queue");
		receiver1.init();

		MessageReceiver receiver2 = new MessageReceiver("Receiver 2", new XMLCommandMessageListener(), "xml-command-queue");
		receiver2.init();

	}

}
