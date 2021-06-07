package br.com.concrete.booking.infrastructure.event;

import br.com.concrete.BookingCreate;
import br.com.concrete.BookingResult;
import br.com.concrete.booking.domain.business.ReserveRoom;
import br.com.concrete.booking.domain.event.EventStreamable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import javax.inject.Named;

@Named
public class KafkaEventStreamer implements EventStreamable {

    private final ReserveRoom reserveRoom;
    private final KafkaTemplate<String, BookingResult> kafkaTemplate;
    private final String bookingResultTopic;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public KafkaEventStreamer(
        ReserveRoom reserveRoom,
        KafkaTemplate<String, BookingResult> kafkaTemplate,
        @Value("${booking.topics.booking-result}") String bookingResultTopic
    ) {
        this.reserveRoom = reserveRoom;
        this.kafkaTemplate = kafkaTemplate;
        this.bookingResultTopic = bookingResultTopic;
    }

    @Override
    @KafkaListener(topics = "${booking.topics.booking-create}")
    public void consumeBookingCreateEvent(BookingCreate bookingCreate) {
        logger.info("Consuming RESERVE room: " + bookingCreate);

        BookingResult bookingResult = reserveRoom.reserve(bookingCreate);

        produceBookingResultEvent(bookingResult);
    }

    @Override
    public void produceBookingResultEvent(BookingResult bookingResult) {
        logger.info("Producing booking result event: " + bookingResult);

        kafkaTemplate.send(bookingResultTopic, bookingResult);
    }
}
