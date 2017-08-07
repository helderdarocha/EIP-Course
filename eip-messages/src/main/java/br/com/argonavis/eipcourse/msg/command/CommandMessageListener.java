package br.com.argonavis.eipcourse.msg.command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class CommandMessageListener implements MessageListener {

	public void onMessage(Message message) {
		
		try {
			String data = message.getStringProperty("Text-to-print");
			int copies = message.getIntProperty("Number-of-copies");
			String methodName = message.getStringProperty("Method-name");

			Method method = PrintingService.class.getMethod(methodName, String.class, Integer.class);
			method.invoke(data, copies);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
