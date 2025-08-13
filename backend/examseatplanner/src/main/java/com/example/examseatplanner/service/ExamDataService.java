package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.*;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.repository.ExamRepository;
import com.example.examseatplanner.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamDataService {

    private ExamRepository examRepository;
    private StudentRepository studentRepository;

    @Autowired
    public ExamDataService(ExamRepository examRepository,
                           StudentRepository studentRepository){
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
    }

    public ExamDataDTO getExamData(Integer examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found: " + examId));

        // Programs
        List<ProgramDTO> programs = exam.getPrograms().stream()
                .map(p -> new ProgramDTO(p.getProgramCode(), p.getProgramName()))
                .toList();

        // Rooms
        List<RoomDTO> rooms = exam.getRooms().stream()
                .map(r -> new RoomDTO(r.getRoomNo(), r.getSeatingCapacity(), r.getNumRow()))
                .toList();

        // Students
        List<StudentDTO> students = studentRepository.findByExamId(examId).stream()
                .map(s -> new StudentDTO(
                        s.getProgram().getProgramCode(),
                        s.getSemester().ordinal() + 1, // Convert enum to 1-based integer
                        s.getRoll()))
                .toList();

        return new ExamDataDTO(
                exam.getId(),
                exam.getDate().toString(),
                programs,
                rooms,
                students
        );
    }
}

//    public ExamDataDTO getExamData(Integer examId) {
//        Exam exam = examRepository.findById(examId)
//                .orElseThrow(() -> new RuntimeException("Exam not found"));
//
//        // Programs
//        List<ProgramRequestDTO> programs = exam.getPrograms().stream()
//                .map(p -> new ProgramRequestDTO(p.getProgramName(), p.getProgramCode()))
//                .toList();
//
//        // Rooms
//        List<RoomRequestDTO> rooms = exam.getRooms().stream()
//                .map(r -> new RoomRequestDTO(r.getRoomNo(), r.getSeatingCapacity(), r.getNumRow()))
//                .toList();
//
//        // Students
//        List<StudentRequestDTO> students = studentRepository.findByExamId(examId).stream()
//                .map(s -> new StudentRequestDTO(
//                        s.getProgram().getProgramCode(),
//                        s.getSemester().ordinal() + 1,
//                        s.getRoll()
//                )).toList();
//
//        return new ExamDataDTO(exam.getId(), exam.getDate().toString(), programs, rooms, students);
//    }
//}

