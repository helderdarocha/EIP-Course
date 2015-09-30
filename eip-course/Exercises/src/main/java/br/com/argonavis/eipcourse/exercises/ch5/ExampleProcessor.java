package br.com.argonavis.eipcourse.exercises.ch5;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ExampleProcessor<T> {
    public String process(TextMessage message) {
    	try {
			String xmlText = message.getText();
			
			// Inicialização do processador XML
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
			XPath xPath =  XPathFactory.newInstance().newXPath();
			
			// Extração do nome da classe, método e parâmetros
			String className = xPath.compile("/command/method/@class").evaluate(doc);
			String methodName = xPath.compile("/command/method/@name").evaluate(doc);
			NodeList nodes = (NodeList) xPath.compile("/command/method/params/*").evaluate(doc, XPathConstants.NODESET);

			// Obtenção de tipos e valores de parâmetros
			List<Class<T>> paramTypesList = new ArrayList<>();
            List<Object> paramValuesList = new ArrayList<>();
			for(int i = 0; i < nodes.getLength(); i++) {
            	Element param = (Element)nodes.item(i);
            	
            	Class<T> paramType = (Class<T>)Class.forName(param.getNodeName());
            	String paramStringValue = param.getTextContent();
            	T paramValue = paramType.getConstructor(new Class[] {String.class}).newInstance(paramStringValue);
            	
            	paramTypesList.add(paramType);
            	paramValuesList.add(paramValue);
            }
            Class<T>[] parameterTypes  = paramTypesList.toArray(new Class[paramTypesList.size()]);
            Object[] parameterValues = paramValuesList.toArray(new Object[paramValuesList.size()]);
            
            // Reflexão para obter classe e método
            Class<T> clazz = (Class<T>) Class.forName(className);
			Method method = clazz.getMethod(methodName, parameterTypes);
			
			Object result = null;
			// execucao
			if (Modifier.isStatic(method.getModifiers())) {
                 result = method.invoke(parameterValues);
			} else {
				result = method.invoke(clazz.newInstance(), parameterValues);
			}
			
			// Construção da resposta
			String returnType = method.getReturnType().getCanonicalName();
			return "<result><"+returnType+">"+result+"</"+returnType+"></result>";
			
		} catch (Exception e) {
			e.printStackTrace();
			return "<result><"+e.getClass()+">"+e+"</"+e.getClass()+"></result>";
		} 
    }
}
