package br.com.concrete.order.infrastructure.event;

import br.com.concrete.OrderCreated;
import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.domain.event.EventStreamable;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static br.com.concrete.order.domain.entity.enums.OrderStatus.PENDING;
import static java.time.Duration.ofMillis;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@EmbeddedKafka(topics = KafkaEventStreamerTest.TOPIC)
public class KafkaEventStreamerTest {

    @Autowired private EmbeddedKafkaBroker embeddedKafkaBroker;
    @Autowired private EventStreamable eventStreamable;
    private Consumer<String, OrderCreated> consumer;
    static final String TOPIC = "test";

    @BeforeEach
    public void setUp() throws Exception {
        Map<String, Object> props = KafkaTestUtils.consumerProps("test-broker", "false", embeddedKafkaBroker);

        consumer = new DefaultKafkaConsumerFactory<String, OrderCreated>(props).createConsumer();
        consumer.subscribe(Collections.singleton(TOPIC));
        consumer.poll(ofMillis(0));
    }

    @AfterEach
    public void tearDown() throws Exception {
        consumer.close();
    }

    @Test
    void produceOrderCreatedEvent() {
        Order order = new Order(UUID.randomUUID(), PENDING, now());

        eventStreamable.produceOrderCreatedEvent(102, order);

        ConsumerRecord<String, OrderCreated> record = KafkaTestUtils.getSingleRecord(consumer, TOPIC);
        assertThat(record).isNotNull();
    }
}