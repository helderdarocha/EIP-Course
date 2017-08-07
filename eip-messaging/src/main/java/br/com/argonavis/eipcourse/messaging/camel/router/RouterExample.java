/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.eipcourse.messaging.camel.router;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

/**
 *
 * @author helderdarocha
 */
public class RouterExample {
	
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jms:queue:mixed-queue")
                .process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Nature of message received: " + exchange.getIn().getHeader("nature"));
                    }
                })
                .choice()
	                  .when(header("nature").isEqualTo("evil"))
	                       .to("jms:queue:evil-queue")
	                  .when(header("nature").isEqualTo("good"))
	                       .to("jms:queue:good-queue")
	                  .otherwise()
	                       .to("jms:queue:invalid-queue")
	                       .stop() // garante que inválidos não sigam adiante
                .end(); // fim do choice
        
                from("jms:queue:evil-queue").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Mensagem do mal: " + exchange.getIn().getBody(String.class));
                    }
                });
                from("jms:queue:good-queue").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Mensagem do bem: " + exchange.getIn().getBody(String.class));
                    }
                });
            }
        });

        context.start();

        System.out.println("O servidor está no ar por 60 segundos.");
        Thread.sleep(60000);
        context.stop();
    }
}
