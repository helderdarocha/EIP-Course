package lab3.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Application {

	public static void main(String[] args) {
		ApplicationContext ctx = new
				ClassPathXmlApplicationContext("integration-context.xml");

	}

}
