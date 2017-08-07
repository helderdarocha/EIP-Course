package br.com.argonavis.eipcourse.messaging.si.channel;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Veja as rotas no arquivo /META-INF/spring/channel-example.xml
 *
 */
public class ChannelExampleReceiver {

	public static void main(String[] args) throws InterruptedException {
		ApplicationContext context = 
				new ClassPathXmlApplicationContext("/META-INF/spring/channel-example.xml");
		
		System.out.println("O servidor está no ar por 60 segundos.");
        Thread.sleep(60000);
	}

}
