package br.com.argonavis.eipcourse.messaging.si.translator;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;


public class TransformerExample {

	public static void main(String[] args) throws InterruptedException {
		
		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("/META-INF/spring/translator-example.xml");
		context.start();
		
		Message<String> message = 
				MessageBuilder
				.withPayload("This is a #message from @vader. Please click http://abc.xyz for #details.")
				.setHeader("sender", "chewbacca")
				.build();
		MessageChannel channel = context.getBean("inbound-channel", MessageChannel.class);
		channel.send(message);

	}

}
