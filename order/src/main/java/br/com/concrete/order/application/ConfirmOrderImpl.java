package br.com.concrete.order.application;

import br.com.concrete.order.domain.business.ConfirmOrder;
import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.domain.repository.OrderRepository;

import javax.inject.Named;
import java.util.UUID;

import static br.com.concrete.order.domain.entity.enums.OrderStatus.CONFIRM;

@Named
public class ConfirmOrderImpl implements ConfirmOrder {

    private final OrderRepository orderRepository;

    public ConfirmOrderImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order confirm(UUID orderId) {
        Order order = orderRepository
            .findById(orderId)
            .get();

        order.setStatus(CONFIRM);

        return orderRepository.save(order);
    }
}
