package br.com.argonavis.eipcourse.exercises.ch4;

import java.util.HashMap;
import java.util.Map;

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

public class JMSMessageHeaderRouter implements MessageListener{
	private Session session;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private String headerName;
	private Destination invalidChannel;
	
	Map<String, Destination> routes;

	public JMSMessageHeaderRouter(Connection con, String headerName, Map<String, Destination> routes, Destination in, Destination invalid)
			throws JMSException {
	    this.invalidChannel = invalid;
	    this.headerName = headerName;
	    this.routes = routes;
	    this.invalidChannel = invalid;
	    
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(in);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
	public void onMessage(Message message) {
		try {
			// Receive incoming message and get header
			String routingHeader = message.getStringProperty(headerName);
			Destination out = routes.get(routingHeader);
			
			// route to valid channel or redirect to invalid-message-channel
			if(out != null) {
				producer = session.createProducer(out);
				producer.send(message);
			} else {
				// Exercicio 4 - envie para invalid-queue em vez de imprimir esta mensagem
				System.out.println("Message not routed. No route for "+headerName+"="+routingHeader);
			}
			
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			Destination in = (Destination) ctx.lookup("inbound");
			Destination pngChannel = (Destination) ctx.lookup("png-queue");
			Destination xmlChannel = (Destination) ctx.lookup("xml-queue");
			Destination txtChannel = (Destination) ctx.lookup("txt-queue");
			Destination invalid = (Destination) ctx.lookup("invalid-queue");
			
			Map<String, Destination> routes = new HashMap<>();
			routes.put("png", pngChannel);
			routes.put("xml", xmlChannel);
			routes.put("txt", txtChannel);

			new JMSMessageHeaderRouter(con, "Tipo", routes, in, invalid);

			System.out.println("Receiving messages for 60 seconds...");
			Thread.sleep(60000);
			System.out.println("Done.");
			con.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (JMSException e) {
				}
			}
		}
	}
}
