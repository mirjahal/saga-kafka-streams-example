package br.com.concrete.order.infrastructure.event;

import br.com.concrete.OrderCancel;
import br.com.concrete.OrderCreated;
import br.com.concrete.order.domain.business.CancelOrder;
import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.domain.event.EventStreamable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.inject.Named;
import java.util.UUID;

@Named
public class KafkaEventStreamer implements EventStreamable {

    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
    private final CancelOrder cancelOrder;
    private final String orderCreatedTopic;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KafkaEventStreamer(
        KafkaTemplate<String, OrderCreated> kafkaTemplate,
        @Value("${order.topics.order-created}") String orderCreatedTopic,
        CancelOrder cancelOrder
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderCreatedTopic = orderCreatedTopic;
        this.cancelOrder = cancelOrder;
    }

    @Override
    public void produceOrderCreatedEvent(int roomNumber, Order order) {
        OrderCreated orderCreated = new OrderCreated(
            order.getId().toString(),
            roomNumber,
            order.getStatus().name()
        );

        logger.info("Producing PENDING order: " + orderCreated);

        kafkaTemplate.send(orderCreatedTopic, orderCreated);
    }

    @Override
    @KafkaListener(topics = "${order.topics.order-cancel}")
    public void consumeOrderCancelEvent(OrderCancel orderCancel) {
        logger.info("Consuming order cancel event: " + orderCancel);

        cancelOrder.cancel(UUID.fromString(orderCancel.getOrderId().toString()));
    }
}
