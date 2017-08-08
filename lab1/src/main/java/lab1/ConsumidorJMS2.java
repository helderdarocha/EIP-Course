package lab1;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ConsumidorJMS2 {

	public static void main(String[] args) throws NamingException, JMSException {
		Context ctx = new InitialContext(); // jndi.properties
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination fila = (Destination) ctx.lookup("teste");

		try (Connection con = factory.createConnection()) {
			con.start();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// CRIAR UM CONSUMIDOR (MessageConsumer)
			MessageConsumer consumer = session.createConsumer(fila);

			// CONSUMIR A MENSAGEM (receive() ou usar MessageListener)
			consumer.setMessageListener((m) -> System.out.println(m));
			
			consumer.setMessageListener(new MessageListener() {
				public void onMessage(Message m) {
					try {
						System.out.println("Conteudo: " + ((TextMessage)m).getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});

		}

	}

}
