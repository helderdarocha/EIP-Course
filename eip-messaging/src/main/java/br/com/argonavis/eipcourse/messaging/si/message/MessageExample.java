package br.com.argonavis.eipcourse.messaging.si.message;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;


public class MessageExample {

	public static void main(String[] args) throws InterruptedException {
		
		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("/META-INF/spring/message-example.xml");
		context.start();
		
		Message<String> message = MessageBuilder.withPayload("Hello Spring")
				.setHeader("category", "greeting")
				.setHeader("contentType", "text/plain")
				.build();
		MessageChannel channel = context.getBean("jms-example", MessageChannel.class);
		channel.send(message);
		
		// Receiver está configurado no XML
		// Veja exemplo de processamento em eipcourse.messaging.si.translator

	}

}
