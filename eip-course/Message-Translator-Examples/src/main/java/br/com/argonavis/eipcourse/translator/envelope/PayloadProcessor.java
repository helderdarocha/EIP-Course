package br.com.argonavis.eipcourse.translator.envelope;


public class PayloadProcessor {

	public Object process(Object payload) {
		System.out.println("Processing data.");
		return payload;
	}

}