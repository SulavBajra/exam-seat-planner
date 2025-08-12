package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

public record RoomRequestDTO(
        @NotNull(message = "Room number is required")
        Integer roomNo,

        @Min(value = 1, message = "Seating capacity must be at least 1")
        int seatingCapacity,

        @Min(value = 1, message = "Number of rows must be at least 1")
        int numRow
) {}
