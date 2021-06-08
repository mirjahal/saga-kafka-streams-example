package br.com.concrete.booking.domain.event;

public interface EventProducer<T> {

    void produce(T payload);
}
