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

public class KmToMilesTransformer extends MessageConsumerBase  {
	Destination outChannel;
	
	public KmToMilesTransformer(Destination inChannel, Destination outChannel, Connection con) {
		super(inChannel, con);
		this.outChannel = outChannel;
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(outChannel);
			String contents = ((TextMessage)message).getText();
			
			Double miles = Double.valueOf(contents);
			Double km = miles * 1.609;

			TextMessage newMessage = session.createTextMessage("" + km);
			Utils.copyProperties(message, newMessage);
			producer.send(newMessage);
			
			producer.close();
			session.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
