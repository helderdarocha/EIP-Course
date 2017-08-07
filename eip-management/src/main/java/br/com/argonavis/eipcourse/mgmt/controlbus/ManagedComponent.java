package br.com.argonavis.eipcourse.mgmt.controlbus;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;

public interface ManagedComponent {
	
	public void setManaged(Connection con, Destination controlTopic) throws JMSException;
	public void unsetManaged();
	public boolean isManaged();

}
