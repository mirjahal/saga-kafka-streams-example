package br.com.concrete.order.domain.business;

import br.com.concrete.order.domain.entity.Order;

import java.util.UUID;

public interface CancelOrder {

    Order cancel(UUID orderId);
}
