package eipcourse.exercises.camel.basic.route;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class CamelTransformer implements Processor {

	@Override
	public void process(Exchange ex) throws Exception {
		String data = extractData(ex, "/sorte"); // transforma: "<sorte>n</sorte>" -> "n"
		ex.getIn().setBody(data);
		System.out.println("Mensagem processada:" +(ex.getIn().getBody()));
	}
	
	public String extractData(Exchange e, String xpath) throws XPathExpressionException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, JMSException {
		Document doc = parseXML(e.getIn().getBody(String.class));
		XPath xPathCompiler = XPathFactory.newInstance().newXPath();
		return xPathCompiler.compile(xpath).evaluate(doc);
	}
	
	private Document parseXML(String xmlText) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder(); 
		return db.parse(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
	}

}
