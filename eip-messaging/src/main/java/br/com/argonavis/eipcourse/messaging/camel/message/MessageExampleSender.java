package br.com.argonavis.eipcourse.messaging.camel.message;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;

public class MessageExampleSender {

	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		// Camel
		CamelContext context = new DefaultCamelContext();

		// ActiveMQ - adiciona componente "jms" que pode ser usado em rotas (to/from)
		ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		// Message
		Exchange exchange = new DefaultExchange(context);
		Message message = exchange.getIn();

		message.setBody("<h1>Hello Camel!</h1>");
		message.setHeader("category", "greeting");
		message.setHeader("contentType", "text/html");

		ProducerTemplate template = context.createProducerTemplate();	
		template.send("jms:queue:test-queue", exchange); // usando nome do ActiveMQ (nao é o nome JNDI)

		context.start();

		System.out.println("O servidor está no ar por 60 segundos.");
		Thread.sleep(60000);
		context.stop();

	}

}
