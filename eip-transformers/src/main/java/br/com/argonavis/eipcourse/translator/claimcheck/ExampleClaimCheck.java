package br.com.argonavis.eipcourse.translator.claimcheck;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class ExampleClaimCheck implements MessageListener {
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;

	public ExampleClaimCheck(Connection con, Destination in, Destination out)
			throws JMSException {
		System.out.println("Creating bridge from " + in + " to " + out);
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		producer = session.createProducer(out);
		consumer = session.createConsumer(in);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message incomingMessage) {
		try {
			String payload = ((TextMessage) incomingMessage).getText();

			String data = new XPathFilter().extractNode("/document/data", payload);
			String key  = new XPathFilter().extractText("/document/filename", payload);
			DataStore.save(key, data);

			String filteredPayload = new XPathFilter().removeContents("/document/data/node()", payload);
			System.out.println("Filtered payload: " + filteredPayload);
			TextMessage filtered = session.createTextMessage(filteredPayload);

			filtered.setStringProperty("ClaimCheck", key);

			producer.send(filtered);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
