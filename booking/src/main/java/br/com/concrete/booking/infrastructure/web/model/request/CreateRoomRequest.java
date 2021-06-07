package br.com.concrete.booking.infrastructure.web.model.request;

import br.com.concrete.booking.domain.entity.Room;

import java.math.BigDecimal;

public class CreateRoomRequest {

    private int roomNumber;
    private Double price;

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Room toRoomEntity() {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setPrice(price);

        return room;
    }
}
