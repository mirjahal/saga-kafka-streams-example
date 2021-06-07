package br.com.concrete.booking.infrastructure.web.controller;

import br.com.concrete.booking.domain.business.CreateRoom;
import br.com.concrete.booking.domain.business.ListRoom;
import br.com.concrete.booking.domain.entity.Room;
import br.com.concrete.booking.infrastructure.web.model.request.CreateRoomRequest;
import br.com.concrete.booking.infrastructure.web.model.response.CreateRoomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("rooms")
public class RoomController {

    private final CreateRoom createRoom;
    private final ListRoom listRoom;

    public RoomController(CreateRoom createRoom, ListRoom listRoom) {
        this.createRoom = createRoom;
        this.listRoom = listRoom;
    }

    @PostMapping
    public ResponseEntity<CreateRoomResponse> create(@RequestBody CreateRoomRequest createRoomRequest) {
        Room room = createRoom.create(createRoomRequest.toRoomEntity());

        CreateRoomResponse createRoomResponse = new CreateRoomResponse(
            room.getRoomNumber(),
            room.getOrderId(),
            room.getPrice(),
            room.getStatus()
        );

        return ResponseEntity.status(CREATED).body(createRoomResponse);
    }

    @GetMapping
    public ResponseEntity<List<CreateRoomResponse>> getAll() {
        List<CreateRoomResponse> createRoomResponseList = listRoom
            .findAll()
            .stream()
            .map(room -> new CreateRoomResponse(
                room.getRoomNumber(),
                room.getOrderId(),
                room.getPrice(),
                room.getStatus())
            )
            .collect(Collectors.toList());

        return ResponseEntity.status(OK).body(createRoomResponseList);
    }
}
