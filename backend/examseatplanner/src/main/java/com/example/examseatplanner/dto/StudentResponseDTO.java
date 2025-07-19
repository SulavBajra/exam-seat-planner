package com.example.examseatplanner.dto;

import java.util.List;

public record StudentResponseDTO(
        String studentId,
        String enrolledYear,
        int semester,
        int roll,
        String programName,
        List<String> subjectNames
) {}
