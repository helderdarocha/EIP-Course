package lab1;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class ProdutorJMS {

	public static void main(String[] args) throws NamingException, JMSException {
		Context ctx = new InitialContext(); // jndi.properties
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination fila = (Destination) ctx.lookup("entrada");

		try (Connection con = factory.createConnection()) {

			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageProducer produtor = session.createProducer(fila);

			TextMessage m = session.createTextMessage("Hello World!");

			produtor.send(m);

			System.out.println("Mensagem enviada");

		}

	}

}
