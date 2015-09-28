package br.com.argonavis.eipcourse.router.aggregator;

import java.util.HashMap;
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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class DynamicRouter implements MessageListener {

	private Session messageSession;
	private Destination controlChannel;
	private Destination inboundChannel;
	private Destination currentDestination;
	private MessageConsumer control;

	Map<String, Long> rules = new HashMap<>();

	public void init(Connection con) throws NamingException, JMSException {
		Context ctx = new InitialContext();
		this.controlChannel = (Destination) ctx.lookup("ctrl-channel");
		this.inboundChannel = (Destination) ctx.lookup("inbound-channel");

		rules.put("a-channel", 0L);
		rules.put("b-channel", 0L);

		messageSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

		MessageConsumer consumer = messageSession.createConsumer(inboundChannel);
		consumer.setMessageListener(this);

		Session controlSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		control = controlSession.createConsumer(controlChannel);

		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			String filename = message.getStringProperty("Name");
			System.out.println("Received message: " + filename);

			calculateDestination(); // dynamic routing!

			MessageProducer producer = messageSession
					.createProducer(currentDestination);
			producer.send(message);
			System.out.println("Message with " + filename + " sent to "
					+ currentDestination);

			System.out.println("Waiting for new routing information... (timeout 10s)");
			Message controlMessage = control.receive(10000);
			Long totalSize = controlMessage.getLongProperty("TotalSize");
			String channelName = controlMessage
					.getStringProperty("ChannelName");
			rules.put(channelName, totalSize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns channel that received the least amount of data
	 */
	private void calculateDestination() throws NamingException {
		Context ctx = new InitialContext();
		Long min = Long.MAX_VALUE;
		String channelName = "a-channel"; // initially use this channel
		for (Map.Entry<String, Long> entry : rules.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
			if (entry.getValue() < min) {
				min = entry.getValue();
				channelName = entry.getKey();
			}
		}
		currentDestination = (Destination) ctx.lookup(channelName);
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Connection con = factory.createConnection();

		DynamicRouter router = new DynamicRouter();
		router.init(con);

		System.out.println("Waiting 60 seconds for messages...");
		Thread.sleep(60000); // Will wait one minute for files
		con.close();
	}

}
