package br.com.argonavis.eipcourse.msg.command;

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
public class CommandMessageClient {
	private Session session;
	private Destination commandQueue;
	private MessageProducer producer;

	protected void init(Connection con, Destination commandQueue) throws NamingException, JMSException {
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.commandQueue = commandQueue;
		producer = session.createProducer(commandQueue);
		con.start();
	}
	
	public void sendXMLCommand() throws JMSException {
		TextMessage m = session.createTextMessage();
		m.setText("<command>"
				+ "  <method class='br.com.argonavis.eipcourse.msg.command.PrintingService' name='print'>"
				+ "    <params>"
				+ "      <java.lang.String>This is some text to print.</java.lang.String>"
				+ "      <java.lang.Integer>4</java.lang.Integer>"
				+ "    </params>"
				+ "  </method>"
				+ "</command>");
		producer.send(m);
		
		System.out.println("XML request was sent.");
	}
	
	public void sendSimpleCommand() throws JMSException {
		Message m = session.createMessage();
		m.setStringProperty("Text-to-print", "This is some more text to print, perhaps later.");
		m.setIntProperty("Number-of-copies", 3);
		m.setStringProperty("Method-name", "addToPrintQueue");
		producer.send(m);
		
		System.out.println("Simple request was sent.");
	}
	
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		Destination commandQueue = (Destination) ctx.lookup("command-queue");
		Destination xmlCommandQueue = (Destination) ctx.lookup("xml-command-queue");
		
		ConnectionFactory factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		Connection con = factory.createConnection();
		
		CommandMessageClient simpleClient = new CommandMessageClient();
		simpleClient.init(con, commandQueue);
		simpleClient.sendSimpleCommand();
		
		CommandMessageClient xmlClient = new CommandMessageClient();
		xmlClient.init(con, xmlCommandQueue);
		xmlClient.sendXMLCommand();
		

        con.close();
	}
}
