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

public class CSVDiscardLineFilter extends MessageConsumerBase {
	Destination outChannel;
	String discardLine;

	public CSVDiscardLineFilter(Destination inChannel, Destination outChannel, String discardLine, Connection con) {
		super(inChannel, con);
		this.outChannel = outChannel;
		this.discardLine = discardLine;
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(outChannel);
			String contents = ((TextMessage)message).getText();

			if(!contents.equals(discardLine)) {
			    producer.send(message);
			}
			
			producer.close();
			session.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
