package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

public record RoomRequestDTO(
        @NotNull(message = "Room number is required")
        Integer roomNo,

        @Min(value = 1, message = "Number of rows must be at least 1")
        int numRow,

        @Min(value = 1, message = "Number of seats per bench must be at least 1")
        int seatsPerBench,

        @Min(value = 1, message = "Number of columns must be at least 1")
        int roomColumn
) {}
