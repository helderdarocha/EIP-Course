package br.com.argonavis.eipcourse.messaging.camel.translator;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class MessageReceiver {

	public static void main(String[] args) throws InterruptedException, Exception {
		CamelContext context = new DefaultCamelContext();
		ConnectionFactory cf = new ActiveMQConnectionFactory(MessageSender.ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:queue:outbound-queue") 
				.process(new Processor() {
					@Override
					public void process(Exchange ex) {
						System.out.println(ex.getIn().getBody(String.class));
					}
				});
			}
		});
		context.start();

		System.out.println("O servidor está no ar por 60 segundos.");
		Thread.sleep(60000);
		context.stop();

	}

}
