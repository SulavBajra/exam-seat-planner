package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.SeatAllocationResponseDTO;
import com.example.examseatplanner.dto.SeatDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.Seat;
import com.example.examseatplanner.service.ExamService;
import com.example.examseatplanner.service.SeatAllocationService;
import com.example.examseatplanner.service.SeatAllocationService.SeatAllocationResult;
import com.example.examseatplanner.service.SeatAllocationDTOService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/seat-allocation")
public class SeatAllocationController {

    private final SeatAllocationService seatAllocationService;
    private final ExamService examService;
    private final SeatAllocationDTOService dtoService;

    public SeatAllocationController(SeatAllocationService seatAllocationService,
                                    ExamService examService,
                                    SeatAllocationDTOService dtoService) {
        this.seatAllocationService = seatAllocationService;
        this.examService = examService;
        this.dtoService = dtoService;
    }

    @GetMapping("/assignments/{examId}")
    public ResponseEntity<Map<String, List<SeatDTO>>> getAssignments(@PathVariable Integer examId) {
        Map<String, List<SeatDTO>> assignments = seatAllocationService.getSeatAssignments(examId);
        return ResponseEntity.ok(assignments);
    }

    /**
     * Allocate seats for a specific exam
     */
    @PostMapping("/exam/{examId}/allocate")
    public ResponseEntity<SeatAllocationResponseDTO> allocateSeats(@PathVariable Integer examId) {
        try {
            Optional<Exam> examOpt = examService.getExamEntityById(examId);
            if (examOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            SeatAllocationResult result = seatAllocationService.allocateSeatsByProgramColumns(examOpt.get());
            Map<Room, Seat[][][]> seatingChart = seatAllocationService.getSeatingChart(examOpt.get());
            SeatAllocationResponseDTO responseDTO = dtoService.convertToDTO(result, seatingChart);

            return ResponseEntity.ok(responseDTO);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Get seating chart for an exam (3D visualization data)
     */
    @GetMapping("/exam/{examId}/seating-chart")
    public ResponseEntity<Map<Room, Seat[][][]>> getSeatingChart(@PathVariable Integer examId) {
        Optional<Exam> examOpt = examService.getExamEntityById(examId);
        if (examOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Map<Room, Seat[][][]> seatingChart = seatAllocationService.getSeatingChart(examOpt.get());
        return ResponseEntity.ok(seatingChart);
    }

    /**
     * Clear all seat assignments for an exam
     */
    @DeleteMapping("/exam/{examId}/clear")
    public ResponseEntity<String> clearSeatAssignments(@PathVariable Integer examId) {
        Optional<Exam> examOpt = examService.getExamEntityById(examId);
        if (examOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        seatAllocationService.clearSeatAssignments(examOpt.get());
        return ResponseEntity.ok("Seat assignments cleared successfully");
    }

    /**
     * Validate seat allocation for rule violations
     */
    @GetMapping("/exam/{examId}/validate")
    public ResponseEntity<Map<String, Object>> validateSeatAllocation(@PathVariable Integer examId) {
        Optional<Exam> examOpt = examService.getExamEntityById(examId);
        if (examOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<String> violations = seatAllocationService.validateSeatAllocation(examOpt.get());

        Map<String, Object> response = Map.of(
                "isValid", violations.isEmpty(),
                "violations", violations,
                "totalViolations", violations.size()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get allocation statistics for an exam
     */
    @GetMapping("/exam/{examId}/statistics")
    public ResponseEntity<Map<String, Object>> getAllocationStatistics(@PathVariable Integer examId) {
        Optional<Exam> examOpt = examService.getExamEntityById(examId);
        if (examOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Exam exam = examOpt.get();
        Map<Room, Seat[][][]> seatingChart = seatAllocationService.getSeatingChart(exam);
        List<String> violations = seatAllocationService.validateSeatAllocation(exam);

        Map<String, Object> statistics = dtoService.generateAllocationStatistics(seatingChart, violations);

        return ResponseEntity.ok(statistics);
    }

    /**
     * Get visual representation of seating arrangement
     */
    @GetMapping("/exam/{examId}/room/{roomNo}/visualization")
    public ResponseEntity<String> getSeatingVisualization(@PathVariable Integer examId,
                                                          @PathVariable Integer roomNo) {
        Optional<Exam> examOpt = examService.getExamEntityById(examId);
        if (examOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Room targetRoom = examOpt.get().getRooms().stream()
                .filter(room -> room.getRoomNo().equals(roomNo))
                .findFirst()
                .orElse(null);

        if (targetRoom == null) {
            return ResponseEntity.badRequest().body("Room not found in exam");
        }

        Map<Room, Seat[][][]> seatingChart = seatAllocationService.getSeatingChart(examOpt.get());
        Seat[][][] roomSeats = seatingChart.get(targetRoom);

        if (roomSeats == null) {
            return ResponseEntity.badRequest().body("No seating data found for room");
        }

        String visualization = dtoService.generateSeatingVisualization(targetRoom, roomSeats);
        return ResponseEntity.ok(visualization);
    }
}
