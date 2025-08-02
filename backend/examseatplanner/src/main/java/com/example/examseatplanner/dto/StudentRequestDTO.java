package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

public record StudentRequestDTO(
        @NotBlank(message = "Enrolled year is required")
        String enrolledYear,

        @NotNull(message = "Program code is required")
        Integer programCode,

        @NotNull(message = "Semester is required")
        @Min(value = 1, message = "Semester must be at least 1")
        @Max(value = 8, message = "Semester must be at most 8")
        Integer semester,

        @NotNull(message = "Roll number is required")
        @Positive(message = "Roll number must be positive")
        Integer roll

) {}