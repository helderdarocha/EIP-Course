package br.com.argonavis.eipcourse.translator.enricher;

import java.util.HashMap;
import java.util.Map;

public class ProductDatabase {
	
	private static Map<Long, Produto> produtosMap = new HashMap<>();
	static {
		produtosMap.put(1L, new Produto(1, "A001", 34.95));
		produtosMap.put(2L, new Produto(2, "S451", 29.95));
		produtosMap.put(3L, new Produto(3, "W008", 79.95));
		produtosMap.put(4L, new Produto(4, "W023", 112.95));
	}

	public static Produto getProduto(long id) {
		return produtosMap.get(id);
	}
}
