package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.RoomSeatDTO;
import com.example.examseatplanner.dto.SeatAssignmentDTO;
import com.example.examseatplanner.service.SeatAllocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatAssignmentController {

    private final SeatAllocationService seatAllocationService;

    public SeatAssignmentController(SeatAllocationService seatAllocationService) {
        this.seatAllocationService = seatAllocationService;
    }

    @GetMapping("/seat-assignments/exam/{examId}")
    public List<RoomSeatDTO> getSeatsByExam(@PathVariable Integer examId) {
        return seatAllocationService.getSeatAssignmentsByExam(examId);
    }

    @GetMapping("/exam/{examId}")
    public ResponseEntity<List<SeatAssignmentDTO>> getSeatPlan(@PathVariable Integer examId) {
        return ResponseEntity.ok(seatAllocationService.getSeatPlanByExamId(examId));
    }
}
