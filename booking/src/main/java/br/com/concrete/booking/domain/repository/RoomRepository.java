package br.com.concrete.booking.domain.repository;

import br.com.concrete.booking.domain.entity.Room;

import java.util.List;
import java.util.Optional;

public interface RoomRepository {

    Room save(Room room);
    List<Room> findAll();
    Optional<Room> findById(int roomNumber);
}
