package br.com.concrete.booking.infrastructure.event;

import br.com.concrete.BookingCancel;
import br.com.concrete.booking.domain.business.ReleaseRoom;
import br.com.concrete.booking.domain.event.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import javax.inject.Named;

@Named
public class BookingCancelEventConsumer implements EventConsumer<BookingCancel> {

    private final ReleaseRoom releaseRoom;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BookingCancelEventConsumer(ReleaseRoom releaseRoom) {
        this.releaseRoom = releaseRoom;
    }

    @Override
    @KafkaListener(topics = "${booking.topics.booking-cancel}")
    public void consume(BookingCancel bookingCancel) {
        logger.info("Consuming CANCEL booking: " + bookingCancel);

        releaseRoom.release(bookingCancel.getRoomNumber());
    }
}
