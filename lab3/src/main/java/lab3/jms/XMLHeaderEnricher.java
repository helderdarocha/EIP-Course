package lab3.jms;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class XMLHeaderEnricher extends MessageConsumerBase {
	Destination outChannel;
	Map<String, String> headers;

	public XMLHeaderEnricher(Destination inChannel, Destination outChannel, Map<String, String> headers, Connection con) {
		super(inChannel, con);
		this.outChannel = outChannel;
		this.headers = headers;
	}

	@Override
	public void onMessage(Message message) {
		try {
			Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(outChannel);
			String contents = ((TextMessage)message).getText();
			
            TextMessage newMessage = session.createTextMessage(((TextMessage)message).getText());
            Utils.copyProperties(message, newMessage);
            
            for(String header: headers.keySet()) {
            	    String result = XPathEngine.init().extractText(headers.get(header), contents);
            	    newMessage.setStringProperty(header, result);
            }

			producer.send(newMessage);
			
			producer.close();
			session.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
