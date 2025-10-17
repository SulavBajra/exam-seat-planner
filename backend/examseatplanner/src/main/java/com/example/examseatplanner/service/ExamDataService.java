package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.*;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.repository.ExamRepository;
import com.example.examseatplanner.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamDataService {

    private ExamRepository examRepository;
    private StudentRepository studentRepository;

    public ExamDataService(ExamRepository examRepository,
                           StudentRepository studentRepository){
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
    }

    public ExamDataDTO getExamData(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));

        List<ProgramResponseDTO> programs = exam.getPrograms().stream()
                .map(p -> new ProgramResponseDTO(p.getProgramCode(), p.getProgramName()))
                .toList();

        List<RoomResponseDTO> rooms = exam.getRooms().stream()
                .map(r -> new RoomResponseDTO(r.getRoomNo(), r.getSeatingCapacity(), r.getNumRow(),r.getSeatsPerBench(),r.getRoomColumn()))
                .toList();

        List<StudentDTO> students = studentRepository.findByExamId(examId).stream()
                .map(s -> new StudentDTO(
                        s.getProgram().getProgramCode(),
                        s.getSemester().ordinal() + 1, // Convert enum to 1-based integer
                        s.getRoll()))
                .toList();

        return new ExamDataDTO(
                exam.getId(),
                exam.getStartDate().toString(),
                exam.getEndDate().toString(),
                programs,
                rooms,
                students
        );
    }
}

