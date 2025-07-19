package com.example.examseatplanner.dto;

import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Room;

import java.util.List;

public record ExamResponseDTO(Integer examId,
                              String subjectName,
                              String date,
                              String time,
                              List<String> studentCode,
                              List<Integer> roomNo) {

    public static ExamResponseDTO fromEntity(Exam exam) {
        return new ExamResponseDTO(
                exam.getId(),
                exam.getSubject().getSubjectName(),
                exam.getDate(),
                exam.getTime(),
                exam.getStudents().stream()
                        .map(Student::getStudentId)
                        .toList(),
                exam.getRooms().stream().map(Room::getRoomNo).toList()
        );
    }
}
