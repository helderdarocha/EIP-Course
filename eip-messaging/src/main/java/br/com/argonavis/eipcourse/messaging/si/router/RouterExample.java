package br.com.argonavis.eipcourse.messaging.si.router;

import java.util.Random;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultExchange;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;


public class RouterExample {

	public static void main(String[] args) throws InterruptedException {
		
		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("/META-INF/spring/router-example.xml");
		context.start();
		
		MessageChannel channel = context.getBean("mixed-queue", MessageChannel.class);
		
		for(int i = 0; i < 10; i++) {
			Message<String> message = null;
			
			Random rand = new Random();
			boolean isEvil = rand.nextBoolean();
			if(isEvil) {
				message = MessageBuilder.withPayload("Evil message!")
						.setHeader("nature", "evil")
						.build();
			} else {
				message = MessageBuilder.withPayload("Nice message!")
						.setHeader("nature", "good")
						.build();
			}
			if(message != null) {
				channel.send(message);
			}
		}
		
		System.out.println("Mensagens enviadas");
		
		// Router está configurado no XML

	}

}
