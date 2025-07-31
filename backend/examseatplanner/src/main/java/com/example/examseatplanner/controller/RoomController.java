package com.example.examseatplanner.controller;

import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Room savedRoom = roomService.saveRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }

    @GetMapping("/{roomNo}")
    public ResponseEntity<Room> getRoomByNo(@PathVariable Integer roomNo) {
        return roomService.getRoomByNo(roomNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{roomNo}")
    public ResponseEntity<Room> updateRoom(@PathVariable Integer roomNo,
                                           @Valid @RequestBody Room room) {
        room.setRoomNo(roomNo);
        Room updatedRoom = roomService.saveRoom(room);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/{roomNo}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer roomNo) {
        roomService.deleteRoom(roomNo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam String date,
            @RequestParam String time) {
        List<Room> availableRooms = roomService.getAvailableRooms(date, time);
        return ResponseEntity.ok(availableRooms);
    }
}
