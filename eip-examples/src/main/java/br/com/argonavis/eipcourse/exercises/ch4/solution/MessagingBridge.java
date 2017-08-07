package br.com.argonavis.eipcourse.exercises.ch4.solution;

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
			
			// Bridge 1 - XML files can be printed
			Destination from1 = (Destination) ctx.lookup("xml-queue");
			Destination to1   = (Destination) ctx.lookup("printable-topic");
			new JMSChannelBridge(con, from1, to1);
			
			// Bridge 2 - Printable files can be saved
			Destination from2 = (Destination) ctx.lookup("printable-topic");
			Destination to2   = (Destination) ctx.lookup("file-topic");
			new JMSChannelBridge(con, from2, to2);
			
			// Bridge 3 - PNG files can be saved
			Destination from3 = (Destination) ctx.lookup("png-queue");
			Destination to3   = (Destination) ctx.lookup("file-topic");
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
