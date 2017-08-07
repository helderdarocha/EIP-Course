package br.com.argonavis.eipcourse.channel.pubsub.spring;

public class Worker {
	private String name;
	public Worker(String name) {
		this.name = name;
	}
	public void doWork(Object data) {
		System.out.println(name + " working on " + data);
	}
}
