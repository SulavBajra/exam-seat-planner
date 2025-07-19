package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record ProgramDTO(
        @NotBlank(message = "Program name is required")
        String programName,

        @NotNull(message = "Program code is required")
        @Min(value = 1, message = "Program code must be a positive integer")
        Integer programCode,

        @NotNull(message = "Subject list cannot be null")
        @Size(min = 1, message = "At least one subject is required")
        List<@NotBlank(message = "Subject name cannot be blank") String> subjectNames
) {}
