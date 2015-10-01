package br.com.argonavis.eipcourse.router.resequencer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

public class MessageResequencer implements MessageListener {

	private Map<String, TreeSet<Message>> messageSetMap = new HashMap<>();
	private Map<String, Integer> sizeMap = new HashMap<>();
	private Map<String, Integer> positionMap = new HashMap<>();

	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;

	public MessageResequencer(Connection con, Destination in, Destination out)
			throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(out);
		consumer = session.createConsumer(in);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			String sequenceID = message
					.getStringProperty(MessageSequence.SEQUENCE_ID_HEADER);
			TreeSet<Message> messageSet = messageSetMap.get(sequenceID);

			if (messageSet == null) {
				int sequenceSize = message
						.getIntProperty(MessageSequence.SIZE_HEADER);
				System.out.println("Creating set for " + sequenceID + " ("
						+ sequenceSize + " messages)");
				messageSet = new TreeSet<>(new MessageComparator());
				sizeMap.put(sequenceID, sequenceSize);
				messageSetMap.put(sequenceID, messageSet);
				positionMap.put(sequenceID, 1); // sequences starting in 1
			}

			int currentSetSize = sizeMap.get(sequenceID);
			int currentPosition = positionMap.get(sequenceID); // always starts
																// in 1

			int seqPosition = message
					.getIntProperty(MessageSequence.POSITION_HEADER);
			if (seqPosition >= currentPosition) { // ignore duplicates!
				System.out.println("Adding message " + sequenceID + ":" + seqPosition);
				messageSet.add(message); // automatically orders

				// get lowest message in set
				Message lowest = messageSet.first();
				int lowestPosition = lowest.getIntProperty(MessageSequence.POSITION_HEADER);

				System.out.println("Lowest position: " + lowestPosition
						+ " CurrentPosition: " + currentPosition);

				while (lowestPosition == currentPosition) { // position is never less than currentPosition

					System.out.println("Sending message " + sequenceID + ":"
							+ lowestPosition);
					producer.send(lowest);

					if (currentPosition == currentSetSize) { // done
						System.out.println("Sequence " + sequenceID
								+ " is done. All messages were sent.");
						messageSet.clear();
						messageSetMap.remove(sequenceID);
						sizeMap.remove(sequenceID);
						positionMap.remove(sequenceID);
						currentPosition = 0;
					} else { // increment next lowest message
						positionMap.put(sequenceID, ++currentPosition);
						System.out.println("Removing message " + sequenceID
								+ ":" + lowestPosition + " from queue.");
						messageSet.remove(lowest);

						// Check next message in set
						lowest = messageSet.first();
						lowestPosition = lowest
								.getIntProperty(MessageSequence.POSITION_HEADER);
					}
					System.out.println(sequenceID + " set size: "
							+ messageSet.size());
					System.out.println("Map size: " + messageSetMap.size());
				}
			}

		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();

			Destination from = (Destination) ctx.lookup("b-channel");
			Destination to = (Destination) ctx.lookup("c-channel");

			new MessageResequencer(con, from, to);

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
