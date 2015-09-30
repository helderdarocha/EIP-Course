package br.com.argonavis.eipcourse.exercises.ch4;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessagingBridge {
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			// Exercicio 2: crie uma rota de xml-queue para a fila de impressao
			Destination from1 = (Destination) ctx.lookup("xml-queue");
			Destination to1   = (Destination) ctx.lookup("?????"); // printable-topic ou printable-queue?
			new JMSChannelBridge(con, from1, to1);
			
			// Exercicio 2: crie uma rota da fila de impressao para a fila de arquivos
			Destination from2 = (Destination) ctx.lookup("?????"); // printable-topic ou printable-queue?
			Destination to2   = (Destination) ctx.lookup("?????"); // file-topic ou file-queue?
			new JMSChannelBridge(con, from2, to2);
			
			// Exercicio 2: crie uma rota da png-queue para a fila de arquivos
			Destination from3 = (Destination) ctx.lookup("png-queue");
			Destination to3   = (Destination) ctx.lookup("?????"); // file-topic ou file-queue?
			new JMSChannelBridge(con, from3, to3);
			

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
