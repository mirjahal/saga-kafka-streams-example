package br.com.concrete.booking.application;

import br.com.concrete.booking.domain.business.ListRoom;
import br.com.concrete.booking.domain.entity.Room;
import br.com.concrete.booking.domain.repository.RoomRepository;

import javax.inject.Named;
import java.util.List;

@Named
public class ListRoomImpl implements ListRoom {

    private final RoomRepository roomRepository;

    public ListRoomImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> findAll() {
        return roomRepository.findAll();
    }
}
