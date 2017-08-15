package lab3.jms;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.NamingException;

public class HeaderBasedRouter extends MessageConsumerBase {

	private Destination invalid;
	private Map<String, Destination> outMap;
	private String header;

	public HeaderBasedRouter(Destination inChannel, Destination invalid, String header, Map<String, Destination> outMap,
			Connection con) throws NamingException, JMSException {
		super(inChannel, con);
		this.header = header;
		this.invalid = invalid;
		this.outMap = outMap;
	}

	@Override
	public void onMessage(Message message) {
		try {
			String content = message.getStringProperty(header);
			if (outMap.containsKey(content)) {
				routeMessage(outMap.get(content), message);
			} else {
				routeMessage(invalid, message);
				System.out.println("Invalid");
			}
		} catch (JMSException e) {
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
