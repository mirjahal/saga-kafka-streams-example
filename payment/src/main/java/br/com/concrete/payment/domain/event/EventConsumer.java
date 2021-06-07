package br.com.concrete.payment.domain.event;

public interface EventConsumer<T> {

    void consume(T payload);
}
