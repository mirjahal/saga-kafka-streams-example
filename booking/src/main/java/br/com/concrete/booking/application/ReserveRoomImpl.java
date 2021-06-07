package br.com.concrete.booking.application;

import br.com.concrete.BookingCreate;
import br.com.concrete.BookingResult;
import br.com.concrete.BookingStatus;
import br.com.concrete.booking.domain.business.ReserveRoom;
import br.com.concrete.booking.domain.entity.Room;
import br.com.concrete.booking.domain.entity.enums.RoomStatus;
import br.com.concrete.booking.domain.repository.RoomRepository;

import javax.inject.Named;
import java.util.Optional;
import java.util.UUID;

import static br.com.concrete.booking.domain.entity.enums.RoomStatus.FREE;
import static br.com.concrete.booking.domain.entity.enums.RoomStatus.RESERVE;

@Named
public class ReserveRoomImpl implements ReserveRoom {

    private final RoomRepository roomRepository;

    public ReserveRoomImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public BookingResult reserve(BookingCreate bookingCreate) {
        Optional<Room> optionalRoom = roomRepository.findById(bookingCreate.getRoomNumber());
        if (!optionalRoom.isPresent()) {
            return new BookingResult(
                null, null, null, null, "There is no room with room number."
            );
        }

        Room room = optionalRoom.get();
        if (room.getStatus().equals(FREE)) {
            room.setOrderId(UUID.fromString(bookingCreate.getOrderId().toString()));
            room.setStatus(RoomStatus.RESERVE);

            roomRepository.save(room);

            return new BookingResult(
                room.getOrderId().toString(),
                room.getRoomNumber(),
                BookingStatus.RESERVE,
                room.getPrice(),
                null
            );
        }

        return new BookingResult(
            bookingCreate.getOrderId().toString(),
            bookingCreate.getRoomNumber(),
            bookingCreate.getStatus(),
            room.getPrice(),
            "The room is not available."
        );
    }
}
