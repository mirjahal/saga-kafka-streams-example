package br.com.concrete.booking.infrastructure.event;

import br.com.concrete.BookingResult;
import br.com.concrete.booking.domain.event.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;

import javax.inject.Named;

@Named
public class BookingResultEventProducer implements EventProducer<BookingResult> {

    private final KafkaTemplate<String, BookingResult> kafkaTemplate;
    private final String bookingResultTopic;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BookingResultEventProducer(
        KafkaTemplate<String, BookingResult> kafkaTemplate,
        @Value("${booking.topics.booking-result}") String bookingResultTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.bookingResultTopic = bookingResultTopic;
    }

    @Override
    public void produce(BookingResult bookingResult) {
        logger.info("Producing booking result event: " + bookingResult);

        kafkaTemplate.send(bookingResultTopic, bookingResult);
    }
}
