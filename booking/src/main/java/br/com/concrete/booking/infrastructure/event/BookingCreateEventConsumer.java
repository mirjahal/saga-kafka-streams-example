package br.com.concrete.booking.infrastructure.event;

import br.com.concrete.BookingCreate;
import br.com.concrete.BookingResult;
import br.com.concrete.booking.domain.business.ReserveRoom;
import br.com.concrete.booking.domain.event.EventConsumer;
import br.com.concrete.booking.domain.event.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import javax.inject.Named;

@Named
public class BookingCreateEventConsumer implements EventConsumer<BookingCreate> {

    private final ReserveRoom reserveRoom;
    private final EventProducer<BookingResult> eventProducer;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BookingCreateEventConsumer(ReserveRoom reserveRoom, EventProducer<BookingResult> eventProducer) {
        this.reserveRoom = reserveRoom;
        this.eventProducer = eventProducer;
    }

    @Override
    @KafkaListener(topics = "${booking.topics.booking-create}")
    public void consume(BookingCreate bookingCreate) {
        logger.info("Consuming RESERVE room: " + bookingCreate);

        BookingResult bookingResult = reserveRoom.reserve(bookingCreate);

        eventProducer.produce(bookingResult);
    }
}
