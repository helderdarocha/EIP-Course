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

public class LineSplitter extends MessageConsumerBase {
	Destination outChannel;;

	public LineSplitter(Destination inChannel, Destination outChannel, Connection con) {
		super(inChannel, con);
		this.outChannel = outChannel;
	}

	@Override
	public void onMessage(Message message) {
		try {
			String[] splitContent = ((TextMessage)message).getText().split("\\n");
			
			for(String payload: splitContent) {
				Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(outChannel);
				TextMessage newMessage = session.createTextMessage(payload);
				producer.send(newMessage);
				producer.close();
				session.close();
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
