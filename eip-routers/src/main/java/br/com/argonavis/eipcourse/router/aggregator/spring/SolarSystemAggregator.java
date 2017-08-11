package br.com.argonavis.eipcourse.router.aggregator.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolarSystemAggregator {

	public static void main(String[] args) {
		new ClassPathXmlApplicationContext("/META-INF/spring/aggregator.xml");
	}

}
