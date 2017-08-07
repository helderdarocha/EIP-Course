package br.com.argonavis.eipcourse.messaging.camel.pipesfilters;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;

public class NoPipesFiltersExample {

	public static void main(String[] args) throws Exception {
		
		CamelContext context = new DefaultCamelContext();

		ConnectionFactory cf = new ActiveMQConnectionFactory(PipesFiltersExample.ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		// Sem usar Pipes and Filters (da fila direto para a saída, com processador local)
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("jms:queue:inbound-queue").process(new Processor() {
					@Override
					public void process(Exchange ex) {
						Message in = ex.getIn();
						in.setBody(in.getBody(String.class).toUpperCase());
					}
				})
				.to("stream:out");
			}
		});
				
		context.start();
		
		// Trecho 1 Sender: Sender -> inbound-queue
		Exchange exchange = new DefaultExchange(context);
		Message message = exchange.getIn();

		message.setBody("<h1>Hello Camel!</h1>");
		message.setHeader("category", "greeting");
		message.setHeader("contentType", "text/html");
		
		ProducerTemplate template = context.createProducerTemplate();
		template.send("jms:queue:inbound-queue", exchange); // usando nome do ActiveMQ (nao é o nome JNDI)



		Thread.sleep(5000);
		context.stop();

	}

}
