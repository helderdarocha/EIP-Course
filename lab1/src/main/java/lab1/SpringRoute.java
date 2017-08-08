package lab1;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

public class SpringRoute {

	public static void main(String[] args) {
		ApplicationContext ctx = new
				ClassPathXmlApplicationContext("context.xml");
		
		BeanTeste bean = (BeanTeste)ctx.getBean("teste");
		bean.teste();
		
		MessageChannel channel = (MessageChannel) ctx.getBean("entrada");
		
		Message<String> message1 = MessageBuilder
				                 .withPayload("Mensagem comum.")
				                 .setHeader("tipo", "comum")
				                 .build();
		channel.send(message1);
		
		Message<String> message2 = MessageBuilder
                .withPayload("<weird>Mensagem estranha.</weird>")
                .setHeader("tipo", "incomum")
                .build();
        channel.send(message2);

	}

}
