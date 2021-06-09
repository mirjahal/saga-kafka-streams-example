package br.com.concrete.orchestrator.topology.builder;

import br.com.concrete.AccountWithdrawResult;
import br.com.concrete.BookingCancel;
import br.com.concrete.BookingConfirm;
import br.com.concrete.BookingStatus;
import br.com.concrete.OrderCancel;
import br.com.concrete.OrderConfirm;
import br.com.concrete.OrderStatus;
import br.com.concrete.orchestrator.infrastructure.configuration.TopicConfiguration;
import br.com.concrete.orchestrator.topology.configuration.SerdeConfiguration;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

import static br.com.concrete.AccountWithdrawStatus.APPROVED;
import static br.com.concrete.AccountWithdrawStatus.DENIED;
import static br.com.concrete.OrderStatus.CANCEL;
import static org.apache.kafka.common.serialization.Serdes.String;

@Named
public class PaymentStreamBuilder {

    private final StreamsBuilder streamsBuilder;
    private final SerdeConfiguration serdeConfiguration;
    private final TopicConfiguration topicConfiguration;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public PaymentStreamBuilder(StreamsBuilder streamsBuilder, SerdeConfiguration serdeConfiguration, TopicConfiguration topicConfiguration) {
        this.streamsBuilder = streamsBuilder;
        this.serdeConfiguration = serdeConfiguration;
        this.topicConfiguration = topicConfiguration;
    }

    public void build() {
        KStream<String, AccountWithdrawResult>[] accountWithdrawResultStream = buildAccountWithdrawResultStream(streamsBuilder);

        KStream<String, AccountWithdrawResult> accountWithdrawResultStreamDenied = accountWithdrawResultStream[0];
        buildOrderCancelStreamForPaymentDenied(accountWithdrawResultStreamDenied);
        buildBookingCancelStream(accountWithdrawResultStreamDenied);

        KStream<String, AccountWithdrawResult> accountWithdrawResultStreamApproved = accountWithdrawResultStream[1];
        buildOrderConfirmStream(accountWithdrawResultStreamApproved);
        buildBookingConfirmStream(accountWithdrawResultStreamApproved);
    }

    private KStream<String, AccountWithdrawResult>[] buildAccountWithdrawResultStream(StreamsBuilder streamsBuilder) {
        KStream<String, AccountWithdrawResult> accountWithdrawResultStream = streamsBuilder.stream(
            topicConfiguration.getAccountWithdrawResultTopic(),
            Consumed.with(String(), serdeConfiguration.configure())
        );

        Predicate<String, AccountWithdrawResult> accountWithdrawResultDenied = (key, value) -> value.getStatus() == DENIED;
        Predicate<String, AccountWithdrawResult> accountWithdrawResultApproved =  (key, value) -> value.getStatus() == APPROVED;

        KStream<String, AccountWithdrawResult>[] accountWithdrawResultStreamBranches = accountWithdrawResultStream.branch(
            accountWithdrawResultDenied,
            accountWithdrawResultApproved
        );

        accountWithdrawResultStreamBranches[0].foreach(
            (key, value) -> logger.info("Consuming account withdraw result denied: " + value)
        );
        accountWithdrawResultStreamBranches[1].foreach(
            (key, value) -> logger.info("Consuming account withdraw result approved: " + value)
        );

        return accountWithdrawResultStreamBranches;
    }

    private void buildOrderCancelStreamForPaymentDenied(KStream<String, AccountWithdrawResult> accountWithdrawResultErrorStream) {
        KStream<String, OrderCancel> orderCancelStream = accountWithdrawResultErrorStream
            .mapValues(
                (value) -> new OrderCancel(value.getOrderId(), CANCEL, value.getMessage())
            );

        orderCancelStream.foreach(
            (key, value) -> logger.info("Requesting cancel order " + value)
        );

        orderCancelStream.to(
            topicConfiguration.getOrderCancelTopic(),
            Produced.with(String(), serdeConfiguration.configure())
        );
    }

    private void buildBookingCancelStream(KStream<String, AccountWithdrawResult> accountWithdrawResultErrorStream) {
        KStream<String, BookingCancel> bookingCancelStream = accountWithdrawResultErrorStream
            .mapValues(
                (value) -> new BookingCancel(value.getOrderId(), value.getRoomNumber(), BookingStatus.CANCEL, value.getMessage())
            );

        bookingCancelStream.foreach(
            (key, value) -> logger.info("Requesting cancel booking " + value)
        );

        bookingCancelStream.to(
            topicConfiguration.getBookingCancelTopic(),
            Produced.with(String(), serdeConfiguration.configure())
        );
    }

    private void buildOrderConfirmStream(KStream<String, AccountWithdrawResult> accountWithdrawResultStreamApproved) {
        KStream<String, OrderConfirm> orderConfirmStream = accountWithdrawResultStreamApproved
            .mapValues(
                (value) -> new OrderConfirm(value.getOrderId(), value.getRoomNumber(), OrderStatus.CONFIRM)
            );

        orderConfirmStream.foreach(
            (key, value) -> logger.info("Requesting confirm order " + value)
        );

        orderConfirmStream.to(
            topicConfiguration.getOrderConfirmTopic(),
            Produced.with(String(), serdeConfiguration.configure())
        );
    }

    private void buildBookingConfirmStream(KStream<String, AccountWithdrawResult> accountWithdrawResultStreamApproved) {
        KStream<String, BookingConfirm> orderConfirmStream = accountWithdrawResultStreamApproved
            .mapValues(
                (value) -> new BookingConfirm(value.getOrderId(), value.getRoomNumber(), BookingStatus.CONFIRM)
            );

        orderConfirmStream.foreach(
            (key, value) -> logger.info("Requesting confirm booking " + value)
        );

        orderConfirmStream.to(
            topicConfiguration.getBookingConfirmTopic(),
            Produced.with(String(), serdeConfiguration.configure())
        );
    }
}
