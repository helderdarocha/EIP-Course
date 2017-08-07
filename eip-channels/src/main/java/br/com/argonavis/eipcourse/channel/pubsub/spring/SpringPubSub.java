package br.com.argonavis.eipcourse.channel.pubsub.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringPubSub {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = 
				new ClassPathXmlApplicationContext("/META-INF/spring/pubsub.xml");
		
		System.out.println("O servidor está no ar por 60 segundos.");
        Thread.sleep(60000);
	}

}
