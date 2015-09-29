package br.com.argonavis.eipcourse.exercises.ch3.solution;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

public class JMSUtils {
	public static Map<String, Object> getMessageProperties(Message msg)
			throws JMSException {
		Map<String, Object> properties = new HashMap<>();
		Enumeration<?> srcProperties = msg.getPropertyNames();
		while (srcProperties.hasMoreElements()) {
			String propertyName = (String) srcProperties.nextElement();
			properties.put(propertyName, msg.getObjectProperty(propertyName));
		}
		return properties;
	}

	public static void setMessageProperties(Message msg, Map<String, Object> properties) throws JMSException {
		if (properties == null) {
			return;
		}
		for (Map.Entry<String, Object> entry : properties.entrySet()) {
			String propertyName = entry.getKey();
			Object value = entry.getValue();
			msg.setObjectProperty(propertyName, value);
		}
	}

	public static Object extractPayload(Message message) throws JMSException {
		if (message instanceof ObjectMessage) {
			return ((ObjectMessage) message).getObject();
		} else if (message instanceof TextMessage) {
			return ((TextMessage) message).getText();
		} else if (message instanceof BytesMessage) {
			BytesMessage bm = (BytesMessage) message;
			byte[] buffer = new byte[(int)bm.getBodyLength()];
			bm.readBytes(buffer);
			return buffer;
		} else {
			throw new UnsupportedOperationException("Payload type not supported");
		}
	}
	
	public static Message createMessageWithPayload(Session session, Object payload) throws JMSException {
		if (payload instanceof String) {
			return session.createTextMessage((String)payload);
		} else if (payload instanceof byte[]) {
			BytesMessage bytesMessage = session.createBytesMessage();
			bytesMessage.writeBytes((byte[])payload);
			return bytesMessage;
		} else if (payload instanceof Serializable){ // Object
			return session.createObjectMessage((Serializable)payload);
		} else {
			throw new UnsupportedOperationException("Message type not supported");
		}
	}
	
}
