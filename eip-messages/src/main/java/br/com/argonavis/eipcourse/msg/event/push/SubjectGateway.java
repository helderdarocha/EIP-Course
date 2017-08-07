package br.com.argonavis.eipcourse.msg.event.push;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class SubjectGateway {
	
	MessageProducer updateProducer;
	Session session;
	
	public void init(Connection con, Destination notificationsTopic) throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		updateProducer = session.createProducer(notificationsTopic);
		con.start();
	}
	
	public void notify(String state) throws JMSException {
		TextMessage message = session.createTextMessage(state);
		updateProducer.send(message);
		
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		Destination notificationsTopic = (Destination) ctx.lookup("notifications");
		
		ConnectionFactory factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		
		SubjectGateway subject = new SubjectGateway();
		subject.init(con, notificationsTopic);
		
		String state = "The door is now open! Come in!";
		System.out.println("Will send a notification now!");
		subject.notify(state);
	}
}
