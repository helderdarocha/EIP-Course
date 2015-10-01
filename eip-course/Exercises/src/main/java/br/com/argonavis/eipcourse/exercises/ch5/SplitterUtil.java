package br.com.argonavis.eipcourse.exercises.ch5;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class SplitterUtil {
    public static String[] splitLines(String text) {
    	return text.split("\n");
    }
    
    public static String[] split(String text, int size) throws IOException {
    	BufferedReader reader = new BufferedReader(new StringReader(text));
    	char[] cbuf = new char[size];
    	int len = reader.read(cbuf);
    	List<String> blocks = new ArrayList<>();
    	while(len != -1) {
    		blocks.add(new String(cbuf).trim());
    		len = reader.read(cbuf);
    	}
    	return blocks.toArray(new String[blocks.size()]);
    }
}
