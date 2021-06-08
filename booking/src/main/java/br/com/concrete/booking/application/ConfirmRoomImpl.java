package br.com.concrete.booking.application;

import br.com.concrete.booking.domain.business.ConfirmRoom;
import br.com.concrete.booking.domain.entity.Room;
import br.com.concrete.booking.domain.repository.RoomRepository;

import javax.inject.Named;

import static br.com.concrete.booking.domain.entity.enums.RoomStatus.CONFIRM;

@Named
public class ConfirmRoomImpl implements ConfirmRoom {

    private final RoomRepository roomRepository;

    public ConfirmRoomImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room confirm(int roomNumber) {
        Room room = roomRepository
            .findById(roomNumber)
            .get();

        room.setStatus(CONFIRM);

        return roomRepository.save(room);
    }
}
