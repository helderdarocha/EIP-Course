package br.com.argonavis.eipcourse.translator.enricher;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;

public class Enricher {
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			Destination from = (Destination) ctx.lookup("produtos");
			Destination to   = (Destination) ctx.lookup("saida"); 
			
			new JMSChannelBridge(con, from, to, new PayloadProcessor() {
				public Object process(Object payload) {
					long id = ((Produto)payload).getId();
					Produto p = ProductDatabase.getProduto(id);
					System.out.println("Produto obtido: " + p);
					return p;
				}
			});

			
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
