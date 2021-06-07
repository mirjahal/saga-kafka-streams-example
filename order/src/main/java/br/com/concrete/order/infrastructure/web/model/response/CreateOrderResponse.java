package br.com.concrete.order.infrastructure.web.model.response;

import java.util.UUID;

public class CreateOrderResponse {

    private UUID id;
    private String status;

    public CreateOrderResponse() {
    }

    public CreateOrderResponse(UUID id, String status) {
        this.id = id;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
