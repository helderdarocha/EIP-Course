package br.com.argonavis.eipcourse.endpoint.gateway;

public interface MessagingEventHandler {
    void process(SimpleMessage m);
}
