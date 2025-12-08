package com.royalopulence.controller;

import model.operation.Room;
import dto.room.RoomRequest;
import dto.room.RoomResponse;
import service.base.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> create(@RequestBody RoomRequest req) {
        return ResponseEntity.ok(roomService.create(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.get(id));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomResponse> update(@PathVariable Long id, @RequestBody RoomRequest req) {
        return ResponseEntity.ok(roomService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
