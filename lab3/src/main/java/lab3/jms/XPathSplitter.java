package lab3.jms;

import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class XPathSplitter extends MessageConsumerBase {
	private Destination outChannel;
	private String expression;

	public XPathSplitter(Destination inChannel, Destination outChannel, String expression, Connection con) {
		super(inChannel, con);
		this.outChannel = outChannel;
		this.expression = expression;
	}

	@Override
	public void onMessage(Message message) {
		try {
			String contents = ((TextMessage)message).getText();
			List<String> splitContent = XPathEngine.init().extractNodeSetAsString(expression, contents);
			
			for(String payload: splitContent) {
				Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(outChannel);
				TextMessage newMessage = session.createTextMessage(payload);
				Utils.copyProperties(message, newMessage);
				producer.send(newMessage);
				producer.close();
				session.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
