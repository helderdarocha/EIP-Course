package lab1;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class RouterJMS {

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext(); // jndi.properties
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination entrada = (Destination) ctx.lookup("entrada");
		Destination saida   = (Destination) ctx.lookup("teste");

		try (Connection con = factory.createConnection()) {
             
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

			MessageProducer produtor = session.createProducer(saida);
			MessageConsumer consumidor = session.createConsumer(entrada);
			
			con.start();
			
			System.out.println("Esperando mensagens...");
			Message m = consumidor.receive();
			System.out.println("Repassando " + m);
			produtor.send(m);
			System.out.println("Enviada!");
			
		}

	}

}
