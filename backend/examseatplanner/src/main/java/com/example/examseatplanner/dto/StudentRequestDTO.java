package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record StudentRequestDTO(
        @NotBlank(message = "Enroll year is required")
        String enrollYear,

        @NotBlank(message = "Program is required")
        String program,

        @NotNull(message = "Semester is required")
        @Min(value = 1, message = "Semester must be at least 1")
        @Max(value = 8, message = "Semester must be at most 8")
        Integer semester,

        @NotNull(message = "Roll is required")
        @Positive(message = "Roll number must be positive")
        Integer roll,

        @NotEmpty(message = "At least one subject must be selected")
        List<@NotBlank(message = "Subject name cannot be blank") String> subjects
) {}
