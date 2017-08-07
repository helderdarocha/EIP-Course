package br.com.argonavis.eipcourse.messaging.jms.router;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GoodEvilRouter implements MessageListener {

	Destination heaven;
	Destination hell;

	private Connection con;

	public GoodEvilRouter(Connection con) throws NamingException, JMSException {
		this.con = con;

		Context ctx = new InitialContext();
		this.heaven = (Destination) ctx.lookup("good-queue");
		this.hell = (Destination) ctx.lookup("evil-queue");
	}

	@Override
	public void onMessage(Message message) {
		try {
			String nature = message.getStringProperty("nature");

			Destination destination = null;

			if (nature != null && nature.equals("good")) {
				destination = heaven;
			} else if (nature != null && nature.equals("evil")) {
				destination = hell;
			}
			if(destination != null) {
				routeMessage(destination, message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void routeMessage(Destination destination, Message message) throws JMSException {
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(destination);
		producer.send(message);
	}
}
