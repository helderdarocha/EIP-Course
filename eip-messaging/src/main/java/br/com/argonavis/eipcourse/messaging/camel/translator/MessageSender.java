package br.com.argonavis.eipcourse.messaging.camel.translator;

import java.util.Random;

import javax.jms.ConnectionFactory;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;

public class MessageSender {

	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		// Camel
		CamelContext context = new DefaultCamelContext();

		ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		// Send some messages
		ProducerTemplate template = context.createProducerTemplate();
		Exchange exchange = new DefaultExchange(context);
		Message message = exchange.getIn();

		message.setBody("This is a #message from @vader. Please click http://abc.xyz for #details.");
		message.setHeader("sender", "chewbacca");

		template.send("jms:queue:inbound-queue", exchange);

		System.out.println("Mensagens enviadas");

		context.start();
		Thread.sleep(5000);
		context.stop();

	}

}
