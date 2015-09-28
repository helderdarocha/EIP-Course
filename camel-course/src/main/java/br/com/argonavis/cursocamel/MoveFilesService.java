/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.argonavis.cursocamel;

import br.com.argonavis.cursocamel.routes.MoveFileRoute;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 *
 * @author helderdarocha
 */
public class MoveFilesService {

    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        
        RouteBuilder routeBuilder = new MoveFileRoute();
        context.addRoutes(routeBuilder);
        context.start();
        
        System.out.println("O servidor está no ar. Ponha arquivos em "
                +Configuration.INBOX
                +" para que o sistema os mova para "
                +Configuration.OUTBOX
                + ". O servidor ficará no ar por 20 segundos.");
        Thread.sleep(20000);
        context.stop();
    }

}
