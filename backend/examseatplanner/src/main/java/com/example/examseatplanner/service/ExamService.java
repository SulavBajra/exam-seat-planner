package com.example.examseatplanner.service;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.dto.ProgramSemesterDTO;
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
import java.util.NoSuchElementException;
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

    public ExamResponseDTO createExamFromDto(ExamRequestDTO dto) {
        // Validate and fetch programs
        List<Integer> programCodes = dto.programSemesters().stream()
                .map(ProgramSemesterDTO::programCode)
                .distinct()
                .toList();

        List<Program> programs = programRepository.findAllById(programCodes);
        if (programs.size() != programCodes.size()) {
            throw new IllegalArgumentException("One or more program codes are invalid");
        }

        // Validate and fetch rooms
        List<Room> rooms = roomRepository.findAllById(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            throw new IllegalArgumentException("One or more room numbers are invalid");
        }

        Exam exam = ExamMapper.toEntity(dto, programs, rooms);
        Exam savedExam = examRepository.save(exam);
        return ExamMapper.toDto(savedExam);
    }

    public ExamResponseDTO updateExamFromDto(Integer examId, ExamRequestDTO dto) {
        Optional<Exam> existingExamOpt = examRepository.findById(examId);
        if (existingExamOpt.isEmpty()) {
            throw new NoSuchElementException("Exam not found");
        }

        // Validate and fetch programs
        List<Integer> programCodes = dto.programSemesters().stream()
                .map(ProgramSemesterDTO::programCode)
                .distinct()
                .toList();

        List<Program> programs = programRepository.findAllById(programCodes);
        if (programs.size() != programCodes.size()) {
            throw new IllegalArgumentException("One or more program codes are invalid");
        }

        // Validate and fetch rooms
        List<Room> rooms = roomRepository.findAllById(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            throw new IllegalArgumentException("One or more room numbers are invalid");
        }

        Exam exam = ExamMapper.toEntity(dto, programs, rooms);
        exam.setId(examId);

        Exam savedExam = examRepository.save(exam);
        return ExamMapper.toDto(savedExam);
    }

    public boolean deleteExam(Integer examId) {
        if (!examRepository.existsById(examId)) {
            return false;
        }
        examRepository.deleteById(examId);
        return true;
    }

    public List<Exam> getExamsByDate(LocalDate date) {
        return examRepository.findByDate(date);
    }

    public List<Exam> getExamsByDateRange(LocalDate startDate, LocalDate endDate) {
        return examRepository.findByDateBetween(startDate, endDate);
    }

    public List<Exam> getExamsByProgramCode(Integer programCode) {
        return examRepository.findByProgramCode(programCode);
    }
}
