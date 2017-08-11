package br.com.argonavis.eipcourse.router.aggregator.spring;

import java.util.List;

public class CompletionStrategy {
	public boolean isComplete(List<String> payloads) {
		if(payloads.size() >= 11) {
			return true;
		} else {
			return false;
		}
	}
}
