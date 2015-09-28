/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.cursocamel.router;

import br.com.argonavis.cursocamel.Configuration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class DynamicRouterExample {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory cf = new ActiveMQConnectionFactory(Configuration.ACTIVEMQ_URL);
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // Rota 1 – Do sistema de arquivos para a fila inbound-topic
                from("file:/tmp/jms/inbox")
                    .setHeader("Name", header("CamelFileNameOnly"))
                    .setHeader("Length", header("CamelFileNameOnly"))
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            String name = exchange.getIn().getHeader("Name", String.class);
                            String type = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
                            exchange.getIn().setHeader("Type", type);
                            
                            StringBuilder recipients = new StringBuilder();
                            
                            switch(type) {
                                case "png":
                                    recipients.append("jms:queue:dt-queue-1,");
                                    break;
                                case "xml":
                                    recipients.append("jms:queue:dt-queue-3,");
                                case "txt":
                                    recipients.append("jms:queue:dt-queue-2,");
                                    break;
                            }
                            recipients.append("jms:queue:all-queue");
                            exchange.getIn().setHeader("List", recipients.toString());
                        }
                    })
                    .to("jms:queue:inbound-queue");

                // Rota 2
                from("jms:queue:inbound-queue")
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            Message msg = exchange.getIn();
                            String list = msg.getHeader("List", String.class);
                            String filename = msg.getHeader("Name", String.class);

                            System.out.println("File " + filename + " redirected to: " + list);
                        }
                    })
                    .recipientList(header("List").tokenize(","));

            }
        });

        context.start();

        System.out.println("O servidor está no ar por 60 segundos.");
        Thread.sleep(60000);
        context.stop();
    }
}
