package br.com.argonavis.eipcourse.router.aggregator.spring;

import java.util.List;

public class StringConcatenationStrategy {

	public String add(List<String> payloads) {
		StringBuilder payloadBuilder = new StringBuilder();
		for (String fragment : payloads) {
			payloadBuilder.append(fragment);
		}
		return payloadBuilder.toString();
	}
}
