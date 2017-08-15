package lab3.jms;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class ResultConsumer extends MessageConsumerBase {

	public ResultConsumer(Destination inChannel, Connection con) {
		super(inChannel, con);
	}

	@Override
	public void onMessage(Message message) {
		try {
			System.out.println("\nResult: " + ((TextMessage)message).getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
