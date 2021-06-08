package br.com.concrete.booking.domain.business;

import br.com.concrete.booking.domain.entity.Room;

public interface ReleaseRoom {

    Room release(int roomNumber);
}
