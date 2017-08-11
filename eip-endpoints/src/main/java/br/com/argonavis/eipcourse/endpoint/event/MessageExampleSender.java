package br.com.argonavis.eipcourse.endpoint.event;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessageExampleSender {
	
	public static final String DESTINATION = "inbound";
	
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination destination = (Queue) ctx.lookup(DESTINATION);

		Connection con = factory.createConnection();
		con.start();
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(destination);

		TextMessage message = session.createTextMessage("Hello World with properties!");
		message.setStringProperty("category", "greeting");
		message.setStringProperty("contentType", "text/plain");

		producer.send(message);
		System.out.println("Message sent to " + DESTINATION);

	}
}
