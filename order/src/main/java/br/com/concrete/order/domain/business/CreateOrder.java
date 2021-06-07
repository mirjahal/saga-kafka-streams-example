package br.com.concrete.order.domain.business;

import br.com.concrete.order.domain.entity.Order;

public interface CreateOrder {

    Order create(int roomNumber);
}
