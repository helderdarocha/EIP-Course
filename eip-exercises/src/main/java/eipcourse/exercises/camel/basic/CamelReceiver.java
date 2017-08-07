package eipcourse.exercises.camel.basic;

import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.PollingConsumer;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelReceiver {
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		// Necessário para usar filas JMS
	    ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
	    context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));
		
		JmsEndpoint caixaDeEntrada = (JmsEndpoint) context.getEndpoint("jms:queue:entrada");
		PollingConsumer consumer = caixaDeEntrada.createPollingConsumer();
		
		receive(consumer);
	}
	
	public static void receive(PollingConsumer consumer) {
		Exchange exchange = consumer.receive();
		System.out.println("Type: " + exchange.getIn().getHeader("content-type"));
		System.out.println("Contents: " +exchange.getIn().getBody());
		receive(consumer);
	}
}
