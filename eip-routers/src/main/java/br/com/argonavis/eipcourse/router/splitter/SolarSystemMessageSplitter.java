package br.com.argonavis.eipcourse.router.splitter;

import java.util.HashMap;
import java.util.List;
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


public class SolarSystemMessageSplitter implements MessageListener {
		private Session session;
		private MessageProducer producer;
		private MessageConsumer consumer;
		
		Map<String, Destination> routes;

		public SolarSystemMessageSplitter(Connection con, Destination in, Destination out) throws JMSException {
			session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			consumer = session.createConsumer(in);
			producer = session.createProducer(out);
			consumer.setMessageListener(this);
			con.start();
		}

		@Override
		public void onMessage(Message message) {
			try {
				String messageID = message.getJMSMessageID();
				String type = message.getStringProperty("Type");
				
				if(type != null && type.equals("Solar System")) {
					System.out.println("Processing one file.");
					TextMessage tm = (TextMessage)message;
					String payload = tm.getText();
					List<String> splitPayload = new SolarSystemSplitterProcessor().split(payload);
					
					for(int i = 0; i < splitPayload.size(); i++) {
						TextMessage newMessage = session.createTextMessage(splitPayload.get(i));
						newMessage.setJMSCorrelationID(messageID); // ID of first message is SequenceID
						newMessage.setIntProperty("SequencePosition", i+1);
						newMessage.setIntProperty("SequenceSize", splitPayload.size());
						newMessage.setStringProperty("Type", "Solar System Fragment");
						producer.send(newMessage);
					}
				} else {
					producer.send(message);
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
				
				Destination from = (Destination) ctx.lookup("a-channel");
				Destination to   = (Destination) ctx.lookup("b-channel");

				new SolarSystemMessageSplitter(con, from, to);

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
