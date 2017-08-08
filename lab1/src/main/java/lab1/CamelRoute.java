package lab1;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class CamelRoute {
	
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		// Connexao com o ActiveMQ
        ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));
        
        context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:queue:good-queue")
				.to("direct:a");
				
				from("direct:a")
				.process(new Processor() {
					@Override
					public void process(Exchange e) throws Exception {
						System.out.println("Conteudo: " + e.getIn().getBody());
					}
				})
				.to("jms:queue:fila1");
				
				
			}
        });
        
        context.start();

	}

}
