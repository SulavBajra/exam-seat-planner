package com.example.examseatplanner.dto;

public record SeatPositionDTO(
        int row,
        int col,
        String studentId
) {}
