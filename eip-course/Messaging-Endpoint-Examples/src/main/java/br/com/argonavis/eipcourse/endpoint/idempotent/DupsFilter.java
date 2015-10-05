package br.com.argonavis.eipcourse.endpoint.idempotent;

import java.util.HashSet;
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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DupsFilter implements MessageListener {

	private MessageProducer producer;
	private MessageProducer invalidProducer;
	private Session session;
	
	private Set<String> messageIDs = new HashSet<>();
	boolean removeDups = false;
	
	public DupsFilter(Connection con, Destination in, Destination out, Destination invalid, boolean removeDups) throws JMSException {
		this.removeDups = removeDups;
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(in);
		producer = session.createProducer(out);
		invalidProducer = session.createProducer(invalid);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			String messageID = message.getJMSMessageID();

			if (messageIDs.contains(messageID)) { 
				System.out.println("Duplicate found!");
				if(removeDups) {
				    invalidProducer.send(message);
				} else {
					message.setBooleanProperty("Duplicate", true);
					producer.send(message);
				}
			} else {
				System.out.println("Not a duplicate!");
				messageIDs.add(messageID);
				producer.send(message);
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		
		Destination from = (Destination) ctx.lookup("inbound");
		Destination to  = (Destination) ctx.lookup("outbound");
		Destination invalid  = (Destination) ctx.lookup("invalid");
		
		new DupsFilter(con, from, to, invalid, true);

		System.out.println("Waiting 60 seconds for messages...");
		
        Thread.sleep(60000); // Will wait one minute for files
        con.close();
	}
}
