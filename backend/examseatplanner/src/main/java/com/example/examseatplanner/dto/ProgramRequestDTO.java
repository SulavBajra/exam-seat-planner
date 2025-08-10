package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

public record ProgramRequestDTO(
        @NotBlank(message = "Program name is required")
        String programName,

        @NotNull(message = "Program code is required")
        @Min(value = 1, message = "Program code must be a positive integer")
        Integer programCode
) {}