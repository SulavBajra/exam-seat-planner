package com.example.examseatplanner.dto;

public record ProgramSemesterResponseDTO(
        Integer programCode,
        String programName,
        Integer semester
) {}