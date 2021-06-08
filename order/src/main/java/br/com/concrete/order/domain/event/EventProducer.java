package br.com.concrete.order.domain.event;

public interface EventProducer<T> {

    void produce(T payload);
}
