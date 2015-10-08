package br.com.argonavis.cursocamel.si.twitter;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.Transformer;
import org.springframework.messaging.Message;
import org.springframework.social.twitter.api.Tweet;

public class TweetTransformer implements Transformer {

	@Override
	public Message<?> transform(Message<?> tweetMessage) {
		Tweet tweet = (Tweet)tweetMessage.getPayload();
		String sender = tweet.getFromUser();
		String text   = tweet.getText();
		
		Message<String> message = MessageBuilder.withPayload(text)
				.setHeader("sender", sender)
				.build(); 
		return message;
	}

}
