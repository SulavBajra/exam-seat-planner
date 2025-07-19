package com.example.examseatplanner.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record ExamRequestDTO(
        @NotNull(message = "Subject code is required")
        Integer subjectCode,

        @NotBlank(message = "Date is required")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in format yyyy-MM-dd")
        String date,

        @NotBlank(message = "Time is required")
        @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Time must be in 24-hour format HH:mm")
        String time,

        @NotEmpty(message = "Student list must not be empty")
        List<@NotNull(message = "Student ID cannot be null") Integer> studentIds,

        @NotEmpty(message = "Room list must not be empty")
        List<@NotNull(message = "Room ID cannot be null") Integer> roomIds
) {}
