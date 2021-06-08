package br.com.concrete.orchestrator.topology;

import br.com.concrete.orchestrator.topology.builder.BookingStreamBuilder;
import br.com.concrete.orchestrator.topology.builder.PaymentStreamBuilder;
import br.com.concrete.orchestrator.topology.builder.PendingOrdersStreamBuilder;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.inject.Named;

@Named
    public class StreamTopology {

    private final StreamsBuilder streamsBuilder;
    private final PendingOrdersStreamBuilder pendingOrdersStreamBuilder;
    private final BookingStreamBuilder bookingStreamBuilder;
    private final PaymentStreamBuilder paymentStreamBuilder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public StreamTopology(
        StreamsBuilder streamsBuilder,
        PendingOrdersStreamBuilder pendingOrdersStreamBuilder,
        BookingStreamBuilder bookingStreamBuilder,
        PaymentStreamBuilder paymentStreamBuilder
    ) {
        this.streamsBuilder = streamsBuilder;
        this.pendingOrdersStreamBuilder = pendingOrdersStreamBuilder;
        this.bookingStreamBuilder = bookingStreamBuilder;
        this.paymentStreamBuilder = paymentStreamBuilder;
    }

    @Bean
    public Topology buildTopology() {
        pendingOrdersStreamBuilder.build(streamsBuilder);
        bookingStreamBuilder.build(streamsBuilder);
        paymentStreamBuilder.build(streamsBuilder);

        return streamsBuilder.build();
    }
}
