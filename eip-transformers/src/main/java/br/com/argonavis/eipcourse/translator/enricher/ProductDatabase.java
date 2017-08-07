package br.com.argonavis.eipcourse.translator.enricher;

import java.util.HashMap;
import java.util.Map;

public class ProductDatabase {
	
	private static Map<Long, Product> produtosMap = new HashMap<>();
	static {
		produtosMap.put(1L, new Product(1, "A001", 34.95));
		produtosMap.put(2L, new Product(2, "S451", 29.95));
		produtosMap.put(3L, new Product(3, "W008", 79.95));
		produtosMap.put(4L, new Product(4, "W023", 112.95));
	}

	public static Product getProduto(long id) {
		return produtosMap.get(id);
	}
}
