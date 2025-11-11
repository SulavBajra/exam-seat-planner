package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.RoomRequestDTO;
import com.example.examseatplanner.dto.RoomResponseDTO;
import com.example.examseatplanner.service.RoomService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<RoomResponseDTO> getAllRooms() {

        return roomService.getAllRooms();
    }

    @GetMapping("/{roomNo}")
    public ResponseEntity<RoomResponseDTO> getRoomById(@PathVariable Integer roomNo) {
        return roomService.getRoomById(roomNo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public RoomResponseDTO createRoom(@RequestBody RoomRequestDTO dto) {
        return roomService.saveRoom(dto);
    }

    @PutMapping("/{roomNo}")
    public ResponseEntity<RoomResponseDTO> updateRoom(
            @PathVariable Integer roomNo,
            @Valid @RequestBody RoomRequestDTO dto) { 
        if (roomService.getRoomById(roomNo).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        dto = new RoomRequestDTO(roomNo, dto.seatingCapacity(), dto.numRow(), dto.seatsPerBench(), dto.roomColumn());
        RoomResponseDTO updatedRoom = roomService.saveRoom(dto);
        return ResponseEntity.ok(updatedRoom);
    }


    @DeleteMapping("/{roomNo}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Integer roomNo) {
        if (roomService.getRoomById(roomNo).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        roomService.deleteRoom(roomNo);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/booked/{roomNo}")
    public boolean isRoomBooked(@PathVariable Integer roomNo) {
        if (roomService.getRoomById(roomNo).isEmpty()) {
            return false;
        }
        return roomService.hasUpcomingExams(roomNo);    
    }
    

    @GetMapping("/search")
    public List<RoomResponseDTO> getRoomsWithMinCapacity(@RequestParam int minCapacity) {
        return roomService.getRoomsWithMinCapacity(minCapacity);
    }
}
