package br.com.argonavis.eipcourse.messaging.si.pipesfilters;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class UpperCaseTransformer implements Transformer {

	@Override
	public Message<?> transform(Message<?> message) {
		String data = message.getPayload().toString().toUpperCase();
		return MessageBuilder.withPayload(data).build(); 
	}

}
