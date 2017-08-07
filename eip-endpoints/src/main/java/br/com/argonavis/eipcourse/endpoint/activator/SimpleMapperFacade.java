package br.com.argonavis.eipcourse.endpoint.activator;

public interface SimpleMapperFacade {
    void persist(Product p);
    Product select(Long pk) throws ProductNotFoundException;
    void closeConnection();
}
