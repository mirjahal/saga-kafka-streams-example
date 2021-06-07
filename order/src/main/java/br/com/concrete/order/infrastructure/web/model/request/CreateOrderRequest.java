package br.com.concrete.order.infrastructure.web.model.request;

public class CreateOrderRequest {

    private int roomNumber;

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
}
