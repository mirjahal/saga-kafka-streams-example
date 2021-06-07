package br.com.concrete.order.application;

import br.com.concrete.order.domain.business.ListOrder;
import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.domain.repository.OrderRepository;

import javax.inject.Named;
import java.util.List;

@Named
public class ListOrderImpl implements ListOrder {

    private final OrderRepository orderRepository;

    public ListOrderImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
