package eipcourse.exercises.camel.basic.route;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CamelSpringRoute {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = 
	  			  new ClassPathXmlApplicationContext("/META-INF/camel/context.xml");
	    context.start();
	    System.out.println("Esperando...");
	}
}
