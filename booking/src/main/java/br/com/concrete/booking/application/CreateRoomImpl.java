package br.com.concrete.booking.application;

import br.com.concrete.booking.domain.business.CreateRoom;
import br.com.concrete.booking.domain.entity.Room;
import br.com.concrete.booking.domain.repository.RoomRepository;

import javax.inject.Named;

import static br.com.concrete.booking.domain.entity.enums.RoomStatus.FREE;

@Named
public class CreateRoomImpl implements CreateRoom {

    private final RoomRepository roomRepository;

    public CreateRoomImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room create(Room room) {
        room.setStatus(FREE);
        return roomRepository.save(room);
    }
}
