package br.com.argonavis.eipcourse.translator.envelope;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class JMSChannelBridge implements MessageListener {
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private PayloadProcessor payloadProcessor;
	private PropertiesProcessor propertiesProcessor;

	public JMSChannelBridge(Connection con, Destination in, Destination out, PayloadProcessor payloadProcessor, PropertiesProcessor propertiesProcessor)
			throws JMSException {
		System.out.println("Creating bridge from " + in + " to " + out);
		this.payloadProcessor = payloadProcessor;
		this.propertiesProcessor = propertiesProcessor;
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(out);
		consumer = session.createConsumer(in);
		consumer.setMessageListener(this);
		con.start();
	}
	
	public JMSChannelBridge(Connection con, Destination in, Destination out, PayloadProcessor payloadProcessor) throws JMSException {
    	this(con, in, out, payloadProcessor, new PropertiesProcessor());
	}
	
	public JMSChannelBridge(Connection con, Destination in, Destination out, PropertiesProcessor propertiesProcessor) throws JMSException {
    	this(con, in, out, new PayloadProcessor(), propertiesProcessor);
	}
	
    public JMSChannelBridge(Connection con, Destination in, Destination out) throws JMSException {
    	this(con, in, out, new PayloadProcessor(), new PropertiesProcessor());
	}

	@Override
	public void onMessage(Message incomingMessage) {
		try {
			// Receive incoming message and extract data
			Object payload = JMSUtils.extractPayload(incomingMessage);
			Map<String, Object> properties = JMSUtils.getMessageProperties(incomingMessage);
			
			// Do something with the data
			Object newPayload = payloadProcessor.process(payload);
			Map<String, Object> newProperties = propertiesProcessor.process(properties);
			

			// Publish new outgoing message
			Message outgoingMessage = JMSUtils.createMessageWithPayload(session, newPayload);
			JMSUtils.setMessageProperties(outgoingMessage, newProperties);
			producer.send(outgoingMessage);
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
