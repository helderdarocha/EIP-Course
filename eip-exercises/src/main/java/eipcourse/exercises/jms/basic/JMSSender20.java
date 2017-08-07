package eipcourse.exercises.jms.basic;

import java.util.Random;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
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
public class JMSSender20 {

	public static void main(String[] args) throws NamingException, JMSException {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination caixaDeEntrada = (Destination) ctx.lookup("entrada");
		
		try (JMSContext context = factory.createContext()) {
			for (int i = 0; i < 10; i++) {
				Random rand = new Random();
				int sorte = rand.nextInt(1000) + 1;
				int index = rand.nextInt(2);

				TextMessage m = context.createTextMessage();

				if (index == 0) {
					m.setStringProperty("content-type", "text/plain");
					m.setText("" + sorte);
				} else {
					m.setStringProperty("content-type", "text/xml");
					m.setText("<sorte>" + sorte + "</sorte>");
				}
				context.createProducer().send(caixaDeEntrada, m);
			}
		}

	}

}
