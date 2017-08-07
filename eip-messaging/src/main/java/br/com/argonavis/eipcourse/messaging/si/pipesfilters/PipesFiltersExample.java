package br.com.argonavis.eipcourse.messaging.si.pipesfilters;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;


public class PipesFiltersExample {

	public static void main(String[] args) throws InterruptedException {
		
		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("/META-INF/spring/pipesfilters-example.xml");
		context.start();
		
		Message<String> message = MessageBuilder.withPayload("Hello Spring")
				.setHeader("category", "greeting")
				.setHeader("contentType", "text/plain")
				.build();
		MessageChannel channel = context.getBean("inbound-channel", MessageChannel.class);
		channel.send(message);
		
		// Filtro e Receiver está configurado no XML

	}

}
