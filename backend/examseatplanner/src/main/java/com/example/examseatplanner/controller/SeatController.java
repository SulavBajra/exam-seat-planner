package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.SeatDTO;
import com.example.examseatplanner.model.Seat;
import com.example.examseatplanner.repository.SeatRepository;
import com.example.examseatplanner.service.SeatAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatRepository seatRepository;
    private final SeatAllocationService seatAllocationService;

    public SeatController(SeatRepository seatRepository,
                          SeatAllocationService seatAllocationService) {
        this.seatRepository = seatRepository;
        this.seatAllocationService =seatAllocationService;
    }

    @GetMapping("/assignments")
    public Map<String, List<SeatDTO>> getAssignments() {
        List<Seat> seats = seatRepository.findAll();

        return seats.stream()
                .collect(Collectors.groupingBy(
                        seat -> String.valueOf(seat.getRoom().getRoomNo()),
                        Collectors.mapping(SeatDTO::fromEntity, Collectors.toList())
                ));
    }

    @GetMapping("/{examId}")
    public ResponseEntity<Map<String, List<SeatDTO>>> getExamSeatInfo(@PathVariable Integer examId) { // ✅ Changed from Long to Integer
        Map<String, List<SeatDTO>> assignments = seatAllocationService.getSeatAssignmentsByExamId(examId);
        return ResponseEntity.ok(assignments);
    }

    // In SeatAllocationController.java
    @GetMapping("/assignments/{examId}")
    public ResponseEntity<Map<String, List<SeatDTO>>> getAssignments(@PathVariable Integer examId) { // ✅ Changed from Long to Integer
        Map<String, List<SeatDTO>> assignments = seatAllocationService.getSeatAssignments(examId);
        return ResponseEntity.ok(assignments);
    }
}
