package br.com.argonavis.eipcourse.mgmt.wiretap;

import java.io.File;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

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
}
