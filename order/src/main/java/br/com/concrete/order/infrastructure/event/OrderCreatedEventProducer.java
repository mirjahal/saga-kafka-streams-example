package br.com.concrete.order.infrastructure.event;

import br.com.concrete.OrderCreated;
import br.com.concrete.order.domain.event.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import javax.inject.Named;

@Named
public class OrderCreatedEventProducer implements EventProducer<OrderCreated> {

    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
    private final String orderCreatedTopic;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public OrderCreatedEventProducer(
        KafkaTemplate<String, OrderCreated> kafkaTemplate,
        @Value("${order.topics.order-created}") String orderCreatedTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.orderCreatedTopic = orderCreatedTopic;
    }

    @Override
    public void produce(OrderCreated orderCreated) {
        logger.info("Producing PENDING order: " + orderCreated);

        kafkaTemplate.send(orderCreatedTopic, orderCreated);
    }
}
