package br.com.argonavis.eipcourse.channel.p2p.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringP2P {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = 
				new ClassPathXmlApplicationContext("/META-INF/spring/p2p.xml");

		System.out.println("O serviço está no ar.");
	}

}
