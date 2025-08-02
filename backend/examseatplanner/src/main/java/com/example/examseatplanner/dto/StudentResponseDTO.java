package com.example.examseatplanner.dto;

import com.example.examseatplanner.model.Student;

public record StudentResponseDTO(
        String studentId,
        String enrolledYear,
        int semester,
        int roll,
        String programName,
        Integer programCode
) {
    public static StudentResponseDTO fromEntity(Student student) {
        return new StudentResponseDTO(
                student.getStudentId(),
                student.getEnrolledYear(),
                student.getSemester(),
                student.getRoll(),
                student.getProgram().getProgramName(),
                student.getProgram().getProgramCode()
        );
    }
}