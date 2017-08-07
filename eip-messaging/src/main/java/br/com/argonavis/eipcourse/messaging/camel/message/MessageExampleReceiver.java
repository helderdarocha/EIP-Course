package br.com.argonavis.eipcourse.messaging.camel.message;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class MessageExampleReceiver {

	public static void main(String[] args) throws InterruptedException, Exception {
		CamelContext context = new DefaultCamelContext();
		ConnectionFactory cf = new ActiveMQConnectionFactory(MessageExampleSender.ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:queue:test-queue") // queue.simple-p2p-channel = test-queue
						.process(new Processor() {
							@Override
							public void process(Exchange ex) {
								String category = ex.getIn().getHeader("category", String.class);
								String content = ex.getIn().getHeader("contentType", String.class);
								String contents = ex.getIn().getBody(String.class);

								System.out.println("Category: " + category);
								System.out.println("Content type: " + content);
								System.out.println("Contents: " + contents);
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
