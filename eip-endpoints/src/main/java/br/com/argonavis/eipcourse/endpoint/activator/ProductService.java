package br.com.argonavis.eipcourse.endpoint.activator;

public class ProductService {
    public void persist(Product p) {
    	ProductDatabase.addProduct(p);
    }
    
    public Product select(Long pk) throws ProductNotFoundException {
    	Product p = ProductDatabase.getProduct(pk);
    	if (p == null) {
    		throw new ProductNotFoundException("Product " + pk + " not found!");
    	}
    	return p;
    }
}
