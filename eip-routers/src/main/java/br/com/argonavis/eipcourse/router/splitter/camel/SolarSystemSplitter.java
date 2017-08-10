package br.com.argonavis.eipcourse.router.splitter.camel;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class SolarSystemSplitter {
	
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		// ActiveMQ - adiciona componente "jms" que pode ser usado em rotas (to/from)
		ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:queue:a-channel")
				.split(xpath("(//orbita | //centro)"))
				.convertBodyTo(String.class)
				.to("jms:queue:b-channel");
			}
        });

        context.start();
	}

}
