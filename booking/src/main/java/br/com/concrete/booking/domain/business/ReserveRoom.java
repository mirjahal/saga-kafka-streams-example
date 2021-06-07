package br.com.concrete.booking.domain.business;

import br.com.concrete.BookingCreate;
import br.com.concrete.BookingResult;
import br.com.concrete.booking.domain.entity.Room;

public interface ReserveRoom {

    BookingResult reserve(BookingCreate bookingCreate);
}
