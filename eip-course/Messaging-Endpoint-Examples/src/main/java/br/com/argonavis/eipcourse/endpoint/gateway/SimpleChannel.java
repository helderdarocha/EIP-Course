package br.com.argonavis.eipcourse.endpoint.gateway;

public class SimpleChannel {
    private String name;
    
    public SimpleChannel(String name) {
    	this.name = name;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
