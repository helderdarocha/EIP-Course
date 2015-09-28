/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.cursocamel;

import br.com.argonavis.cursocamel.routes.FtpToJmsRoute;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * É preciso antes instalar o ActiveMQ e iniciá-lo (activemq start) 
 * antes de rodar este programa. Use o Console do ActiveMQ: http://localhost:8161/admin
 * para ver a fila ser criada e mensagens serem adicionadas.
 * 
 * É preciso antes instalar um serviço FTP e configurar sua localização
 * no arquivo Configuration.java (senha, usuario e caminho) que será usado
 * para compor a FTP_URL, antes de rodar este programa. 
 * 
 * É preciso incluir o módulo FTP do Camel no projeto.
 */
public class MoveFtpToJmsService {

    public static void main(String[] args) throws Exception {
        
        CamelContext context = new DefaultCamelContext();
        
        // Connexao com o ActiveMQ
        ConnectionFactory cf = new ActiveMQConnectionFactory(Configuration.ACTIVEMQ_URL);
        context.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(cf));
        
        RouteBuilder routeBuilder = new FtpToJmsRoute();
        context.addRoutes(routeBuilder);
        context.start();

        System.out.println("O servidor está no ar. Ponha arquivos em "
                + Configuration.FTP_PATH
                + " e veja eles serem copiados para uma fila JMS"
                + ". O servidor ficará no ar por 20 segundos.");
        Thread.sleep(20000);
        context.stop();
    }
}
