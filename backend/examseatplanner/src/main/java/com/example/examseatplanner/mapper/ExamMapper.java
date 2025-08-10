package com.example.examseatplanner.mapper;

import com.example.examseatplanner.dto.*;
import com.example.examseatplanner.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExamMapper {

    public static Exam toEntity(ExamRequestDTO dto, List<Program> programs, List<Room> rooms) {
        Exam exam = new Exam();
        exam.setDate(LocalDate.parse(dto.date()));
        exam.setRooms(rooms);

        // ✅ Create ExamProgramSemester entities
        List<ExamProgramSemester> programSemesters = new ArrayList<>();

        for (ProgramSemesterDTO psDto : dto.programSemesters()) {
            Program program = programs.stream()
                    .filter(p -> p.getProgramCode().equals(psDto.programCode()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Program not found: " + psDto.programCode()));

            Student.Semester semester = Student.Semester.values()[psDto.semester() - 1];

            ExamProgramSemester eps = new ExamProgramSemester(exam, program, semester);
            programSemesters.add(eps);
        }

        exam.setProgramSemesters(programSemesters);
        return exam;
    }

    public static ExamResponseDTO toDto(Exam exam) {
        List<ProgramSemesterResponseDTO> programSemesters = exam.getProgramSemesters()
                .stream()
                .map(eps -> new ProgramSemesterResponseDTO(
                        eps.getProgram().getProgramCode(),
                        eps.getProgram().getProgramName(),
                        eps.getSemester().ordinal() + 1,
                        eps.getSemester().name()
                ))
                .collect(Collectors.toList());

        return new ExamResponseDTO(
                exam.getId(),
                exam.getDate(),
                programSemesters,
                exam.getRooms().stream()
                        .map(room -> room.getRoomNo().toString())
                        .collect(Collectors.toList())
        );
    }
}