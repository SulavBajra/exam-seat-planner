package com.example.examseatplanner.dto;

import java.util.List;

public record ExamDataDTO(
        Integer examId,
        String examDate,
        List<ProgramResponseDTO> programs,
        List<RoomResponseDTO> rooms,
        List<StudentDTO> students
) {}

