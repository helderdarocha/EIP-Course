package br.com.argonavis.eipcourse.exercises.ch4.solution;

import java.util.ArrayList;
import java.util.List;

public class MockData {

	private List<String> mockData = new ArrayList<>();

	private MockData() {
		for (int i = 0; i < 10; i++) {
			String xml = "<product>" 
		               + "    <name>Product_"  + (i + 1) + "</name>" 
					   + "    <price>" + (Math.random() * 10000) / 100 + "</price>" 
		               + "</product>";
			mockData.add(xml);
		}
	}

	private static MockData instance = new MockData();

	public static List<String> getMockData() {
		return instance.mockData;
	}
}
