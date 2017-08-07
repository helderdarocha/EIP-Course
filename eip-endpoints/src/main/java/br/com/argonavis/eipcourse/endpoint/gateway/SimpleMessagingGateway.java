package br.com.argonavis.eipcourse.endpoint.gateway;

public interface SimpleMessagingGateway {
    void send(SimpleChannel c, SimpleMessage m) throws MessagingException;
    SimpleMessage receive(SimpleChannel c) throws MessagingException;
    void register(MessagingEventHandler handler, SimpleChannel c) throws MessagingException;
}
