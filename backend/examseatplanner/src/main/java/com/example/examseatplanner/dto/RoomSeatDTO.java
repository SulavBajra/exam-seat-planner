package com.example.examseatplanner.dto;

import java.util.List;

public record RoomSeatDTO(
        Integer roomNo,
        int numRow,
        int numColumn,
        List<SeatAssignmentDTO> seats
) {}
