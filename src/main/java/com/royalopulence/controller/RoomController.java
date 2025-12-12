
package com.royalopulence.controller;


import com.royalopulence.dto.room.RoomResponse;
import com.royalopulence.service.base.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {


private final RoomService roomService;


public RoomController(RoomService roomService) {
this.roomService = roomService;
}


@GetMapping("/search")
public ResponseEntity<List<RoomResponse>> search(
@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
@RequestParam(required = false) Integer guests
) {
return ResponseEntity.ok(roomService.searchAvailableRooms(checkIn, checkOut, guests));
}
}