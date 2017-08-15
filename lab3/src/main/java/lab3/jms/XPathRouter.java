package lab3.jms;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class XPathRouter extends MessageConsumerBase {
	private Destination invalid;
    private String expression;
    private Map<String, Destination> outMap;

	public XPathRouter(Destination inChannel, Destination invalid, String expression, Map<String, Destination> outMap, Connection con) {
		super(inChannel, con);
		this.expression = expression;
		this.outMap = outMap;
	}

	@Override
	public void onMessage(Message message) {
		try {
			String contents = ((TextMessage)message).getText();
			String result = XPathEngine.init().extractText(expression, contents);
			
			if (outMap.containsKey(result)) {
				routeMessage(outMap.get(result), message);
			} else {
				routeMessage(invalid, message);
				System.out.println("Invalid");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void routeMessage(Destination destination, Message message) throws JMSException {
		Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageProducer producer = session.createProducer(destination);
		producer.send(message);
		producer.close();
		session.close();
	}

}
