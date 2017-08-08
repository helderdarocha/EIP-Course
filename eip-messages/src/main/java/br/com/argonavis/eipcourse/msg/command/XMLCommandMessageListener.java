package br.com.argonavis.eipcourse.msg.command;

import java.io.StringReader;
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
import org.xml.sax.InputSource;

public class XMLCommandMessageListener implements MessageListener {

	public void onMessage(Message message) {

		try {
			String xmlText = ((TextMessage) message).getText();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(xmlText));
			Document doc = db.parse(is);
			XPath xPath = XPathFactory.newInstance().newXPath();

			String className = xPath.compile("/command/method/@class").evaluate(doc);
			String methodName = xPath.compile("/command/method/@name").evaluate(doc);
			NodeList nodes = (NodeList) xPath.compile("/command/method/params/*").evaluate(doc,
					XPathConstants.NODESET);

			List<Class> parameterTypesList = new ArrayList<>();
			List<Object> parameterValuesList = new ArrayList<>();
			for (int i = 0; i < nodes.getLength(); i++) {
				Node node = (Node) nodes.item(i);
				Class parameterType = Class.forName(node.getNodeName());
				String parameterStringValue = node.getTextContent();
				Object parameterValue = parameterType.getConstructor(new Class[] { String.class })
						.newInstance(parameterStringValue);

				parameterTypesList.add(parameterType);
				parameterValuesList.add(parameterValue);
			}
			Class[] parameterTypes =parameterTypesList.toArray(new Class[parameterTypesList.size()]);
			Object[] parameterValues = parameterValuesList.toArray(new Object[parameterValuesList.size()]);
			
            Class clazz = Class.forName(className);
			Method method = clazz.getMethod(methodName, parameterTypes);
			method.invoke(clazz.newInstance(), parameterValues);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
