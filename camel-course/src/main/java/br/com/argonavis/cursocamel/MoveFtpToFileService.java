/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.argonavis.cursocamel;

import br.com.argonavis.cursocamel.routes.FtpToFileRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * É preciso antes instalar um serviço FTP e configurar sua localização
 * no arquivo Configuration.java (senha, usuario e caminho) que será usado
 * para compor a FTP_URL, antes de rodar este programa. 
 * É preciso incluir o módulo FTP do Camel no projeto.
 */
public class MoveFtpToFileService {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();

        RouteBuilder routeBuilder = new FtpToFileRoute();
        context.addRoutes(routeBuilder);
        context.start();

        System.out.println("O servidor está no ar. Ponha arquivos em "
                + " uma pasta servida pelo FTP"
                + " e veja eles serem copiados para "+Configuration.INBOX
                + ". O servidor ficará no ar por 20 segundos.");
        Thread.sleep(20000);
        context.stop();
    }
}
