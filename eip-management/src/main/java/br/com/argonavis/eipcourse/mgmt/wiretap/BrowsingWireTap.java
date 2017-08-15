package br.com.argonavis.eipcourse.mgmt.wiretap;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;

public class BrowsingWireTap {

	private Session session;
	private QueueBrowser browser;

	public BrowsingWireTap(Connection con, Queue inQueue) throws JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		browser = session.createBrowser(inQueue);
		con.start();
	}

	public void browse() throws JMSException {
		Enumeration<Message> messages = browser.getEnumeration();

		if (!messages.hasMoreElements()) {
			System.out.println("No messages in queue");
		} else {
			while (messages.hasMoreElements()) {
				Message message = messages.nextElement();
				System.out.println("Message: " + message);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		Queue queue = (Queue) ctx.lookup("inbound");

		BrowsingWireTap browser = new BrowsingWireTap(con, queue);
		browser.browse();

		con.close();
	}
}
