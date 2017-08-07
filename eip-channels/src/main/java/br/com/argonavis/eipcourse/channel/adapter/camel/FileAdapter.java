/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.eipcourse.channel.adapter.camel;

import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class FileAdapter {
	
	public static String ACTIVEMQ_URL = "tcp://localhost:61616";

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        
        ConnectionFactory cf = new ActiveMQConnectionFactory(ACTIVEMQ_URL);
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));
        
        context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                this.from("file:///tmp/jms/inbox") 
                    .process((e)->System.out.println("Copiando " + e.getIn().getHeader("CamelFileName")))
                    .to("jms:queue:inbound-queue");
            }
        });
        context.start();

        System.out.println("O servidor está no ar. Ponha arquivos em "
                + "file:///tmp/jms/inbox"
                + " e veja eles serem copiados para uma fila JMS"
                + ". O servidor ficará no ar por 20 segundos.");
        Thread.sleep(20000);
        context.stop();
    }

}
