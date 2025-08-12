package com.example.examseatplanner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record BulkExamRequestDTO(
        @NotEmpty(message = "Exam list must not be empty")
        @Valid
        List<ExamRequestDTO> exams
) {}

