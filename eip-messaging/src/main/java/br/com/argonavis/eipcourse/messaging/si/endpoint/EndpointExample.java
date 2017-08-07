package br.com.argonavis.eipcourse.messaging.si.endpoint;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;


public class EndpointExample {

	public static void main(String[] args) throws InterruptedException {
		
		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("/META-INF/spring/endpoint-example.xml");
		context.start();
		
		Thread.sleep(60000);
		
		System.out.println("Encerrando.");
		context.stop();
	}

}
