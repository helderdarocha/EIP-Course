package br.com.argonavis.eipcourse.messaging.camel.translator;

public class TranslateTweetToHtml {
	public String translate(String sender, String payload) {
		String[] words = payload.split(" ");
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
		
		String newPayload =  buffer.toString().substring(0,buffer.length()-1);
		String result = "<div class='tweet'><span class='sender'>" + sender + "</span>" + newPayload + "</div>";
		
		return result;
		
	}
}