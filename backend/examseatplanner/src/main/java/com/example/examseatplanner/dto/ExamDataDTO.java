package com.example.examseatplanner.dto;

import java.util.List;

public record ExamDataDTO(
        Integer examId,
        String examDate,
        List<ProgramDTO> programs,
        List<RoomDTO> rooms,
        List<StudentDTO> students
) {}

