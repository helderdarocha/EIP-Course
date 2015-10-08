package br.com.argonavis.cursocamel.si.twitter;

public class TweetSubjectTagger {
    public String setSubjectHeader(String payload) {
    	if(SelectUtil.isAboutJava(payload)) {
    		return "java";
    	} else {
    		return "other"; // web
    	}
    }
}
