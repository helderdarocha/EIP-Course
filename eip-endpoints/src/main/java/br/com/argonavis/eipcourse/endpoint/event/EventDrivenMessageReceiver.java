package br.com.argonavis.eipcourse.endpoint.event;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class EventDrivenMessageReceiver implements MessageListener {

	private Session session;
	private MessageConsumer consumer;
	private String name;

	public EventDrivenMessageReceiver(Connection con, Destination queue,
			String name) throws JMSException {
		this.name = name;
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(queue);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message msg) {
		try {
			TextMessage message = (TextMessage) msg;
			System.out.println(name + " received: " + message.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Destination from = (Destination) ctx.lookup("inbound");
		Connection con = factory.createConnection();

		System.out.println("Waiting for messages for 60 seconds... (^C to cancel)");

		new EventDrivenMessageReceiver(con, from, "Receiver 1");
		new EventDrivenMessageReceiver(con, from, "Receiver 2");

		Thread.sleep(60000);

	}
}
