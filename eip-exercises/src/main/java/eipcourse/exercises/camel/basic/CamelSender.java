package eipcourse.exercises.camel.basic;

import java.util.Random;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;

public class CamelSender {
	
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) {
		CamelContext context = new DefaultCamelContext();
		
		// Necessário para usar filas JMS
	    ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
	    context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));
		
		JmsEndpoint caixaDeEntrada = (JmsEndpoint) context.getEndpoint("jms:queue:entrada");
		ProducerTemplate producer = context.createProducerTemplate();

		Exchange exchange = new DefaultExchange(context);
		exchange.getIn().setBody("Hello World!");
		producer.send(caixaDeEntrada, exchange);

		for (int i = 0; i < 10; i++) {
			Random rand = new Random();
			int sorte = rand.nextInt(1000) + 1;
			int index = rand.nextInt(2);

			Exchange e = new DefaultExchange(context);

			if (index == 0) {
				e.getIn().setHeader("content-type", "text/plain");
				e.getIn().setBody("" + sorte);
			} else {
				e.getIn().setHeader("content-type", "text/xml");
				e.getIn().setBody("<sorte>" + sorte + "</sorte>");
			}
			producer.send(caixaDeEntrada, e);
		}

		System.out.println("Mensagens enviadas!");

	}
}
