package br.com.argonavis.eipcourse.endpoint.mapper;

public interface SimpleMapperFacade {
    void persist(Product p);
    Product select(Long pk) throws ProductNotFoundException;
    void closeConnection();
}
