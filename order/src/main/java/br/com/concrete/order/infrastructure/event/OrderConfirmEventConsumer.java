package br.com.concrete.order.infrastructure.event;

import br.com.concrete.OrderConfirm;
import br.com.concrete.order.domain.business.ConfirmOrder;
import br.com.concrete.order.domain.event.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import javax.inject.Named;
import java.util.UUID;

@Named
public class OrderConfirmEventConsumer implements EventConsumer<OrderConfirm> {

    private final ConfirmOrder confirmOrder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderConfirmEventConsumer(ConfirmOrder confirmOrder) {
        this.confirmOrder = confirmOrder;
    }

    @Override
    @KafkaListener(topics = "${order.topics.order-confirm}")
    public void consume(OrderConfirm orderConfirm) {
        logger.info("Consuming order confirm event: " + orderConfirm);

        confirmOrder.confirm(UUID.fromString(orderConfirm.getOrderId().toString()));
    }
}
