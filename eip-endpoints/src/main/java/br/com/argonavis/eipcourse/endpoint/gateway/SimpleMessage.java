package br.com.argonavis.eipcourse.endpoint.gateway;

import java.util.HashMap;
import java.util.Map;

public class SimpleMessage {
    private Map<String, String> headers = new HashMap<>();
    private String payload;
    
    public SimpleMessage(Map<String, String> headers, String payload) {
    	this.headers = headers;
    	this.payload = payload;
    }
    
    public SimpleMessage(String payload) {
    	this.payload = payload;
    }
    
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	public String getHeader(String key) {
		return headers.get(key);
	}
	
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}

	@Override
	public String toString() {
		return "SimpleMessage [headers=" + headers + ", payload=" + payload + "]";
	}
}
