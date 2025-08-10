package com.example.examseatplanner.dto;

import com.example.examseatplanner.model.Seat;

public record SeatDTO(
        Long id,
        int benchNumber,
        int seatPosition,
        StudentSummaryDTO assignedStudent
) {
    public static SeatDTO fromEntity(Seat seat) {
        return new SeatDTO(
                seat.getId(),
                seat.getBenchNumber(),
                seat.getSeatPosition(),
                seat.getAssignedStudent() != null
                        ? StudentSummaryDTO.fromEntity(seat.getAssignedStudent())
                        : null
        );
    }
}
