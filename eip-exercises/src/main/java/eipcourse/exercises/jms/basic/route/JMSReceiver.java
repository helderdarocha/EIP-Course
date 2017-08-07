package eipcourse.exercises.jms.basic.route;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JMSReceiver {

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination caixaDeEntrada = (Destination) ctx.lookup("saida");

		try (Connection con = factory.createConnection();
				Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);) {
			
			con.start();
			session.createConsumer(caixaDeEntrada).setMessageListener((m) -> {
				try {
					System.out.print("[" + ((TextMessage) m).getText() + "]");
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});
			
			System.out.println();
			System.out.println("30 segundos para receber mensagens. ");
			Thread.sleep(30000);
			System.out.println("\nFim.");
		}

	}

}
