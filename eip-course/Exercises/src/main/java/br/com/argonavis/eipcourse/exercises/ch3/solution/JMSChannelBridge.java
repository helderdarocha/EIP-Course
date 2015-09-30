package br.com.argonavis.eipcourse.exercises.ch3.solution;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import br.com.argonavis.eipcourse.exercises.utils.JMSUtils;

public class JMSChannelBridge implements MessageListener {
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private PayloadProcessor processor;

	public JMSChannelBridge(Connection con, Destination in, Destination out, PayloadProcessor processor)
			throws JMSException {
		System.out.println("Creating bridge from " + in + " to " + out);
		this.processor = processor;
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(out);
		consumer = session.createConsumer(in);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message incomingMessage) {
		try {
			// Receive incoming message and extract data
			Object payload = JMSUtils.extractPayload(incomingMessage);
			Map<String, Object> properties = JMSUtils.getMessageProperties(incomingMessage);
			
			// Do something with the data
			Object newPayload = processor.process(payload);

			// Publish new outgoing message
			Message outgoingMessage = JMSUtils.createMessageWithPayload(session, newPayload);
			JMSUtils.setMessageProperties(outgoingMessage, properties);
			producer.send(outgoingMessage);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
