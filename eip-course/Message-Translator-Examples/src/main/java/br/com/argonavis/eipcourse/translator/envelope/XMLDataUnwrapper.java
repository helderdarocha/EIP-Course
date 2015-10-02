package br.com.argonavis.eipcourse.translator.envelope;

import java.util.Base64;

public class XMLDataUnwrapper {
	public static String BEGIN_TAG = "<data>";
	public static String END_TAG   = "</data>";
	
	
    public byte[] unwrap(String xmlPayload) {
    	
    	System.out.println("String to process: " + xmlPayload);
    	
    	int begin = xmlPayload.indexOf(BEGIN_TAG) + BEGIN_TAG.length();
    	int end   = xmlPayload.indexOf(END_TAG);
    	
    	String base64String = xmlPayload.substring(begin, end);
    	System.out.println("base64 to decode: " + base64String);
    	
    	byte[] data = Base64.getDecoder().decode(base64String);
    	
    	System.out.println("decoded length: " + data.length);
    	
		return data;
    }
}
