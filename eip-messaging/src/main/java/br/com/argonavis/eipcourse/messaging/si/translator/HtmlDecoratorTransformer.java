package br.com.argonavis.eipcourse.messaging.si.translator;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;

public class HtmlDecoratorTransformer implements Transformer {

	@Override
	public Message<?> transform(Message<?> message) {
		String[] words = message.getPayload().toString().split(" ");
		StringBuilder buffer = new StringBuilder();
		for(String word: words) {
			if(word.startsWith("#")) {
				buffer.append("<span class='hashtag'>").append(word).append("</span>");
			} else if (word.startsWith("@")) {
				buffer.append("<span class='user'>").append(word).append("</span>");
			} else if (word.startsWith("http")) {
				buffer.append("<a href='").append(word).append("'>").append(word).append("</a>");
			} else {
				buffer.append(word);
			}
			buffer.append(" ");
		}
		
		String sender = (String)message.getHeaders().get("sender");
		String newPayload =  buffer.toString().substring(0,buffer.length()-1);
		
		String result = "<div class='tweet'><span class='sender'>" + sender + "</span>" + newPayload + "</div>";
		Message<String> decoratedMessage = MessageBuilder.withPayload(result).copyHeaders(message.getHeaders()).build(); 
		return decoratedMessage;
	}
}
