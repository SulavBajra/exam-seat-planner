package com.example.examseatplanner.dto;

import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Room;

import java.util.List;

public record ExamResponseDTO(
        Integer examId,
        String subjectName,
        Integer subjectCode,
        String date,
        String programName,
        Integer semester,
        List<Integer> roomNumbers
) {

    public static ExamResponseDTO fromEntity(Exam exam) {
        return new ExamResponseDTO(
                exam.getId(),
                exam.getSubject().getSubjectName(),
                exam.getSubject().getSubjectCode(),
                exam.getDate().toString(), // LocalDate.toString() gives yyyy-MM-dd format
                exam.getSubject().getProgram().getProgramName(),
                exam.getSubject().getSemester(),
                exam.getRooms().stream()
                        .map(Room::getRoomNo)
                        .toList()
        );
    }
}