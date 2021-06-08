package br.com.concrete.orchestrator.topology.builder;

import br.com.concrete.AccountWithdraw;
import br.com.concrete.BookingResult;
import br.com.concrete.OrderCancel;
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

import static br.com.concrete.BookingStatus.RESERVE;
import static br.com.concrete.OrderStatus.CANCEL;
import static org.apache.kafka.common.serialization.Serdes.String;

@Named
public class BookingStreamBuilder {

    private final SerdeConfiguration serdeConfiguration;
    private final TopicConfiguration topicConfiguration;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BookingStreamBuilder(SerdeConfiguration serdeConfiguration, TopicConfiguration topicConfiguration) {
        this.serdeConfiguration = serdeConfiguration;
        this.topicConfiguration = topicConfiguration;
    }

    public void build(StreamsBuilder streamsBuilder) {
        KStream<String, BookingResult>[] bookingResultStream = buildBookingResultStream(streamsBuilder);

        KStream<String, BookingResult> bookingResultErrorStream = bookingResultStream[0];
        buildOrderCancelStreamForBookingError(bookingResultErrorStream);

        KStream<String, BookingResult> bookingResultSuccessStream = bookingResultStream[1];
        buildAccountWithdrawStream(bookingResultSuccessStream);
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
}
