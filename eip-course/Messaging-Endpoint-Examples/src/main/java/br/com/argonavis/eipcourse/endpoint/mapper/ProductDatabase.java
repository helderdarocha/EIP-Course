package br.com.argonavis.eipcourse.endpoint.mapper;

import java.util.HashMap;
import java.util.Map;

public class ProductDatabase {

	private static Map<Long, Product> productMap = new HashMap<>();
	static {
		productMap.put(1L, new Product(1L, "A001", 34.95));
		productMap.put(2L, new Product(2L, "S451", 29.95));
		productMap.put(3L, new Product(3L, "W008", 79.95));
		productMap.put(4L, new Product(4L, "W023", 112.95));
	}
	
	public static void addProduct(Product p) {
		productMap.put(p.getId(), p);
	}

	public static Product getProduct(long id) {
		return productMap.get(id);
	}
}
