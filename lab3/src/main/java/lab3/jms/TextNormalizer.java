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

public class TextNormalizer extends MessageConsumerBase {
	Destination outChannel;
	
	public TextNormalizer(Destination inChannel, Destination outChannel, Connection con) {
		super(inChannel, con);
		this.outChannel = outChannel;
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(outChannel);
			TextMessage newMessage = session.createTextMessage();
			
			newMessage.setStringProperty("Placa", message.getStringProperty("FileName").split("_")[0]);
			newMessage.setStringProperty("Data", message.getStringProperty("FileName").split("_")[1].split("\\.")[0]);
			newMessage.setStringProperty("Group", "all");
			newMessage.setText(((TextMessage)message).getText());

			producer.send(newMessage);
			
			producer.close();
			session.close();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
