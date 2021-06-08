package br.com.concrete.booking.domain.business;

import br.com.concrete.booking.domain.entity.Room;

public interface ConfirmRoom {

    Room confirm(int roomNumber);
}
