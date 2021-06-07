package br.com.concrete.order.application;

import br.com.concrete.order.domain.business.CancelOrder;
import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.domain.repository.OrderRepository;

import javax.inject.Named;
import java.util.Optional;
import java.util.UUID;

import static br.com.concrete.order.domain.entity.enums.OrderStatus.CANCEL;

@Named
public class CancelOrderImpl implements CancelOrder {

    private final OrderRepository orderRepository;

    public CancelOrderImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order cancel(UUID orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        Order order = optionalOrder.get();
        order.setStatus(CANCEL);

        return orderRepository.save(order);
    }
}
