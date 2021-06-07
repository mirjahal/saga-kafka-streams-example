package br.com.concrete.order.infrastructure.web.controller;

import br.com.concrete.order.domain.business.CreateOrder;
import br.com.concrete.order.domain.business.ListOrder;
import br.com.concrete.order.domain.entity.Order;
import br.com.concrete.order.infrastructure.web.model.request.CreateOrderRequest;
import br.com.concrete.order.infrastructure.web.model.response.CreateOrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("orders")
public class OrderController {

    private final CreateOrder createOrder;
    private final ListOrder listOrder;

    public OrderController(CreateOrder createOrder, ListOrder listOrder) {
        this.createOrder = createOrder;
        this.listOrder = listOrder;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> create(@RequestBody CreateOrderRequest createOrderRequest) {
        Order order = createOrder.create(createOrderRequest.getRoomNumber());

        CreateOrderResponse createOrderResponse = new CreateOrderResponse(order.getId(), order.getStatus().name());

        return ResponseEntity.status(CREATED).body(createOrderResponse);
    }

    @GetMapping
    public ResponseEntity<List<CreateOrderResponse>> getAll() {
        List<CreateOrderResponse> createOrderResponseList = listOrder
            .findAll()
            .stream()
            .map(order -> new CreateOrderResponse(order.getId(), order.getStatus().name()))
            .collect(Collectors.toList());

        return ResponseEntity.status(OK).body(createOrderResponseList);
    }
}
