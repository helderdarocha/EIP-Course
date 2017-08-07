package br.com.argonavis.eipcourse.translator.claimcheck;

import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;

public class MessagingRoute {
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			Destination from = (Destination) ctx.lookup("documents");
			Destination to   = (Destination) ctx.lookup("filtered-documents");
			
			new ExampleClaimCheck(con, from, to);
			
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
