package br.com.argonavis.si.examples.tdc;

import org.springframework.integration.core.MessageSelector;
import org.springframework.messaging.Message;

public class LinkTweetSelector implements MessageSelector {

	public boolean accept(Message<?> message) {
		String contents = (String)message.getPayload();
		for (String word : contents.split(" ")) {
			if (word.startsWith("http://") || word.startsWith("https://")) {
				return true;
			}
		}
		return false;
	}

}
