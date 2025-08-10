package com.example.examseatplanner.dto;

import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.Seat;
import com.example.examseatplanner.model.Student;

import java.util.List;

/**
 * DTO for seat allocation response
 */
public record SeatAllocationResponseDTO(
        Integer examId,
        String examDate,
        int totalStudents,
        int totalSeats,
        int remainingCapacity,
        List<RoomSeatingDTO> roomSeating,
        List<String> allocationNotes
) {}

