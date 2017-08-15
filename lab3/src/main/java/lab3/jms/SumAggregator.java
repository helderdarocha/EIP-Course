package lab3.jms;

import java.util.ArrayList;
import java.util.List;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

public class SumAggregator extends MessageConsumerBase {
    Destination outChannel;
    Destination  inChannel;
    List<Message> messages = new ArrayList<>();
    String correlationSelector;
	
	public SumAggregator(Destination inChannel, Destination outChannel, String correlationSelector, Connection con) {
		super(inChannel, con);
		this.outChannel = outChannel;
		this.inChannel = inChannel;
		this.correlationSelector = correlationSelector;
	}
	
	@Override
	public void init() throws JMSException {
		Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer consumer = session.createConsumer(inChannel, correlationSelector);
		consumer.setMessageListener(this);
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			messages.add(message);
			print(message);
			
			if(release()) {
				Session session = getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
				String contents = aggregate();
				Message result = session.createTextMessage(contents);
			    MessageProducer producer = session.createProducer(outChannel);
			    producer.send(result);
			    producer.close();
			    session.close();
			}
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
    public String aggregate() throws JMSException {
    	    double sum = 0.0;
    		for(Message m : messages) {
    			String contents = ((TextMessage)m).getText();
    			double value = Double.parseDouble(contents);
    			sum += value;
    		}
		messages.clear();
		return "" + sum;
	}

	public boolean release() {
		return messages.size() >= 57;
	}
	
	public void print(Message message) throws JMSException {
		TextMessage tm = (TextMessage) message;
		System.out.println("Data: " + tm.getStringProperty("Data") + "\nPlaca: "
				+ tm.getStringProperty("Placa") + "\nPayload: " + tm.getText() + "\n");
	}
}
