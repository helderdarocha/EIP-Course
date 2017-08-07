package br.com.argonavis.eipcourse.translator.envelope;

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

public class MessagingRoute {
	public static void main(String[] args) {
		Connection con = null;
		try {
			Context ctx = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) ctx
					.lookup("ConnectionFactory");
			con = factory.createConnection();
			
			Destination inbound = (Destination) ctx.lookup("inbound");
			
			FileInboundAdapter adapter = new FileInboundAdapter(con, inbound, new File(FileInboundAdapter.INBOX), false);
			List<File> files = adapter.loadFiles();
			if (!files.isEmpty()) {
				List<Message> messages = adapter.createMessages(files);
				adapter.send(messages);
			}
			
			Destination from2 = inbound;
			Destination to2   = (Destination) ctx.lookup("encoded-queue");
			
			new JMSChannelBridge(con, from2, to2, new PayloadProcessor() {
				public Object process(Object payload) {
					System.out.println("Encoding data.");
					String wrappedData = new XMLDataWrapper().wrap((byte[])payload);
					return wrappedData;
				}
			}, new PropertiesProcessor() {
				public Map<String, Object> process(Map<String, Object> properties) {
					System.out.println("Resetting Type to XML");
					properties.put("WrappedType", (String)properties.get("Type"));
					properties.put("Type", "xml");
					return properties;
				}
			});
			
			Destination from3 = to2;
			Destination to3   = (Destination) ctx.lookup("outbound"); 
			
			new JMSChannelBridge(con, from3, to3, new PayloadProcessor() {
				public Object process(Object payload) {
					System.out.println("Decoding data.");
					byte[] data = new XMLDataUnwrapper().unwrap((String) payload);
					return data;
				}
			}, new PropertiesProcessor() {
				public Map<String, Object> process(Map<String, Object> properties) {
					System.out.println("Resetting Type to original type");
					properties.put("Type", (String)properties.get("WrappedType"));
					properties.remove("WrappedType");
					return properties;
				}
			});
			
			Destination from4 = (Destination) ctx.lookup("outbound");
			new FileOutboundAdapter(con, from4, new File(FileOutboundAdapter.OUTBOX));
			
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
