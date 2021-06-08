package br.com.concrete.order.application;

import br.com.concrete.OrderCreated;
import br.com.concrete.OrderStatus;
import br.com.concrete.order.domain.business.CreateOrder;
import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.domain.event.EventProducer;
import br.com.concrete.order.domain.repository.OrderRepository;

import javax.inject.Named;

import static br.com.concrete.order.domain.entity.enums.OrderStatus.PENDING;
import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Named
public class CreateOrderImpl implements CreateOrder {

    private final OrderRepository orderRepository;
    private final EventProducer<OrderCreated> eventProducer;

    public CreateOrderImpl(OrderRepository orderRepository, EventProducer<OrderCreated> eventProducer) {
        this.orderRepository = orderRepository;
        this.eventProducer = eventProducer;
    }

    @Override
    public Order create(int roomNumber) {
        Order order = orderRepository.save(new Order(randomUUID(), PENDING, now()));

        OrderCreated orderCreated = new OrderCreated(
            order.getId().toString(),
            roomNumber,
            OrderStatus.PENDING
        );
        eventProducer.produce(orderCreated);

        return order;
    }
}
