package br.com.argonavis.eipcourse.msg.event.push;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class ObserverGateway implements MessageListener {

	private String state;

	public void init(String initialState, Connection con,
			Destination notificationsTopic) throws JMSException {
		this.state = initialState;
		Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		MessageConsumer updateConsumer = session
				.createConsumer(notificationsTopic);
		updateConsumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			System.out.println("Notification has arrived.");
			TextMessage eventMessage = (TextMessage) message;
			String newState = eventMessage.getText();
			this.update(newState);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	private void update(String newState) {
		this.state = newState;
		System.out.println("Current state is now: " + newState);
	}
	
	public String getState() {
		return state;
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		Destination notificationsTopic = (Destination) ctx
				.lookup("notifications");

		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Connection con = factory.createConnection();

		ObserverGateway observer = new ObserverGateway();
		observer.init("The door is closed!", con, notificationsTopic);
		System.out.println("Initial state: " + observer.getState());
		
		System.out.println("Waiting for notifications.");

		Thread.sleep(60000); // 1 minute to receive notification
		con.close();
	}
}
