package br.com.concrete.order.domain.event;

import br.com.concrete.OrderCancel;
import br.com.concrete.order.domain.entity.Order;

public interface EventStreamable {

    void produceOrderCreatedEvent(int roomNumber, Order order);
    void consumeOrderCancelEvent(OrderCancel orderCancel);
}
