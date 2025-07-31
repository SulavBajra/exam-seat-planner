package com.example.examseatplanner.dto;

import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Subject;

import java.util.List;

public record StudentResponseDTO(
        String studentId,
        String enrolledYear,
        int semester,
        int roll,
        String programName,
        List<String> subjectNames
) {
    public static StudentResponseDTO fromEntity(Student student) {
        return new StudentResponseDTO(
                student.getStudentId(),
                student.getEnrolledYear(),
                student.getSemester(),
                student.getRoll(),
                student.getProgram().getProgramName(),
                student.getSubjects().stream()
                        .map(Subject::getSubjectName)
                        .toList()
        );
    }
}
