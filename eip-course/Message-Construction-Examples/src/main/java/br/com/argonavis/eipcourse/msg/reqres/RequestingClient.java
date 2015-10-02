package br.com.argonavis.eipcourse.msg.reqres;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This example was adapted from the book Enterprise INtegration Patterns, chapter 6
 */
public class RequestingClient {
	private Session session;
	private Destination replyQueue, requestQueue;
	private MessageProducer requestProducer;
	private MessageConsumer replyConsumer;

	protected void init(Connection con, Destination requestQueue, Destination replyQueue) throws NamingException, JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.replyQueue = replyQueue;
		this.requestQueue = requestQueue;
		requestProducer = session.createProducer(requestQueue);
		replyConsumer = session.createConsumer(replyQueue);
		con.start();
	}
	
	public void send() throws JMSException {
		TextMessage requestMessage = session.createTextMessage();
		requestMessage.setText("<command>"
				+ "  <method class='br.com.argonavis.eipcourse.msg.reqres.Operation' name='divide'>"
				+ "    <params>"
				+ "      <java.lang.Double>45.0</java.lang.Double>"
				+ "      <java.lang.Double>7.0</java.lang.Double>"
				+ "    </params>"
				+ "  </method>"
				+ "</command>");
		requestMessage.setJMSReplyTo(replyQueue);
		requestProducer.send(requestMessage);
		
		System.out.println("Request was sent.");
		Utils.printMessage(requestMessage);
	}
	
	public void receive() throws JMSException {
		System.out.print("Waiting for reply... ");
	    TextMessage replyMessage = (TextMessage)replyConsumer.receive();
	    
	    System.out.println("Received reply.");
	    Utils.printMessage(replyMessage);
	}
	
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		Destination requestQueue = (Destination) ctx.lookup("request-queue");
		Destination replyQueue   = (Destination) ctx.lookup("reply-queue");
		
		ConnectionFactory factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		
		RequestingClient requestor = new RequestingClient();
		requestor.init(con, requestQueue, replyQueue);
		
        requestor.send();
        requestor.receive();
        
        con.close();
	}
}
