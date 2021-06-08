package br.com.concrete.order.infrastructure.event;

import br.com.concrete.OrderCancel;
import br.com.concrete.order.domain.business.CancelOrder;
import br.com.concrete.order.domain.event.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import javax.inject.Named;
import java.util.UUID;

@Named
public class OrderCancelEventConsumer implements EventConsumer<OrderCancel> {

    private final CancelOrder cancelOrder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderCancelEventConsumer(CancelOrder cancelOrder) {
        this.cancelOrder = cancelOrder;
    }

    @Override
    @KafkaListener(topics = "${order.topics.order-cancel}")
    public void consume(OrderCancel orderCancel) {
        logger.info("Consuming order cancel event: " + orderCancel);

        cancelOrder.cancel(UUID.fromString(orderCancel.getOrderId().toString()));
    }
}
