package br.com.argonavis.eipcourse.exercises.ch3.e7;

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
			
			/*
			// Exercicio 5 - Interligar inbound a outbound
			Destination from = (Destination) ctx.lookup("inbound");
			Destination to   = (Destination) ctx.lookup("outbound");
			*/

			// Exercicio 6 - Canais de texto para saida de impressao
			Destination from = (Destination) ctx.lookup("xml-queue");
			Destination to   = (Destination) ctx.lookup("printable-queue");
			
			new JMSChannelBridge(con, from, to, new PayloadProcessor());

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
