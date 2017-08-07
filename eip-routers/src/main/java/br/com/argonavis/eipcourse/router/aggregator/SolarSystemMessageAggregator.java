package br.com.argonavis.eipcourse.router.aggregator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

import br.com.argonavis.eipcourse.router.splitter.SolarSystemMessageSplitter;

public class SolarSystemMessageAggregator implements MessageListener {
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;
	
	Map<String, Set<TextMessage>> messageSets = new HashMap<>();;

	public SolarSystemMessageAggregator(Connection con, Destination in, Destination out) throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(in);
		producer = session.createProducer(out);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			String sequenceID = message.getJMSCorrelationID();
			int size = message.getIntProperty("SequenceSize");
			String type = message.getStringProperty("Type");

			if(type != null && type.equals("Solar System Fragment")) {
				System.out.println("Processing fragment.");
				Set<TextMessage> messageSet = null;
				
				if (messageSets.containsKey(sequenceID)) {
					System.out.println("Adding to " + sequenceID);
					messageSet = messageSets.get(sequenceID);
					messageSet.add((TextMessage)message);
				} else {
					System.out.println("Creating new sequence " + sequenceID);
				    messageSet = new HashSet<>();
				    messageSet.add((TextMessage)message);
				    messageSets.put(sequenceID, messageSet);
				}
				
				if(messageSet.size() == size && messageSets.containsKey(sequenceID)) { // all parts were collected
					System.out.println("All parts were collected. Will assemble now " + sequenceID);
					TextMessage newMessage = reassemble(messageSet);
					messageSets.remove(sequenceID);
					newMessage.setStringProperty("Type", "SolarSystem"); 
					producer.send(newMessage);
				}
			} else {
				producer.send(message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public TextMessage reassemble(Set<TextMessage> messages) throws JMSException {
		String[] parts = new String[messages.size()];
		for(TextMessage message : messages) {
			String fragment = message.getText();
			int index = message.getIntProperty("SequencePosition") - 1;
			parts[index] = fragment;
		}
		String newPayload = "<joined>" + String.join("\n", parts) + "</joined>";
		
		return session.createTextMessage(newPayload);
	}
	
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			Destination from = (Destination) ctx.lookup("b-channel");
			Destination to   = (Destination) ctx.lookup("c-channel");

			new SolarSystemMessageAggregator(con, from, to);

			System.out.println("Receiving messages for 60 seconds...");
			Thread.sleep(60000);
			System.out.println("Done.");
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (JMSException e) {
				}
			}
		}
	}
}
