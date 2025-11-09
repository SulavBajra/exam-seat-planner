package com.example.examseatplanner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.examseatplanner.dto.RoomPlanDTO;
import com.example.examseatplanner.dto.SeatAssignmentDTO;
import com.example.examseatplanner.mapper.SeatPlanMapper;
import com.example.examseatplanner.model.SeatingPlan;
import com.example.examseatplanner.service.SeatPlanService;

@RestController
@RequestMapping("/api/seating")
public class SeatingController {

    @Autowired
    private SeatPlanService seatPlanService;

    @PostMapping("/generate/{examId}")
    public ResponseEntity<?> generateAndSavePlan(@PathVariable Integer examId) {
        List<RoomPlanDTO> plan = seatPlanService.generateAndSaveSeatingPlan(examId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<?> getSavedPlan(@PathVariable Integer examId) {
        List<SeatingPlan> plan = seatPlanService.getSavedSeatingPlan(examId);
        return ResponseEntity.ok(plan);
    }

    @GetMapping("/search")
public ResponseEntity<?> searchStudentSeat(
        @RequestParam Integer examId,
        @RequestParam String programCode,
        @RequestParam Integer semester,
        @RequestParam Integer roll
) {
    try {
        SeatingPlan seat = seatPlanService.searchStudentSeat(examId, programCode, semester, roll);
        if (seat == null || seat.getRoom() == null) {
            throw new RuntimeException("Seat or assigned room not found");
        }

        SeatAssignmentDTO seatDTO = SeatPlanMapper.toDTO(seat);
        RoomPlanDTO roomDTO = new RoomPlanDTO(
            seat.getRoom().getRoomNo().toString(),
            List.of(List.of(seatDTO)) 
        );

        return ResponseEntity.ok(roomDTO);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}


}

