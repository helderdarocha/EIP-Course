/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.cursocamel.components;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class LoggingProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        System.out.println("Copiando " + exchange.getIn().getHeader("CamelFileName"));
    }

}
