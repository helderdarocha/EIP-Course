package br.com.argonavis.eipcourse.messaging.jms.translator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

public class TranslateTweetToHtml {
	public Message translate(Message m, Session session) throws JMSException {
		TextMessage message = (TextMessage) m;
		String[] words = message.getText().split(" ");
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
		
		String sender = (String)message.getStringProperty("sender");
		String newPayload =  buffer.toString().substring(0,buffer.length()-1);
		
		String result = "<div class='tweet'><span class='sender'>" + sender + "</span>" + newPayload + "</div>";
		
		return session.createTextMessage(result);
		
	}
}