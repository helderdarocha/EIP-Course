package br.com.argonavis.eipcourse.mgmt.controlbus;

public class ProductClient {
	public static void main(String[] args) throws MapperException {
		SimpleMapperFacade facade = new JmsMapperFacade("request-queue", "response-queue");
		
        Product p1 = new Product(5L, "G837", 56.99);
        System.out.println("Client will try to persist Product 5");
        facade.persist(p1);
        
        try {
        	System.out.println("Client will try to find Product 5");
			Product p2 = facade.select(5L);
			System.out.println("Product found: " + p2);
		} catch (ProductNotFoundException e) {
			e.printStackTrace();
		} finally {
			facade.closeConnection();
		}
	}
}
