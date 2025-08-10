package com.example.examseatplanner.mapper;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ExamMapper {

    public static Exam toEntity(ExamRequestDTO dto, List<Program> programs, List<Room> rooms) {
        Exam exam = new Exam();
        exam.setDate(LocalDate.parse(dto.date()));
        exam.setPrograms(programs);
        exam.setRooms(rooms);
        return exam;
    }

    public static ExamResponseDTO toDto(Exam exam) {
        return new ExamResponseDTO(
                exam.getId(),
                exam.getDate(),
                exam.getPrograms().stream()
                        .map(Program::getProgramName)
                        .collect(Collectors.toList()),
                exam.getRooms().stream()
                        .map(room -> room.getRoomNo().toString())  // converting Integer to String if needed
                        .collect(Collectors.toList())
        );
    }
}
