package br.com.argonavis.eipcourse.exercises.ch5;

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
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessageSequenceReceiver implements MessageListener {

	// sequenceID, List of blocks
	private Map<String, Set<Message>> sequences;

	public void init(Connection con, Destination queue) throws JMSException {
		sequences = new HashMap<>();

		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer sequenceConsumer = session.createConsumer(queue);
		sequenceConsumer.setMessageListener(this);
		con.start();
	}

	public void onMessage(Message message) {
		try {
			String sequenceID = message.getJMSCorrelationID();

			if (sequences.containsKey(sequenceID)) {
				Set<Message> messages = sequences.get(sequenceID);
				messages.add(message);
			} else {
				Set<Message> messages = new HashSet<>();
				messages.add(message);
				sequences.put(sequenceID, messages);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void verifyAndPrint() throws JMSException {
		for (Set<Message> messages : sequences.values()) {
			Message testMsg = messages.iterator().next();
			int size = testMsg.getIntProperty("Size");
			String seqID = testMsg.getJMSCorrelationID();
			
			if (size == messages.size()) { // all messages arrived (if no dups)
				System.out.println("\nSequence: " + seqID);
				for (Message message : messages) {
					int position = message.getIntProperty("Position");
					String text = ((TextMessage)message).getText();
					System.out.println("["+position + "] >" + text + "<");
				}
			} else {
				// send all messages to invalid message queue
				System.out.println("Incomplete sequence: " + seqID + ", size = " + messages.size());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		Destination sequenceQueue = (Destination) ctx.lookup("sequence-queue");

		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Connection con = factory.createConnection();

		MessageSequenceReceiver receiver = new MessageSequenceReceiver();
		receiver.init(con, sequenceQueue);

		System.out.println("Waiting 10 seconds before verifying messages...");

		Thread.sleep(10000); // 10 seconds to receive sequence

		System.out.println("Verifying sequences...");
		receiver.verifyAndPrint();

		con.close();

	}
}
