package br.com.concrete.booking.infrastructure.event;

import br.com.concrete.BookingConfirm;
import br.com.concrete.booking.domain.business.ConfirmRoom;
import br.com.concrete.booking.domain.event.EventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

import javax.inject.Named;

@Named
public class BookingConfirmEventConsumer implements EventConsumer<BookingConfirm> {

    private final ConfirmRoom confirmRoom;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BookingConfirmEventConsumer(ConfirmRoom confirmRoom) {
        this.confirmRoom = confirmRoom;
    }

    @Override
    @KafkaListener(topics = "${booking.topics.booking-confirm}")
    public void consume(BookingConfirm bookingConfirm) {
        logger.info("Consuming CONFIRM booking: " + bookingConfirm);

        confirmRoom.confirm(bookingConfirm.getRoomNumber());
    }
}
