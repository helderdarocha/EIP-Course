package br.com.argonavis.eipcourse.msg.event.push;

public class Observer {
	private String state;
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
    public void update(String newState) {
    	setState(newState);
    }
}
