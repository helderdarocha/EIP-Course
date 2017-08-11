package br.com.argonavis.eipcourse.router.splitter.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolarSystemMessageSplitter {
	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("/META-INF/spring/splitter.xml");
	}
}
