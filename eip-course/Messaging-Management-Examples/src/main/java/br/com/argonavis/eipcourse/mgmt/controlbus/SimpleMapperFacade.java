package br.com.argonavis.eipcourse.mgmt.controlbus;

public interface SimpleMapperFacade {
    void persist(Product p);
    Product select(Long pk) throws ProductNotFoundException;
    void closeConnection();
}
