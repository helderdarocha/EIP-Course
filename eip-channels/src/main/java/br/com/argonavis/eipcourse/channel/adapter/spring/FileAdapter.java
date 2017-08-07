/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.argonavis.eipcourse.channel.adapter.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FileAdapter {
	
	public static void main(String[] args) throws Exception {
		new ClassPathXmlApplicationContext("/META-INF/spring/channel-adapter.xml").start();

		System.out.println("O serviço está no ar.");
    }

}
