package eipcourse.exercises.si.basic;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringIntegrationReceiver {
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		ClassPathXmlApplicationContext context = 
  			  new ClassPathXmlApplicationContext("/META-INF/spring/receiver.xml");
        context.start();
        
        System.out.println("Esperando...");
	}

}
