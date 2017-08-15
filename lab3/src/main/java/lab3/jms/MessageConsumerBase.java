package lab3.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public abstract class MessageConsumerBase implements MessageListener {
	
	private Destination inChannel;
	private Connection con;
	
	public MessageConsumerBase(Destination inChannel, Connection con) {
		this.con = con;
		this.inChannel = inChannel;
	}

	public Connection getConnection() {
		return con;
	}
	
	public void init() throws JMSException {
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(inChannel);
		consumer.setMessageListener(this);
	}

}
