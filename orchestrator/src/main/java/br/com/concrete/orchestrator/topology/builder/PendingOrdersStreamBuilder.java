package br.com.concrete.orchestrator.topology.builder;

import br.com.concrete.BookingCreate;
import br.com.concrete.OrderCreated;
import br.com.concrete.orchestrator.infrastructure.configuration.TopicConfiguration;
import br.com.concrete.orchestrator.topology.configuration.SerdeConfiguration;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

import static br.com.concrete.BookingStatus.RESERVE;
import static br.com.concrete.OrderStatus.PENDING;
import static org.apache.kafka.common.serialization.Serdes.String;

@Named
public class PendingOrdersStreamBuilder {

    private final StreamsBuilder streamsBuilder;
    private final SerdeConfiguration serdeConfiguration;
    private final TopicConfiguration topicConfiguration;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PendingOrdersStreamBuilder(StreamsBuilder streamsBuilder, SerdeConfiguration serdeConfiguration, TopicConfiguration topicConfiguration) {
        this.streamsBuilder = streamsBuilder;
        this.serdeConfiguration = serdeConfiguration;
        this.topicConfiguration = topicConfiguration;
    }

    public void build() {
        KStream<String, OrderCreated> orderPendingStream = buildOrderPendingStream(streamsBuilder);
        buildBookingCreateStream(orderPendingStream);
    }

    private KStream<String, OrderCreated> buildOrderPendingStream(StreamsBuilder streamsBuilder) {
        KStream<String, OrderCreated> orderPendingStream = streamsBuilder.stream(
            topicConfiguration.getOrderCreatedTopic(),
            Consumed.with(String(), serdeConfiguration.configure())
        );

        orderPendingStream
            .filter((key, value) -> value != null)
            .filter((key, value) -> value.getStatus().toString().equals(PENDING))
            .foreach((key, value) -> logger.info("Consuming PENDING Order: " + value));

        return orderPendingStream;
    }

    private void buildBookingCreateStream(KStream<String, OrderCreated> pendingOrdersStream) {
        KStream<String, BookingCreate> bookingCreateStream = pendingOrdersStream
            .mapValues((value) -> {
                BookingCreate bookingCreate = new BookingCreate();
                bookingCreate.setRoomNumber(value.getRoomNumber());
                bookingCreate.setOrderId(value.getOrderId());
                bookingCreate.setStatus(RESERVE);

                return bookingCreate;
            });

        bookingCreateStream.foreach(
            (key, value) -> logger.info("Requesting a room " + value)
        );

        bookingCreateStream.to(
            topicConfiguration.getBookingCreateTopic(),
            Produced.with(String(), serdeConfiguration.configure())
        );
    }
}
