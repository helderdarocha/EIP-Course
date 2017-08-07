package br.com.argonavis.eipcourse.messaging.camel.router;

import java.util.Random;

import javax.jms.ConnectionFactory;

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

		// ActiveMQ - adiciona componente "jms" que pode ser usado em rotas (to/from)
		ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		// Send some messages
		ProducerTemplate template = context.createProducerTemplate();	
		for(int i = 0; i < 10; i++) {
			Exchange exchange = new DefaultExchange(context);
			Message message = exchange.getIn();
			
			Random rand = new Random();
			boolean isEvil = rand.nextBoolean();
			if(isEvil) {
				message.setBody("I am mean!");
				message.setHeader("nature", "evil");
			} else {
				message.setBody("I am nice!");
				message.setHeader("nature", "good");
			}

			template.send("jms:queue:mixed-queue", exchange);
		}
		System.out.println("Mensagens enviadas");

		context.start();
		Thread.sleep(5000);
		context.stop();

	}

}
