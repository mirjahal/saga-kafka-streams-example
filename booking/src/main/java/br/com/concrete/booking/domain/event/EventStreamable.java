package br.com.concrete.booking.domain.event;

import br.com.concrete.BookingCreate;
import br.com.concrete.BookingResult;

public interface EventStreamable {

    void consumeBookingCreateEvent(BookingCreate bookingCreate);
    void produceBookingResultEvent(BookingResult bookingResult);
}
