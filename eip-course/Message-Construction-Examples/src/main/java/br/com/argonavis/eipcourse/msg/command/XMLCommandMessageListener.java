package br.com.argonavis.eipcourse.msg.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLCommandMessageListener<T> implements MessageListener {

	public void onMessage(Message message) {
		
		try {
			String xmlText = ((TextMessage)message).getText();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(xmlText);
			XPath xPath =  XPathFactory.newInstance().newXPath();
			
			String methodName = xPath.compile("/command/method/name").evaluate(doc);
			NodeList nodes = (NodeList) xPath.compile("/command/method/parameters/*").evaluate(doc, XPathConstants.NODESET);
            
			List<Class> parameterTypesList = new ArrayList<>();
            List<Object> parameterValuesList = new ArrayList<>();
			for(int i = 0; i < nodes.getLength(); i++) {
            	Node node = (Node)nodes.item(i);
            	Class<T> parameterType = (Class<T>)Class.forName(node.getLocalName());
            	String parameterStringValue = node.getTextContent();
            	T parameterValue = parameterType.getConstructor(new Class[] {String.class}).newInstance(parameterStringValue);
            	
            	parameterTypesList.add(parameterType);
            	parameterValuesList.add(parameterValue);
            }
            Class[] parameterTypes  = parameterTypesList.toArray(new Class[parameterTypesList.size()]);
            Class[] parameterValues = parameterValuesList.toArray(new Class[parameterTypesList.size()]);

			Method method = PrintingService.class.getMethod(methodName, parameterTypes);
			method.invoke(parameterValues);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

}
