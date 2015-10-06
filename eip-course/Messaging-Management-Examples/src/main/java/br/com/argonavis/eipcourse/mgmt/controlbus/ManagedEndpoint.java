package br.com.argonavis.eipcourse.mgmt.controlbus;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class ManagedEndpoint<T> {
	
	private Session controlSession;
	private MessageConsumer controlIn;
	private MessageProducer controlOut;
	
	private Destination componentInChannel;
	private Destination componentOutChannel;
	private T component;
	
	public ManagedEndpoint(T component, Destination componentInChannel, Destination componentOutChannel) {
		this.component = component;
		this.componentInChannel = componentInChannel;
		this.componentOutChannel = componentOutChannel;
	}
	
	public void initControl(Connection con, Destination controlTopic) throws JMSException {
		String componentID = component.getClass().getName();
		controlSession = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		String selector = "ComponentID = '" + componentID + "' OR ComponentID = 'all'";
		controlIn = controlSession.createConsumer(controlTopic, selector);
		controlIn.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) {
				System.out.println("Received a control message.");
				try {
					Destination replyChannel = message.getJMSReplyTo();
					Message reply = controlSession.createMessage();
					reply.setJMSCorrelationID(message.getJMSMessageID());
					if(componentInChannel != null) {
					    reply.setStringProperty("InboundChannel", componentInChannel.toString());
					}
					if(componentOutChannel != null) {
					    reply.setStringProperty("OutboundChannel", componentOutChannel.toString());
					}
					reply.setStringProperty("ComponentID", componentID);
					reply.setLongProperty("Timestamp", new Date().getTime());
					
					controlOut = controlSession.createProducer(replyChannel);
					System.out.println("Sending status to " + replyChannel);
					controlOut.send(reply);
					
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		});
		con.start();
	}
	
	public void saveHistory(T component, Message inMessage, Message outMessage, Destination outChannel) throws JMSException {
		String history = null;
		if(inMessage != null) {
			history = inMessage.getStringProperty("MessageHistory");
		}
		if(history != null) {
			history += "," + component.getClass().getName() + "," + outChannel;
		} else {
		    history = component.getClass().getName() + "," + outChannel;
		}
		outMessage.setStringProperty("MessageHistory", history);
	}
}
