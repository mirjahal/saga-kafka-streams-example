package br.com.concrete.booking.infrastructure.web.model.response;

import br.com.concrete.booking.domain.entity.enums.RoomStatus;

import java.util.UUID;

public class CreateRoomResponse {

    private int roomNumber;
    private UUID orderId;
    private Double price;
    private RoomStatus status;

    public CreateRoomResponse() {
    }

    public CreateRoomResponse(int roomNumber, UUID orderId, Double price, RoomStatus status) {
        this.roomNumber = roomNumber;
        this.orderId = orderId;
        this.price = price;
        this.status = status;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
