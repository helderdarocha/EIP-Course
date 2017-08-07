/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.eipcourse.messaging.camel.translator;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

/**
 *
 * @author helderdarocha
 */
public class TransformerExample {
	
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:queue:inbound-queue")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        Message in = exchange.getIn();
                        String sender = in.getHeader("sender", String.class);
                        String payload = in.getBody(String.class);
                        String newBody = new TranslateTweetToHtml().translate(sender,  payload);
                        in.setBody(newBody);
                    }
                })
               .to("jms:queue:outbound-queue");
            }
        });

        context.start();

        System.out.println("O servidor está no ar por 60 segundos.");
        Thread.sleep(60000);
        context.stop();
    }
}
