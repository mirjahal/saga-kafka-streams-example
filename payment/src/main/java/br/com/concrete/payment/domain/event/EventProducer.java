package br.com.concrete.payment.domain.event;

public interface EventProducer<T> {

    void produce(T payload);
}
