package br.com.concrete.booking.domain.event;

public interface EventConsumer<T> {

    void consume(T payload);
}
