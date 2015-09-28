package br.com.argonavis.eipcourse.router.filter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

public class FileAdapter {
    private File directory;

	public FileAdapter(File directory) throws JMSException {
    	this.directory = directory;
    }

    public void send(List<Message> messages, MessageProducer producer) {
		try {
			for(Message message: messages) {
				System.out.println("Sending message");
				producer.send(message);
			}
			System.out.println(messages.size() + " messages sent!");
		} catch(JMSException e){
			System.err.println("JMS Exception: " + e);
		}
	}
    
    public List<Message> createMessages(Session session, List<File> files) throws JMSException {
    	List<Message> messages = new ArrayList<>();
    	for(File file: files) {
    		Message message = null;
    		boolean valid = true;
    		String type = file.getName().substring(file.getName().lastIndexOf('.')+1).toLowerCase();
 	        if(type.equals("xml") || type.equals("txt")) {
 	        	String data = readChars(file);
 	        	message = session.createTextMessage(data);
 	        } else if (type.equals("png")) {
 	        	byte[] data = readBytes(file);
 	        	message = session.createBytesMessage();
 	        	((BytesMessage)message).writeBytes(data);
 	        } else {
 	        	valid = false;
 	        }

    	    if(valid) {
    	       message.setLongProperty("Length", file.length());
    	       message.setStringProperty("Name", file.getName());
    	       message.setStringProperty("Type", type);
    	       messages.add(message);
    	       file.delete();
    	    }
    	}
    	return messages;
    }
    
    public List<File> loadFiles() {
    	List<File> files = new ArrayList<>();
    	String[] fileNames = directory.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml") 
						|| name.endsWith(".png") 
                        || name.endsWith(".txt");
			}
    	});
    	if(fileNames != null && fileNames.length > 0) {
    		for(String fileName : fileNames) {
    		    files.add(new File(directory, fileName));
    		}
    	}
    	return files;
    }
    
    public byte[] readBytes(File file) {
    	InputStream in = null;
    	OutputStream out = null;
		try {
			in = new FileInputStream(file);
			out = new ByteArrayOutputStream();
	    	byte[] buffer = new byte[4096];
	    	int len = in.read(buffer);
	    	while(len > 0) {
	    		out.write(buffer, 0, len);
	    		len = in.read(buffer);
	    	}
	    	out.flush();
	    	return buffer;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {}
		}
    }
    
    public String readChars(File file) {
    	Reader reader = null;
    	Writer writer = null;
		try {
			reader = new FileReader(file);
			writer = new StringWriter();
	    	char[] buffer = new char[4096];
	    	int len = reader.read(buffer);
	    	while(len > 0) {
	    		writer.write(buffer, 0, len);
	    		len = reader.read(buffer);
	    	}
	    	writer.flush();
	    	return writer.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				reader.close();
				writer.close();
			} catch (IOException e) {}
		}
    }
    
	public static void main(String[] args) throws Exception {
		FileAdapter adapter = new FileAdapter(new File("/tmp/jms/inbox"));
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory)ctx.lookup("ConnectionFactory");
		Destination queue = (Destination)ctx.lookup("files-topic");
		Connection con = factory.createConnection();
		con.start();
        int polls = 12;
		while(polls > 0) {
			System.out.println("Checking for files... " + polls + " polls left.");
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(queue);
			List<File> files = adapter.loadFiles();
			if(!files.isEmpty()) {
				List<Message> messages = adapter.createMessages(session, files);
				adapter.send(messages, producer);
			}
			Thread.sleep(5000); // wait 5 seconds
			--polls;
		}
		con.close();
	}
}
