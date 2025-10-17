package com.example.examseatplanner.mapper;

import com.example.examseatplanner.dto.*;
import com.example.examseatplanner.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExamMapper {

    public static Exam toEntity(ExamRequestDTO dto, List<Program> programs, List<Room> rooms) {
        Exam exam = new Exam();
        exam.setStartDate(LocalDate.parse(dto.startDate()));
        exam.setEndDate(LocalDate.parse(dto.endDate()));
        exam.setRooms(rooms);

        List<ExamProgramSemester> programSemesters = new ArrayList<>();

            Map<Integer, Program> programMap = programs.stream()
                    .collect(Collectors.toMap(Program::getProgramCode, p -> p));

            for (ProgramSemesterDTO psDto : dto.programSemesters()) {
                Program program = programMap.get(psDto.programCode());
                if (program == null) {
                    throw new IllegalArgumentException("Program not found: " + psDto.programCode());
                }
            int semValue = psDto.semester();
            if (semValue < 1 || semValue > Student.Semester.values().length) {
                throw new IllegalArgumentException("Invalid semester: " + semValue);
            }
            Student.Semester semester = Student.Semester.values()[semValue - 1];


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
                exam.getStartDate(),
                exam.getEndDate(),
                programSemesters,
                exam.getRooms().stream()
                        .map(room -> room.getRoomNo().toString())
                        .collect(Collectors.toList())
        );
    }
}