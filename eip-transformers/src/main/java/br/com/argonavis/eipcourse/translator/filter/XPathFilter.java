package br.com.argonavis.eipcourse.translator.filter;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XPathFilter {
	
	DocumentBuilderFactory dbf;
    DocumentBuilder db; 
	XPath xpath;
	
	public XPathFilter() throws Exception {
		init();
	}
	
	public void init() throws Exception {
		dbf = DocumentBuilderFactory.newInstance();
        db = dbf.newDocumentBuilder(); 
	    xpath = XPathFactory.newInstance().newXPath();
	}

	public String removeContents(String expr, String xmlText) throws Exception {
		Document doc = db.parse(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
		Node node = (Node) xpath.evaluate(expr, doc, XPathConstants.NODE);
		node.setTextContent(""); // empty
        return XMLUtils.nodeToString(doc);
	}

	public String extractText(String expr, String xmlText) throws Exception {
		Document doc = db.parse(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
		String result = (String) xpath.evaluate(expr, doc, XPathConstants.STRING);
        return result;
	}
	
	public String extractNode(String expr, String xmlText) throws Exception {
		Document doc = db.parse(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));
		Node node = (Node) xpath.evaluate(expr, doc, XPathConstants.NODE);
        return XMLUtils.nodeToString(node);
	}
}
