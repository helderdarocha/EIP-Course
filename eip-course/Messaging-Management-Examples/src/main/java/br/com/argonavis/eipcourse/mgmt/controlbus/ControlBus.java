package br.com.argonavis.eipcourse.mgmt.controlbus;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class ControlBus implements MessageListener {

	private Session controlSession;
	private MessageConsumer statusReceiver;
	private MessageProducer requestSender;
	private Destination replyQueue;

	public ControlBus(Connection con, Destination controlTopic,
			Destination replyQueue) throws JMSException {
		
		this.replyQueue = replyQueue;
		controlSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		requestSender = controlSession.createProducer(controlTopic);
		statusReceiver = controlSession.createConsumer(replyQueue);
		statusReceiver.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		System.out.println("Received response.");
		try {
			System.out.println("\nStatus response received for "
					+ message.getStringProperty("ComponentID"));
			System.out.println("Inbound channel: "
					+ message.getStringProperty("InboundChannel"));
			System.out.println("Outbound channel: "
					+ message.getStringProperty("OutboundChannel"));
			System.out.println("Timestamp: "
					+ message.getStringProperty("Timestamp") + "\n");
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void requestStatus(String componentID) throws JMSException {
		System.out.println("Will request status for "+componentID+":");
		Message controlMessage = controlSession.createMessage();
		controlMessage.setStringProperty("ComponentID", componentID);
		controlMessage.setJMSReplyTo(replyQueue);
		requestSender.send(controlMessage);
	}

	public void requestStatusAllComponents() throws JMSException {
		System.out.println("Will request status for all components");
		requestStatus("all");
	}
	
	public void detour(String componentID, Destination detour) throws JMSException {
		System.out.println("Will request that "+componentID+" detour to " + detour);
		Message controlMessage = controlSession.createMessage();
		controlMessage.setStringProperty("ComponentID", componentID);
		controlMessage.setJMSReplyTo(replyQueue);
		controlMessage.setObjectProperty("Detour", detour);
		requestSender.send(controlMessage);
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		Destination controlTopic = (Destination) ctx.lookup("control-topic");
		Destination replyQueue = (Destination) ctx.lookup("control-reply");

		ControlBus control = new ControlBus(con, controlTopic, replyQueue);

		control.requestStatus("ProductServiceActivator");
		control.requestStatusAllComponents();
		
		Thread.sleep(30000);
		// close the connections
		System.out.println("Done.");
		con.close();

	}
}
