package br.com.concrete.orchestrator.topology;

import br.com.concrete.AccountWithdraw;
import br.com.concrete.AccountWithdrawResult;
import br.com.concrete.BookingCancel;
import br.com.concrete.BookingConfirm;
import br.com.concrete.BookingCreate;
import br.com.concrete.BookingResult;
import br.com.concrete.BookingStatus;
import br.com.concrete.OrderCancel;
import br.com.concrete.OrderConfirm;
import br.com.concrete.OrderCreated;
import br.com.concrete.OrderStatus;
import br.com.concrete.orchestrator.infrastructure.configuration.TopicConfiguration;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

import javax.inject.Named;

import static br.com.concrete.AccountWithdrawStatus.APPROVED;
import static br.com.concrete.AccountWithdrawStatus.DENIED;
import static br.com.concrete.BookingStatus.RESERVE;
import static br.com.concrete.OrderStatus.CANCEL;
import static br.com.concrete.OrderStatus.PENDING;
import static org.apache.kafka.common.serialization.Serdes.String;

@Named
public class StreamOrchestrator {

    private final StreamsBuilder streamsBuilder;
    private final SerdeConfiguration serdeConfiguration;
    private final TopicConfiguration topicConfiguration;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public StreamOrchestrator(
        StreamsBuilder streamsBuilder,
        SerdeConfiguration serdeConfiguration,
        TopicConfiguration topicConfiguration
    ) {
        this.streamsBuilder = streamsBuilder;
        this.serdeConfiguration = serdeConfiguration;
        this.topicConfiguration = topicConfiguration;
    }

    @Bean
    public Topology buildTopology() {
        // Get pending orders flow
        KStream<String, OrderCreated> orderPendingStream = buildOrderPendingStream(streamsBuilder);
        buildBookingCreateStream(orderPendingStream);

        // Booking flow
        KStream<String, BookingResult>[] bookingResultStream = buildBookingResultStream(streamsBuilder);
        KStream<String, BookingResult> bookingResultErrorStream = bookingResultStream[0];
        buildOrderCancelStreamForBookingError(bookingResultErrorStream);
        KStream<String, BookingResult> bookingResultSuccessStream = bookingResultStream[1];
        buildAccountWithdrawStream(bookingResultSuccessStream);

        // Payment flow
        KStream<String, AccountWithdrawResult>[] accountWithdrawResultStream = buildAccountWithdrawResultStream(streamsBuilder);
        KStream<String, AccountWithdrawResult> accountWithdrawResultStreamDenied = accountWithdrawResultStream[0];
        buildOrderCancelStreamForPaymentDenied(accountWithdrawResultStreamDenied);
        buildBookingCancelStream(accountWithdrawResultStreamDenied);
        KStream<String, AccountWithdrawResult> accountWithdrawResultStreamApproved = accountWithdrawResultStream[1];
        buildOrderConfirmStream(accountWithdrawResultStreamApproved);
        buildBookingConfirmStream(accountWithdrawResultStreamApproved);

        return streamsBuilder.build();
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

    private KStream<String, BookingResult>[] buildBookingResultStream(StreamsBuilder streamsBuilder) {
        KStream<String, BookingResult> bookingResultStream = streamsBuilder.stream(
            topicConfiguration.getBookingResultTopic(),
            Consumed.with(String(), serdeConfiguration.configure())
        );

        Predicate<String, BookingResult> bookingErrors = (key, value) -> value.getErrorMessage() != null;
        Predicate<String, BookingResult> bookingSuccess =  (key, value) -> value.getStatus().toString().equals(RESERVE.name());

        KStream<String, BookingResult>[] bookingResultStreamBranches = bookingResultStream.branch(
            bookingErrors,
            bookingSuccess
        );

        bookingResultStreamBranches[0].foreach(
            (key, value) -> logger.info("Consuming booking error: " + value)
        );
        bookingResultStreamBranches[1].foreach(
            (key, value) -> logger.info("Consuming booking success: " + value)
        );

        return bookingResultStreamBranches;
    }

    private void buildOrderCancelStreamForBookingError(KStream<String, BookingResult> bookingResultErrorsStream) {
        KStream<String, OrderCancel> orderCancelStream = bookingResultErrorsStream
            .mapValues(
                (value) -> new OrderCancel(value.getOrderId(), CANCEL, value.getErrorMessage())
            );

        materializeOrderCancelStream(orderCancelStream);
    }

    private void buildOrderCancelStreamForPaymentDenied(KStream<String, AccountWithdrawResult> accountWithdrawResultErrorStream) {
        KStream<String, OrderCancel> orderCancelStream = accountWithdrawResultErrorStream
            .mapValues(
                (value) -> new OrderCancel(value.getOrderId(), CANCEL, value.getMessage())
            );

        materializeOrderCancelStream(orderCancelStream);
    }

    private void materializeOrderCancelStream(KStream<String, OrderCancel> orderCancelStream) {
        orderCancelStream.foreach(
            (key, value) -> logger.info("Requesting cancel order " + value)
        );

        orderCancelStream.to(
            topicConfiguration.getOrderCancelTopic(),
            Produced.with(String(), serdeConfiguration.configure())
        );
    }

    private void buildAccountWithdrawStream(KStream<String, BookingResult> bookingResultSuccessStream) {
        KStream<String, AccountWithdraw> accountWithdrawStream = bookingResultSuccessStream
            .mapValues((value) -> {
                AccountWithdraw accountWithdraw = new AccountWithdraw();
                accountWithdraw.setAccountId("304648ff-6efa-4a75-81f3-d7718d06b2a5");
                accountWithdraw.setOrderId(value.getOrderId());
                accountWithdraw.setAmount(value.getPrice());

                return accountWithdraw;
            });

        accountWithdrawStream.foreach(
            (key, value) -> logger.info("Account withdraw for booking: " + value)
        );

        accountWithdrawStream.to(
            topicConfiguration.getAccountWithdrawTopic(),
            Produced.with(String(), serdeConfiguration.configure())
        );
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
            (key, value) -> logger.info("Requesting confirm order " + value)
        );

        orderConfirmStream.to(
            topicConfiguration.getBookingConfirmTopic(),
            Produced.with(String(), serdeConfiguration.configure())
        );
    }
}
