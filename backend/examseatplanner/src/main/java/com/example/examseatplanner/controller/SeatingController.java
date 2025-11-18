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
import com.example.examseatplanner.service.SeatPlanService;

@RestController
@RequestMapping("/api/seating")
public class SeatingController {

    @Autowired
    private SeatPlanService seatPlanService;

   @PostMapping("/generate/{examId}")
    public ResponseEntity<String> generateAndSavePlan(@PathVariable Integer examId) {
        try {
            seatPlanService.generateAndSaveSeatingPlan(examId);
            return ResponseEntity.ok("Successfully generated seat plan");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error generating plan: " + e.getMessage());
        }
    }

    @GetMapping("/{examId}")
    public ResponseEntity<?> getSavedPlan(@PathVariable Integer examId) {
        try {
            List<RoomPlanDTO> plan = seatPlanService.getSavedSeatingPlanGroupedByRoom(examId);
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body("Plan not found: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchStudentSeat(
            @RequestParam Integer examId,
            @RequestParam String programCode,
            @RequestParam Integer semester,
            @RequestParam Integer roll
    ) {
        try {
            SeatAssignmentDTO seatDTO = seatPlanService.searchStudentSeat(examId, programCode, semester, roll);
            return ResponseEntity.ok(seatDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(e.getMessage());
        }
    }
}

