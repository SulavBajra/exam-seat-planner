package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

public record SubjectDTO(
        @NotNull(message = "Subject code is required")
        Integer subjectCode,

        @NotBlank(message = "Subject name is required")
        String subjectName,

        @NotNull(message = "Semester is required")
        @Min(value = 1, message = "Semester must be at least 1")
        @Max(value = 8, message = "Semester must be at most 8")
        Integer semester,

        @NotNull(message = "Program code is required")
        Integer programCode
) {}