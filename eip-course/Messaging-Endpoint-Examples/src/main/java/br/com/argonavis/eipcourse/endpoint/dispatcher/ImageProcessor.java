package br.com.argonavis.eipcourse.endpoint.dispatcher;

import javax.jms.BytesMessage;
import javax.jms.Message;

public class ImageProcessor extends MessageProcessor {
	
	public ImageProcessor(Message message) {
		super(message);
	}

	@Override
	public void process() {
		BytesMessage message = (BytesMessage)getMessage();
		
		System.out.println("Processing Image.");

	}

}
