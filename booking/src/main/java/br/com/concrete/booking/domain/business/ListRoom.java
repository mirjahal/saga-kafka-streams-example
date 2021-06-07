package br.com.concrete.booking.domain.business;

import br.com.concrete.booking.domain.entity.Room;

import java.util.List;

public interface ListRoom {

    List<Room> findAll();
}
