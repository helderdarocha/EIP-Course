package br.com.argonavis.eipcourse.translator.envelope;

import java.util.Base64;

public class XMLDataWrapper {
	public String wrap(byte[] data) {
		String base64String = Base64.getEncoder().encodeToString(data);
		String xml = "<data>" + base64String + "</data>";
		
		System.out.println("Encoded data: " + xml);
		
		return xml;
	}
}
