package br.com.argonavis.eipcourse.translator.claimcheck.camel;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class FileRouter {

	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		// ActiveMQ - adiciona componente "jms" que pode ser usado em rotas (to/from)
		ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("file:///tmp/jms/inbox")
				//.setHeader("Name", header("CamelFileNameOnly"))
               // .setHeader("Length", header("CamelFileLength"))
		//		.wireTap("file:///tmp/jms/wiretap")
				//.process(e->{ // check in
				//	e.getIn().setBody("", String.class);
				//})
		//		.wireTap("jms:queue:wiretap")
				//.choice()	
				//	.when(header("Length").isGreaterThan(2000))
				//	  .process(e->{ // check out
				//		  from("file:///tmp/jms/wiretap/"+e.getIn().getHeader("CamelFileNameOnly"))
				//		  .to("file:///tmp/jms/outbox/large/"+e.getIn().getHeader("CamelFileNameOnly"));
				//	  });
				.to("file:///tmp/jms/outbox");
			}
        });

        context.start();
	}
}
