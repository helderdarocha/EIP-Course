package lab1;

import java.io.ByteArrayInputStream;

import javax.jms.ConnectionFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.w3c.dom.Document;

public class CamelRoute {
	
	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.start();
		
		// 1) Cria a mensagem
		Exchange exchange1 = new DefaultExchange(context);
		Message message1 = exchange1.getIn();
		message1.setBody("<weird>Mensagem estranha</weird>");
		message1.setHeader("tipo", "incomum");
		
		Exchange exchange2 = new DefaultExchange(context);
		Message message2 = exchange2.getIn();
		message2.setBody("Mensagem comum.");
		message2.setHeader("tipo", "comum");
		
		ProducerTemplate template = context.createProducerTemplate();
		template.send("seda:entrada", exchange1);
		template.send("seda:entrada", exchange2);

        context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				// 2) Define a rota
				from("seda:entrada")
				.process((e)->System.out.println("entrada: "+e.getIn().getBody()))
				.choice()
					.when(header("tipo").isEqualTo("comum"))
						.to("seda:saida")
					.when(header("tipo").isEqualTo("incomum"))
						.to("seda:processamento")
					.otherwise()
						.to("direct:invalida")
				.end();
				
				from("seda:processamento")
				.process((e)->System.out.println("processamento: "+e.getIn().getBody()))
				.process((e)->{

				})
				.process((e)->System.out.println("filtrada: "+e.getIn().getBody()))
				.to("seda:saida");
				
				from("seda:saida")
				.process((e)->System.out.println("resultado: "+e.getIn().getBody()));
			}
        });
        
        

	}

}
