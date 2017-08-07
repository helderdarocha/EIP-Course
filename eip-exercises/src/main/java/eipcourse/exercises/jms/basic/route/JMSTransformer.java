package eipcourse.exercises.jms.basic.route;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class JMSTransformer {

	public static void main(String[] args) throws Exception {
		Context ctx = new InitialContext();
		ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
		Destination caixaDeSaida   = (Destination) ctx.lookup("saida");
		Destination filaProcessamento = (Destination) ctx.lookup("msgxml");

		try (Connection con = factory.createConnection();
				Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);) {
			
			con.start();
			session.createConsumer(filaProcessamento).setMessageListener((m) -> {
				try {
					
					// transform
					String data = extractData((TextMessage)m, "/sorte"); // transforma: "<sorte>n</sorte>" -> "n"
					Message transformedMessage = session.createTextMessage(data);
					MessageProducer producer = session.createProducer(caixaDeSaida);
					producer.send(transformedMessage);
					System.out.println("Mensagem processada.");
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			System.out.println("30 segundos para processar mensagens. ");
			Thread.sleep(30000);
			System.out.println("Fim.");
		}
	}
	
	public static String extractData(TextMessage m, String xpath) throws XPathExpressionException, UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException, JMSException {
		Document doc = parseXML(m.getText());
		XPath xPathCompiler = XPathFactory.newInstance().newXPath();
		return xPathCompiler.compile(xpath).evaluate(doc);
	}
	
	private static Document parseXML(String xmlText) throws ParserConfigurationException, UnsupportedEncodingException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder(); 
		return db.parse(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
	}
}
