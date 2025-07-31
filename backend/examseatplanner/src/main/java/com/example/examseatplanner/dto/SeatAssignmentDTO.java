package com.example.examseatplanner.dto;

public record SeatAssignmentDTO( String studentId,
                                 Integer roomNo,
                                 int row,
                                 int column) {
}
