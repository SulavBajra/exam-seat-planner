package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.mapper.ExamMapper;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.repository.ExamRepository;
import com.example.examseatplanner.repository.ProgramRepository;
import com.example.examseatplanner.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final ProgramRepository programRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public ExamService(ExamRepository examRepository,
                       ProgramRepository programRepository,
                       RoomRepository roomRepository) {
        this.examRepository = examRepository;
        this.programRepository = programRepository;
        this.roomRepository = roomRepository;
    }

    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    public Optional<Exam> getExamById(Integer examId) {
        return examRepository.findById(examId);
    }

    public Exam saveExam(Exam exam) {
        return examRepository.save(exam);
    }

    public Exam saveFromDto(ExamRequestDTO dto) {
        List<Program> programs = programRepository.findAllById(dto.programCodes()); // dto must have programCodes()
        if (programs.size() != dto.programCodes().size()) {
            throw new IllegalArgumentException("One or more program codes are invalid");
        }

        List<Room> rooms = roomRepository.findAllById(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            throw new IllegalArgumentException("One or more room numbers are invalid");
        }

        Exam exam = ExamMapper.toEntity(dto, programs, rooms);
        return examRepository.save(exam);
    }

    public void deleteExam(Integer examId) {
        examRepository.deleteById(examId);
    }

    public List<Exam> getExamsByDate(LocalDate date) {
        return examRepository.findByDate(date);
    }

    public List<Exam> getExamsByProgram(Program program) {
        return examRepository.findByProgramsContaining(program);
    }

    public List<Exam> getExamsByDateRange(LocalDate startDate, LocalDate endDate) {
        return examRepository.findByDateBetween(startDate, endDate);
    }

    public List<Exam> getExamsByProgramCode(Integer programCode) {
        return examRepository.findByPrograms_ProgramCode(programCode);
    }

    public boolean isRoomAvailable(Integer roomNo, LocalDate date) {
        List<Exam> conflictingExams = examRepository.findByRoomAndDate(roomNo, date);
        return conflictingExams.isEmpty();
    }
}
