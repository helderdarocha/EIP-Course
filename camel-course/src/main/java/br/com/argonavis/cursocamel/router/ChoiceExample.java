/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.cursocamel.router;

import br.com.argonavis.cursocamel.channel.*;
import br.com.argonavis.cursocamel.Configuration;
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
public class ChoiceExample {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        ConnectionFactory cf = new ActiveMQConnectionFactory(Configuration.ACTIVEMQ_URL);
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                // Rota 1 – Do sistema de arquivos para a fila inbound-queue
                from("file:/tmp/jms/inbox")
                    .setHeader("Name", header("CamelFileNameOnly"))
                    .setHeader("Length", header("CamelFileNameOnly"))
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            String name =  exchange.getIn().getHeader("Name", String.class);
                            String type = name.substring(name.lastIndexOf('.')+1).toLowerCase();
                            exchange.getIn().setHeader("Type", type);
                        }
                    })
                    .to("jms:queue:inbound-queue");

                // Rota 2 – Da fila inbound-queue para as dt-queues passando pelo CBR
                from("jms:queue:inbound-queue")
                        .process(new Processor() {
                            public void process(Exchange exchange) throws Exception {
                                System.out.println("Received message: "
                                        + exchange.getIn().getHeader("Type"));
                            }
                        })
                        .choice()
                          .when(header("Name").endsWith(".png"))
                            .to("jms:queue:dt-queue-1")
                          .when(header("Name").endsWith(".txt"))
                            .to("jms:queue:dt-queue-2")
                          .when(header("Name").endsWith(".xml"))
                            .to("jms:queue:dt-queue-3")
                          .otherwise()
                            .to("jms:queue:invalid-queue")
                            .stop() // boa prática – garante que inválidos não prossigam
                        .end(); // fim do choice
        /*
                // Rotas adicionais (processadores):
                from("jms:dt-queue-1").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("Processing PNG..." + exchange.getIn().getHeader("Name"));
                    }
                });

                from("jms:dt-queue-2").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("\nProcessing TXT... " 
                                + exchange.getIn().getHeader("Name") + "\n"
                                + exchange.getIn().getBody(String.class));
                    }
                });

                from("jms:dt-queue-3").process(new Processor() {
                    public void process(Exchange exchange) throws Exception {
                        System.out.println("\nProcessing XML... " + exchange.getIn().getHeader("Name"));
                    }
                });
        */

            }
        });

        context.start();

        System.out.println("O servidor está no ar por 60 segundos.");
        Thread.sleep(60000);
        context.stop();
    }
}
