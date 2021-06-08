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

import static br.com.concrete.BookingStatus.ERROR;
import static br.com.concrete.BookingStatus.RESERVE;
import static br.com.concrete.booking.domain.entity.enums.RoomStatus.FREE;

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
                bookingCreate.getOrderId().toString(),
                bookingCreate.getRoomNumber(),
                ERROR,
                null,
                "There is no room with room number."
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
                RESERVE,
                room.getPrice(),
                "Success"
            );
        }

        return new BookingResult(
            bookingCreate.getOrderId().toString(),
            bookingCreate.getRoomNumber(),
            ERROR,
            room.getPrice(),
            "The room is not available."
        );
    }
}
