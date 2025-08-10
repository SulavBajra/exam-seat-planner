package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record ExamRequestDTO(
        @NotBlank(message = "Date is required")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in format yyyy-MM-dd")
        String date,

        @NotEmpty(message = "Program list must not be empty")
        List<@NotNull(message = "Program code cannot be null") Integer> programCodes,

        @NotEmpty(message = "Room list must not be empty")
        List<@NotNull(message = "Room number cannot be null") Integer> roomNumbers
) {}
