package br.com.argonavis.eipcourse.exercises.ch5.sequencia;

import java.io.IOException;
import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessageSequenceSender {
	
	MessageProducer producer;
	Session session;
	
	public void init(Connection con, Destination queue) throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(queue);
	}
	
	public void sendLines(String text) throws JMSException {
		String[] payloads = SplitterUtil.splitLines(text);
		Message[] messages = createSequence(payloads);
		send(messages);
	}
	
    public void sendBlocks(String text, int size) throws JMSException, IOException {
		String[] payloads = SplitterUtil.split(text, size);
		Message[] messages = createSequence(payloads);
		send(messages);
	}

	private void send(Message[] messages) throws JMSException {
		for(Message message : messages) {
			System.out.println("Sending message " + message.getIntProperty("Position") + " of " + message.getIntProperty("Size"));
			producer.send(message);
		}
	}

	private Message[] createSequence(String[] payloads) throws JMSException {
		Message[] messages = new Message[payloads.length];
		String sequenceID = payloads[0].length() > 25 ? payloads[0].substring(0,25) : payloads[0];
		sequenceID = "["+sequenceID.toUpperCase() + ":" + System.nanoTime() +"]";
		
		for(int i = 0; i < payloads.length; i++) {
			messages[i] = session.createTextMessage(payloads[i]);
			messages[i].setJMSCorrelationID(sequenceID);
			messages[i].setIntProperty("Size", payloads.length);
			messages[i].setIntProperty("Position", i+1);
			
		}
		return messages;
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		Destination sequenceQueue = (Destination) ctx
				.lookup("sequence-queue");

		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Connection con = factory.createConnection();

		
		MessageSequenceSender sender = new MessageSequenceSender();
		sender.init(con, sequenceQueue);
		
		String payload = "My mistress' eyes are nothing like the sun;\n"
                       + "Coral is far more red than her lips' red;\n"
                       + "If snow be white, why then her breasts are dun;\n"
                       + "If hairs be wires, black wires grow on her head.\n"
                       + "I have seen roses damask'd, red and white,\n"
                       + "But no such roses see I in her cheeks;\n"
                       + "And in some perfumes is there more delight\n"
                       + "Than in the breath that from my mistress reeks.\n"
                       + "I love to hear her speak, yet well I know\n"
                       + "That music hath a far more pleasing sound;\n"
                       + "I grant I never saw a goddess go;\n"
                       + "My mistress, when she walks, treads on the ground:\n"
                       + "   And yet, by heaven, I think my love as rare\n"
                       + "   As any she belied with false compare.\n";
		
		System.out.println("Sending seq 1 (lines)");
		sender.sendLines(payload);
		
		System.out.println("Sending seq 2 (blocks)");
		sender.sendBlocks(payload, 200);
		
		con.close();
	}
}
