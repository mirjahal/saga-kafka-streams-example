package br.com.concrete.order.application;

import br.com.concrete.order.domain.business.CreateOrder;
import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.domain.event.EventStreamable;
import br.com.concrete.order.domain.repository.OrderRepository;

import javax.inject.Named;

import static br.com.concrete.order.domain.entity.enums.OrderStatus.PENDING;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Named
public class CreateOrderImpl implements CreateOrder {

    private final OrderRepository orderRepository;
    private final EventStreamable eventStreamable;

    public CreateOrderImpl(OrderRepository orderRepository, EventStreamable eventStreamable) {
        this.orderRepository = orderRepository;
        this.eventStreamable = eventStreamable;
    }

    @Override
    public Order create(int roomNumber) {
        Order orderCreated = orderRepository.save(new Order(randomUUID(), PENDING, now()));

        eventStreamable.produceOrderCreatedEvent(roomNumber, orderCreated);

        return orderCreated;
    }
}
