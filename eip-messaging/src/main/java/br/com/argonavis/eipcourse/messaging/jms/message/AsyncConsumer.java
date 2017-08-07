package br.com.argonavis.eipcourse.messaging.jms.message;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class AsyncConsumer implements MessageListener {

	@Override
	public void onMessage(Message message) {
		TextMessage tm = (TextMessage)message;
		try {
			String category = tm.getStringProperty("category");
			String content  = tm.getStringProperty("contentType");
			String contents = tm.getText();
			
			System.out.println("Category: " + category);
			System.out.println("Content type: " + content);
			System.out.println("Contents: " + contents);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
