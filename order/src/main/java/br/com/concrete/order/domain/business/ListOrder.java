package br.com.concrete.order.domain.business;

import br.com.concrete.order.domain.entity.Order;

import java.util.List;

public interface ListOrder {

    List<Order> findAll();
}
