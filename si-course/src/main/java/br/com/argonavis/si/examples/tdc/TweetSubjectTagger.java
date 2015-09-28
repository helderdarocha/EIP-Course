package br.com.argonavis.si.examples.tdc;

public class TweetSubjectTagger {
    public String setSubjectHeader(String payload) {
    	if(SelectUtil.isAboutJava(payload)) {
    		return "java";
    	} else {
    		return "other"; // web
    	}
    }
}
