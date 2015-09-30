package br.com.argonavis.eipcourse.exercises.ch3;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import br.com.argonavis.eipcourse.exercises.utils.FileUtils;
import br.com.argonavis.eipcourse.exercises.utils.JMSUtils;

public class FileOutboundAdapter implements MessageListener {
	
	public static String OUTBOX = "/tmp/jms/outbox";
	
	private File directory;
	
	private Session session;
	private MessageConsumer consumer;

	public FileOutboundAdapter(Connection con, Destination source, File directory) throws JMSException {
		this.directory = directory;
		
		session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
		consumer = session.createConsumer(source);
		consumer.setMessageListener(this);
		con.start();
	}

	@Override
    public void onMessage(Message message) {
		try {
			Object payload = JMSUtils.extractPayload(message);
		
    	String filename = message.getStringProperty("Filename");
    	String type = message.getStringProperty("Tipo");
    	if(payload instanceof String) {
    		FileUtils.saveFile((String)payload, directory, filename, type);
    	} else if (payload instanceof byte[]) {
    		FileUtils.saveFile((byte[])payload, directory, filename, type);
    	} else {
    		System.out.println("Datatype not supported.");
    	}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx
				.lookup("ConnectionFactory");
		
		Destination queue = (Destination) ctx.lookup("outbound");
		Connection con = factory.createConnection();
		con.start();
		
		FileOutboundAdapter adapter = new FileOutboundAdapter(con, queue, new File(OUTBOX));

		System.out.println("Waiting for 60 seconds...");
		Thread.sleep(60000);
		System.out.println("Done.");
		con.close();
	}
}
