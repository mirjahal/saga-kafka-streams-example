package br.com.concrete.booking.application;

import br.com.concrete.booking.domain.business.ReleaseRoom;
import br.com.concrete.booking.domain.entity.Room;
import br.com.concrete.booking.domain.entity.enums.RoomStatus;
import br.com.concrete.booking.domain.repository.RoomRepository;

import javax.inject.Named;

import static br.com.concrete.booking.domain.entity.enums.RoomStatus.FREE;

@Named
public class ReleaseRoomImpl implements ReleaseRoom {

    private final RoomRepository roomRepository;

    public ReleaseRoomImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room release(int roomNumber) {
        Room room = roomRepository
            .findById(roomNumber)
            .get();

        room.setStatus(FREE);

        return roomRepository.save(room);
    }
}
