package br.com.argonavis.eipcourse.exercises.ch3.solution;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;

public class UpperCaseTransformer {
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			Destination from = (Destination) ctx.lookup("txt-queue"); // only txt files should be here
			Destination to = (Destination) ctx.lookup("printable-queue");

			new JMSChannelBridge(con, from, to, new PayloadProcessor() {
				public Object process(Object payload) {
					String text = (String)payload;
					return text.toUpperCase();
				}
			});
			
			System.out.println("Receiving messages for 60 seconds...");
			Thread.sleep(60000);
			System.out.println("Done.");

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
