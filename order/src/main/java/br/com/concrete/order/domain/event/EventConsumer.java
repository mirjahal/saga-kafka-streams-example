package br.com.concrete.order.domain.event;

public interface EventConsumer<T> {

    void consume(T payload);
}
