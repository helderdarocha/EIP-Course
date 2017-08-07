package eipcourse.exercises.jms.basic;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 
 * Funciona apenas em servidores que suportam JMS 2.0 
 * (não funciona em ActiveMQ 5)
 *
 */
public class JMSReceiver20 {

	public static void main(String[] args) throws NamingException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination caixaDeEntrada = (Destination) ctx.lookup("entrada");
		
		JMSContext context = factory.createContext();
		context.createConsumer(caixaDeEntrada)
		       .setMessageListener((m) -> {
					try {
						System.out.println("Type: " + m.getStringProperty("content-type"));
						System.out.println("Contents: " + ((TextMessage)m).getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				});
	}

}
