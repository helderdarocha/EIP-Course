package eipcourse.exercises.si.basic;

import java.util.Random;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.jms.AbstractJmsChannel;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

public class SpringIntegrationSender {
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = 
  			  new ClassPathXmlApplicationContext("/META-INF/spring/sender.xml");
        context.start();
        
        AbstractJmsChannel caixaDeEntrada = context.getBean("entrada", AbstractJmsChannel.class);
        
        Message<String> message = MessageBuilder.withPayload("Hello World!").build();
        caixaDeEntrada.send(message);
        
        for (int i = 0; i < 10; i++) {
			Random rand = new Random();
			int sorte = rand.nextInt(1000) + 1;
			int index = rand.nextInt(2);

			Message<String> m = null;

			if (index == 0) {
				m = MessageBuilder
						.withPayload("" + sorte)
						.setHeader("content-type", "text/plain")
						.build();
			} else {
				m = MessageBuilder
						.withPayload("<sorte>" + sorte + "</sorte>")
						.setHeader("content-type", "text/xml")
						.build();
			}
			caixaDeEntrada.send(m);
			System.out.println("Enviando: " + m);
		}

        context.stop();
	}

}
