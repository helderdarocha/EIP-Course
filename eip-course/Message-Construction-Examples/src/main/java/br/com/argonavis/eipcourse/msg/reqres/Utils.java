package br.com.argonavis.eipcourse.msg.reqres;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

public class Utils {
	public static void printMessage(Message message) throws JMSException {
		System.out.println("  JMSMessageID: " + message.getJMSMessageID());
		System.out.println("  JMSCorrelationID: " + message.getJMSCorrelationID());
		System.out.println("  JMSReplyTo: " + message.getJMSReplyTo());
		if(message instanceof TextMessage) {
		    System.out.println("  Contents: " + ((TextMessage)message).getText());
		} else {
			System.out.println("  Not a TextMessage");
		}
	}
}
