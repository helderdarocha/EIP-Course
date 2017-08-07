package br.com.argonavis.eipcourse.router.splitter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SolarSystemSplitterProcessor {

	public List<String> split(String xmlText) {
		try {
			List<String> documents = new ArrayList<>();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder(); 
			Document doc = db.parse(new ByteArrayInputStream(xmlText.getBytes("UTF-8")));

			XPath xpath = XPathFactory.newInstance().newXPath();

			Node header = (Node) xpath.evaluate("//centro", doc,
					XPathConstants.NODE);
			NodeList nodes = (NodeList) xpath.evaluate("//orbita", doc,
					XPathConstants.NODESET);

			documents.add(XMLUtils.nodeToString(header));
			for (int i = 0; i < nodes.getLength(); i++) {
				documents.add(XMLUtils.nodeToString(nodes.item(i)));
			}

			return documents;
		} catch (SAXException | IOException | ParserConfigurationException | XPathExpressionException | TransformerException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		SolarSystemSplitterProcessor processor = new SolarSystemSplitterProcessor();
		String stringSource = XMLUtils.loadFile("sol.xml");
		List<String> result = processor.split(stringSource);
		for(String doc : result) {
			System.out.println("==============");
			System.out.println(doc);
		}
	}

}
