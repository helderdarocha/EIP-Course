package br.com.argonavis.eipcourse.mgmt.wiretap;

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

public class WireTap implements MessageListener {

	private Session session;
	private Destination outChannel;
	private Destination wireTapChannel;
	
	public WireTap(Connection con, Destination inChannel, Destination outChannel, Destination wireTap) throws JMSException {
		this.outChannel = outChannel;
		this.wireTapChannel = wireTap;
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(inChannel);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			routeMessage(outChannel, message);
			routeMessage(wireTapChannel, message);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void routeMessage(Destination destination, Message message) throws JMSException {
			MessageProducer producer = session.createProducer(destination);
			producer.send(message);
	}

}
