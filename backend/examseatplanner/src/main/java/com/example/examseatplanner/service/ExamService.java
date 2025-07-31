package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final SeatAllocationService seatAllocationService;

    @Autowired
    public ExamService(ExamRepository examRepository,
                       SubjectRepository subjectRepository,
                       StudentRepository studentRepository,
                       RoomRepository roomRepository,
                       SeatAllocationService seatAllocationService) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.seatAllocationService = seatAllocationService;
    }

    public List<Exam> findByDate(String date) {
        return examRepository.findByDate(date);
    }

    public ExamResponseDTO createExam(ExamRequestDTO dto) {
        Subject subject = subjectRepository.findBySubjectCode(dto.subjectCode())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        List<Student> students = studentRepository.findAllById(dto.studentIds());
        List<Room> rooms = roomRepository.findAllByRoomNoIn(dto.roomIds());

        Exam exam = new Exam();
        exam.setSubject(subject);
        exam.setDate(dto.date());
        exam.setTime(dto.time());
        exam.setStudents(students);
        exam.setRooms(rooms);

        Exam savedExam = examRepository.save(exam);

        seatAllocationService.allocateSeatsStudentId(savedExam);

        return ExamResponseDTO.fromEntity(savedExam);
    }
}

