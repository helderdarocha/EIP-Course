package br.com.argonavis.eipcourse.endpoint.dispatcher;

import javax.jms.Message;
import javax.jms.TextMessage;

public class TextProcessor extends MessageProcessor {
	
	public TextProcessor(Message message) {
		super(message);
	}

	@Override
	public void process() {
        TextMessage message = (TextMessage) getMessage();
		
		System.out.println("Processing Text.");

	}

}
