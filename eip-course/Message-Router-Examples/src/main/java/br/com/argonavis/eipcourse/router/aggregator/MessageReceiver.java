package br.com.argonavis.eipcourse.router.aggregator;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MessageReceiver implements MessageListener {

	private String channelName;
	private MessageProducer control;
	private Session session;
	private Queue dataChannel;
	private Topic controlChannel;

	private long size = 0; // accumulates the size of messages received

	public MessageReceiver(String channelName) {
		this.channelName = channelName;
	}

	public void init(Connection con) throws NamingException, JMSException {
		Context ctx = new InitialContext();
		this.dataChannel = (Queue) ctx.lookup(channelName);
		this.controlChannel = (Topic) ctx.lookup("ctrl-channel");

		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(dataChannel);
		control = session.createProducer(controlChannel);
		consumer.setMessageListener(this);
		con.start();
	}

	public void onMessage(Message msg) {
		try {
			Long fileSize = msg.getLongProperty("Length");
			String fileName = msg.getStringProperty("Name");
			System.out.println(channelName.toUpperCase() + " received message with file " + fileName + ": " + fileSize + " bytes.");

			size += fileSize;
			System.out.println(channelName.toUpperCase() + ": Total size is now " + size);

			Message controlMessage = session.createMessage();
			controlMessage.setLongProperty("TotalSize", size);
			controlMessage.setStringProperty("ChannelName", channelName);
	        control.send(controlMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Connection con = factory.createConnection();

		MessageReceiver receiver1 = new MessageReceiver("a-channel");
		receiver1.init(con);
		
		MessageReceiver receiver2 = new MessageReceiver("b-channel");
		receiver2.init(con);

		System.out.println("Waiting 60 seconds for messages...");
		Thread.sleep(60000); // Will wait one minute for files
		con.close();
	}

}
