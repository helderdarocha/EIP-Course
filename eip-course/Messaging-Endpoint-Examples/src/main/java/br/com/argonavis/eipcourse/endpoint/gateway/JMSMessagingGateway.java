package br.com.argonavis.eipcourse.endpoint.gateway;

import java.util.Enumeration;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSMessagingGateway implements SimpleMessagingGateway {

	private Connection con;
	private Context jndiContext;
	MessageConsumer consumer;

	public JMSMessagingGateway() throws MessagingException {
		try {
			jndiContext = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
			con = factory.createConnection();
		} catch (NamingException | JMSException e) {
			throw new MessagingException(e);
		}
	}
	
	public void closeConnection() {
		try {
			con.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	private Destination getDestination(String jndiName) throws MessagingException {
		try {
			return (Destination) jndiContext.lookup(jndiName);
		} catch (NamingException e) {
			throw new MessagingException(e);
		}
	}

	private Session createSession() throws MessagingException {
		try {
			return con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			throw new MessagingException(e);
		}
	}

	private SimpleMessage createSimpleMessage(TextMessage jmsMessage) throws JMSException {
		SimpleMessage m = new SimpleMessage(jmsMessage.getText());

		Enumeration<?> properties = jmsMessage.getPropertyNames();
		while (properties.hasMoreElements()) {
			String key = (String) properties.nextElement();
			m.setHeader(key, jmsMessage.getStringProperty(key));
		}
		return m;
	}

	@Override
	public void send(SimpleChannel c, SimpleMessage m)
			throws MessagingException {
		Session session = createSession();
		Destination destination = getDestination(c.getName());
		try {
			MessageProducer producer = session.createProducer(destination);
			TextMessage jmsMessage = session.createTextMessage(m.getPayload());
			for (Map.Entry<String, String> header : m.getHeaders().entrySet()) {
				jmsMessage
						.setStringProperty(header.getKey(), header.getValue());
			}
			producer.send(jmsMessage);
		} catch (JMSException e) {
			throw new MessagingException(e);
		}
	}

	@Override
	public SimpleMessage receive(SimpleChannel c) throws MessagingException {
		Session session = createSession();
		Destination destination = getDestination(c.getName());
		try {
			consumer = session.createConsumer(destination);
			TextMessage jmsMessage = (TextMessage) consumer.receive();
			return createSimpleMessage(jmsMessage);
		} catch (JMSException e) {
			throw new MessagingException(e);
		}
	}

	@Override
	public void register(MessagingEventHandler handler, SimpleChannel c)
			throws MessagingException {
		Session session = createSession();
		Destination destination = getDestination(c.getName());
		try {
			consumer = session.createConsumer(destination);
			consumer.setMessageListener(new MessageListener() {
				@Override
				public void onMessage(Message jmsMessage) {
					try {
						SimpleMessage m = createSimpleMessage((TextMessage) jmsMessage);
						handler.process(m);
					} catch (JMSException e) {
						e.printStackTrace();
					} 
				}
			});
			con.start();
		} catch (JMSException e) {
			throw new MessagingException(e);
		}
	}
}
