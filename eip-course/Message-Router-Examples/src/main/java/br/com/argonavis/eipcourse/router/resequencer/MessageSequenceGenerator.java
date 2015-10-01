package br.com.argonavis.eipcourse.router.resequencer;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessageSequenceGenerator {
	
	private Session session;
	private MessageProducer producer;
	
	public MessageSequenceGenerator(Connection con, Destination destination) throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(destination);
		con.start();
	}
	
	public void sendSequence(int[] positions, String sequenceID) throws JMSException {
		System.out.println("\nSequence: " + sequenceID);
		for(int i = 0; i < positions.length; i++) {
			Message message = session.createMessage();
			message.setStringProperty(MessageSequence.SEQUENCE_ID_HEADER, sequenceID);
			message.setIntProperty(MessageSequence.SIZE_HEADER, positions.length);
			message.setIntProperty(MessageSequence.POSITION_HEADER, positions[i]);
			
			System.out.println("   " + message.getIntProperty(MessageSequence.POSITION_HEADER));
			
			producer.send(message);
		}
	}
	
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			Destination destination = (Destination) ctx.lookup("b-channel");

			MessageSequenceGenerator generator = new MessageSequenceGenerator(con, destination);
			generator.sendSequence(new int[]{8,3,5,1,6,7,2,4}, "Seq A");
			generator.sendSequence(new int[]{2,3,1,5,4},       "Seq B");
			generator.sendSequence(new int[]{1,2,3},           "Seq C");
			generator.sendSequence(new int[]{4,3,2,1},         "Seq D");

			System.out.println("All sequences sent.");
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
