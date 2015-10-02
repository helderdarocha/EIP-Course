package br.com.argonavis.eipcourse.translator.filter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

public class XMLUtils {
	public static String nodeToString(Node node) throws TransformerException {
		StringWriter writer = new StringWriter();
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString().replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
	}
	
	public static String loadFile(String filename) throws IOException {
		try(BufferedReader br = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("xml/"+filename)))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    return sb.toString();
		}
	}
}
