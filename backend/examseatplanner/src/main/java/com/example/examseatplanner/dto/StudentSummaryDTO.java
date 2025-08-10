package com.example.examseatplanner.dto;

import com.example.examseatplanner.model.Student;

public record StudentSummaryDTO(
        Integer studentId,
        int roll,
        Integer programCode,
        String semester
) {
    public static StudentSummaryDTO fromEntity(Student student) {
        return new StudentSummaryDTO(
                student.getStudentId(),
                student.getRoll(),
                student.getProgram() != null ? student.getProgram().getProgramCode() : null,
                student.getSemester() != null ? student.getSemester().name() : null
        );
    }
}
