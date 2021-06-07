package br.com.concrete.order.domain.repository;

import br.com.concrete.order.domain.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);
    List<Order> findAll();
    Optional<Order> findById(UUID orderId);
}
