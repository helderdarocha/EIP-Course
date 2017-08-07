package br.com.argonavis.eipcourse.translator.claimcheck;

import java.util.HashMap;
import java.util.Map;

public class DataStore {
	
	private static Map<String, String> dataMap = new HashMap<>();
	
	public static void save(String key, String data) {
		dataMap.put(key, data);
	}
	
	public static String get(String key) {
		return dataMap.get(key);
	}

}
