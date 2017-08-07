package eipcourse.exercises.jms.basic.route;

import java.util.Random;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * 
 * Funciona apenas em servidores que suportam JMS 2.0 (não funciona em ActiveMQ
 * 5)
 *
 */
public class JMSRouter {
	
	private static ConnectionFactory factory;

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination caixaDeEntrada = (Destination) ctx.lookup("entrada");
		Destination caixaDeSaida   = (Destination) ctx.lookup("saida");
		Destination filaProcessamento = (Destination) ctx.lookup("msgxml");
		Destination filaInvalida      = (Destination) ctx.lookup("invalida");

		try (Connection con = factory.createConnection();
		     Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
			
			con.start();
			session.createConsumer(caixaDeEntrada)
			       .setMessageListener((m) -> {
				try {
					switch(m.getStringProperty("content-type")) {
					case "text/plain":
						routeTo(m, caixaDeSaida, con);
						System.out.println("Enviada para saida.");
						break;
					case "text/xml":
						routeTo(m, filaProcessamento, con);
						System.out.println("Enviada para processamento.");
						break;
					default:
						routeTo(m, filaInvalida, con);
						System.out.println("Enviada para fila de mensages inválidas.");
					} 
				} catch (JMSException e) {
					e.printStackTrace();
				}
			});
			
			System.out.println("30 segundos para rotear mensagens. ");
			Thread.sleep(30000);
			System.out.println("Fim.");
		}
	}

	private static void routeTo(Message m, Destination destino, Connection con) throws JMSException {
		try (Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE)) {
			MessageProducer producer = session.createProducer(destino);
			producer.send(m);
		}
	}

}
