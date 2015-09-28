/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.argonavis.cursocamel;

import br.com.argonavis.cursocamel.routes.FileToFtpRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * É preciso antes instalar um serviço FTP e configurar sua localização
 * no arquivo Configuration.java (senha, usuario e caminho) que será usado
 * para compor a FTP_URL, antes de rodar este programa. 
 * É preciso incluir o módulo FTP do Camel no projeto.
 */
public class MoveFileToFtpService {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        RouteBuilder routeBuilder = new FileToFtpRoute();
        context.addRoutes(routeBuilder);
        context.start();

        System.out.println("O servidor está no ar. Ponha arquivos em "
                + Configuration.OUTBOX
                + " e veja eles serem movidos para uma pasta servida pelo FTP"
                + ". O servidor ficará no ar por 20 segundos.");
        Thread.sleep(20000);
        context.stop();
    }
    
}
