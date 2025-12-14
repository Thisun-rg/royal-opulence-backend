
package com.royalopulence.controller;

import com.royalopulence.model.core.RoomType;
import com.royalopulence.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeRepository roomTypeRepository;

    @PostMapping
    public RoomType createRoomType(@RequestBody RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    @GetMapping
    public Iterable<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }
}
