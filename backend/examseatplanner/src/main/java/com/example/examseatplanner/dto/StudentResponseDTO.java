package com.example.examseatplanner.dto;

import com.example.examseatplanner.model.Student;

public record StudentResponseDTO(
        Integer studentId,
        int semester,
        int roll,
        String programName,
        Integer programCode
) {
    public static StudentResponseDTO fromEntity(Student student) {
        return new StudentResponseDTO(
                student.getStudentId(),
                student.getSemester().ordinal() + 1,
                student.getRoll(),
                student.getProgram().getProgramName(),
                student.getProgram().getProgramCode()
        );
    }

}