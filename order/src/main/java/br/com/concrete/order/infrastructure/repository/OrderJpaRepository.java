package br.com.concrete.order.infrastructure.repository;

import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.domain.repository.OrderRepository;
import org.springframework.data.repository.RepositoryDefinition;

import java.util.UUID;

@RepositoryDefinition(domainClass = Order.class, idClass = UUID.class)
public interface OrderJpaRepository extends OrderRepository {
}
