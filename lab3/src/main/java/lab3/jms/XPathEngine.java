package lab3.jms;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPathEngine {
	DocumentBuilderFactory dbf;
    DocumentBuilder db; 
	XPath xpath;
	
	private XPathEngine() throws Exception {
		dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder(); 
	    xpath = XPathFactory.newInstance().newXPath();
	}
	public static XPathEngine init() throws Exception {
		return new XPathEngine();
	}

	public String extractText(String expr, String xmlText) throws Exception {
		Document doc = db.parse(new InputSource(new StringReader(xmlText)));
		String result = (String) xpath.evaluate(expr, doc, XPathConstants.STRING);
        return result;
	}
	
	public Node extractNode(String expr, String xmlText) throws Exception {
		Document doc = db.parse(new InputSource(new StringReader(xmlText)));
		Node node = (Node) xpath.evaluate(expr, doc, XPathConstants.NODE);
		return node;
     }
	
	public NodeList extractNodeSet(String expr, String xmlText) throws Exception {
		Document doc = db.parse(new InputSource(new StringReader(xmlText)));
		NodeList nodeSet = (NodeList) xpath.evaluate(expr, doc, XPathConstants.NODESET);
		return nodeSet;
     }
	
	public List<String> extractNodeSetAsString(String expr, String xmlText) throws Exception {
		return convertNodeListToStringArray(extractNodeSet(expr, xmlText));
	}
	
	public List<String> convertNodeListToStringArray(NodeList nodeSet) throws TransformerException {
		List<String> result = new ArrayList<String>();
		for(int i = 0; i < nodeSet.getLength(); i++) {
			Node node = nodeSet.item(i);
			result.add(nodeToString(node));
		}
		return result;
	}
	
	public static String nodeToString(Node node) throws TransformerException {
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString().replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
	}

}
