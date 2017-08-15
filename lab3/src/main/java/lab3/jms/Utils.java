package lab3.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * 
 * @author https://stackoverflow.com/questions/3736604/copy-jms-message-properties
 *
 */
public class Utils {

	public static void copyProperties(Message message, TextMessage newMessage) throws JMSException {
		setMessageStringProperties(newMessage, getMessageStringProperties(message));
	}
	
	public static Map<String, String> getMessageStringProperties(Message msg) throws JMSException {
	   Map<String, String> properties = new HashMap<>();
	   Enumeration<String> srcProperties = msg.getPropertyNames();
	   while (srcProperties.hasMoreElements()) {
	       String propertyName = srcProperties.nextElement();
	       properties.put(propertyName, msg.getStringProperty (propertyName));
	   }
	   return properties;
	}

	public static void setMessageStringProperties(Message msg, Map<String, String> properties) throws JMSException {
	    if (properties == null) {
	        return;
	    }
	    for (Map.Entry<String, String> entry : properties.entrySet()) {
	        String propertyName = entry.getKey();
	        String value = entry.getValue();
	        msg.setStringProperty(propertyName, value);
	    }
	}
}
