package com.example.examseatplanner.dto;

import java.time.LocalDate;
import java.util.List;

public record ExamResponseDTO(
        Integer id,
        LocalDate date,
        List<ProgramSemesterResponseDTO> programSemesters,  // ✅ Changed from programNames
        List<String> roomNames
) {}