package br.com.argonavis.cursocamel;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author helderdarocha
 */
public class RecipientList {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        // Connexao com o ActiveMQ
        ConnectionFactory cf = new ActiveMQConnectionFactory(Configuration.ACTIVEMQ_URL);
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));

        context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                // load file orders from src/data into the JMS queue
                from(Configuration.OUTBOX).to("jms:objetos");

                // content-based router
                from("jms:objetos")
                    .choice()
                        .when(header("CamelFileName").regex("^.*(jpg|png|gif|jpeg)$"))
                          .to("jms:imagens")
                        .when(header("CamelFileName").endsWith(".xml"))
                          .to("jms:docsXML")
                        .otherwise()
                          .to("jms:invalidos")
                          .stop() // estes objetos não seguem para próxima etapa
                    .end() // fim do choice
                        .to("jms:nextStep"); // proxima etapa (contem todos que não chamam stop())

                from("jms:docsXML")
                    .setHeader("Namespace", xpath("//*[local-name() = 'project']/namespace::*[local-name() = '']")) // cria um cabeçalho
                        .process( (Exchange exchange) -> {
                            String ns = exchange.getIn().getHeader("Namespace", String.class);
                            System.out.println("ns = " + ns);
                            if(ns.indexOf("maven.apache.org/POM") > 0) {
                                System.out.println("É POM do Maven!");
                                exchange.getIn().setHeader("Destinatarios", "jms:poms");
                            } else {
                                System.out.println("Não é POM do Maven!");
                            }
                        })
                        .recipientList(header("Destinatarios"));

                from("jms:invalidos")
                        .process( (Exchange exchange) -> {
                            System.out.println("Arquivo inválido: " + exchange.getIn().getHeader("CamelFileName"));
                        });

                from("jms:nextStep")
                        .process( (Exchange exchange) -> {
                            System.out.println("Arquivo copiado para próxima etapa: " + exchange.getIn().getHeader("CamelFileName"));
                        });
                
                from("jms:poms")
                        .process( (Exchange exchange) -> {
                            System.out.println("Adicionado a lista de POMs: " + exchange.getIn().getHeader("CamelFileName"));
                        });
                
            }

        });
        context.start();

        System.out.println("O servidor está no ar. Ponha arquivos em "
                + Configuration.OUTBOX
                + " para iniciar o processo."
                + "O servidor ficará no ar por 20 segundos.");
        Thread.sleep(20000);
        context.stop();
    }
}
