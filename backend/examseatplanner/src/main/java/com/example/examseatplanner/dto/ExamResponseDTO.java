package com.example.examseatplanner.dto;

import java.time.LocalDate;
import java.util.List;

public record ExamResponseDTO(
        Integer id,
        LocalDate startDate,
        LocalDate endDate,
        List<ProgramSemesterResponseDTO> programSemesters, 
        List<String> roomNames
) {}