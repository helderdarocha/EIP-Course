package br.com.argonavis.eipcourse.router.aggregator.camel;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.apache.camel.processor.aggregate.UseLatestAggregationStrategy;

public class SolarSystemAggregator {
	
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		CamelContext context = new DefaultCamelContext();
		
		// ActiveMQ - adiciona componente "jms" que pode ser usado em rotas (to/from)
		ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
		context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

		class StringConcatenationStrategy implements AggregationStrategy {		 
		    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		        if (oldExchange == null) {
		            return newExchange;
		        }
		        String oldBody = oldExchange.getIn().getBody(String.class);
		        String newBody = newExchange.getIn().getBody(String.class);
		        oldExchange.getIn().setBody(oldBody + newBody);
		        return oldExchange;
		    }
		}
		
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				from("jms:queue:b-channel")
				.aggregate(header("JMSCorrelationID"), 
						   new StringConcatenationStrategy())
				  .completionSize(11)
				    .process(e->e.getIn().setBody("<result>"+e.getIn().getBody(String.class)+"</result>"))
				    .to("jms:queue:c-channel");
			}
        });

        context.start();
	}

}
