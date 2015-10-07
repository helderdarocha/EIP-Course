package br.com.argonavis.eipcourse.mgmt.wiretap;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.naming.Context;
import javax.naming.InitialContext;

public class WireTappedBridge {
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();

			Destination fromChannel = (Destination) ctx.lookup("in-channel");
			Destination toChannel   = (Destination) ctx.lookup("out-channel");
			Destination wireTap     = (Destination) ctx.lookup("wiretap");
			/*
			FileInboundAdapter adapter = new FileInboundAdapter(con, fromChannel, new File(FileInboundAdapter.INBOX), false);
			List<File> files = adapter.loadFiles();
			if (!files.isEmpty()) {
				List<Message> messages = adapter.createMessages(files);
				adapter.send(messages);
			}
			*/
			new WireTap(con, fromChannel, toChannel, wireTap);

			new FileOutboundAdapter(con, toChannel, new File(FileOutboundAdapter.OUTBOX));
			
			new FileOutboundAdapter(con, wireTap, new File("/tmp/jms/wiretap"));
			

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
