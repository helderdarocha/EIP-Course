package br.com.argonavis.eipcourse.endpoint.dispatcher;

import java.util.concurrent.Executor;

import javax.jms.Message;

public abstract class MessageProcessor {
	
	private Message message;
	
	public MessageProcessor(Message message) {
		this.message = message;
	}
	
    abstract void process();

    public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void run(Executor thread) {
		thread.execute(new Runnable() {
			public void run() {
				process();
			}
		});
	}
}
