package eipcourse.exercises.jms.basic.route;

import java.util.Random;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSSender {

	public static void main(String[] args) throws NamingException, JMSException {

		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination caixaDeEntrada = (Destination) ctx.lookup("entrada");

		try (Connection con = factory.createConnection();
				Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);) {
			
			MessageProducer producer = session.createProducer(caixaDeEntrada);
			
			TextMessage message = session.createTextMessage("Hello World!");
			message.setStringProperty("content-type", "application/greeting");
			producer.send(message);
			System.out.println("Enviada: " + message.getStringProperty("content-type"));
			
			for (int i = 0; i < 10; i++) {
				Random rand = new Random();
				int sorte = rand.nextInt(1000) + 1;
				int index = rand.nextInt(2);

				TextMessage m = session.createTextMessage();

				if (index == 0) {
					m.setStringProperty("content-type", "text/plain");
					m.setText("" + sorte);
				} else {
					m.setStringProperty("content-type", "text/xml");
					m.setText("<sorte>" + sorte + "</sorte>");
				}
				producer.send(m);
				System.out.println("Enviada: " + m.getStringProperty("content-type"));
			}
		}

	}

}
