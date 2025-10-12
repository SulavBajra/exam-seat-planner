package com.example.examseatplanner.dto;



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

