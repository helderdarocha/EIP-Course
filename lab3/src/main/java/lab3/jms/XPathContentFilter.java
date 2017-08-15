package lab3.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class XPathContentFilter extends MessageConsumerBase {
	
	private Destination outChannel;
	private String expression;

	public XPathContentFilter(Destination inChannel, Destination outChannel, String expression, Connection con) {
		super(inChannel, con);
		this.outChannel = outChannel;
		this.expression = expression;
	}

	@Override
	public void onMessage(Message message) {
		try {
			Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(outChannel);
			String contents = ((TextMessage)message).getText();
			
            TextMessage newMessage = session.createTextMessage();
			Utils.copyProperties(message, newMessage);
			
			String result = XPathEngine.init().extractText(expression, contents);
			newMessage.setText(result);

			producer.send(newMessage);
			
			producer.close();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
