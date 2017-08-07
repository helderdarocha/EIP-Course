package eipcourse.exercises.si.basic.route;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringIntegrationRoute {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = 
	  			  new ClassPathXmlApplicationContext("/META-INF/spring/context.xml");
	    context.start();
	    System.out.println("Esperando...");
	}
}
