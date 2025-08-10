package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.mapper.ExamMapper;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Room;
import com.example.examseatplanner.service.ExamService;
import com.example.examseatplanner.service.ProgramService;
import com.example.examseatplanner.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;
    private final ProgramService programService;
    private final RoomService roomService;  // Inject this for room lookup

    @Autowired
    public ExamController(ExamService examService,
                          ProgramService programService,
                          RoomService roomService) {
        this.examService = examService;
        this.programService = programService;
        this.roomService = roomService;
    }

    @GetMapping
    public List<Exam> getAllExams() {
        return examService.getAllExams();
    }

    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExamById(@PathVariable Integer examId) {
        Optional<Exam> exam = examService.getExamById(examId);
        return exam.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ExamResponseDTO> createExam(@RequestBody @Valid ExamRequestDTO dto) {
        Exam savedExam = examService.saveFromDto(dto);
        ExamResponseDTO responseDTO = ExamMapper.toDto(savedExam);
        return ResponseEntity
                .created(URI.create("/api/exams/" + savedExam.getId()))
                .body(responseDTO);
    }

    @PutMapping("/{examId}")
    public ResponseEntity<ExamResponseDTO> updateExam(@PathVariable Integer examId,
                                                      @RequestBody @Valid ExamRequestDTO dto) {
        Optional<Exam> existingExamOpt = examService.getExamById(examId);
        if (existingExamOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Fetch Programs by codes using ProgramService (you may need to add findAllById method in ProgramService)
        List<Program> programs = programService.findAllById(dto.programCodes());
        if (programs.size() != dto.programCodes().size()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Fetch Rooms by numbers using RoomService (you may need to add findAllById method in RoomService)
        List<Room> rooms = roomService.findAllById(dto.roomNumbers());
        if (rooms.size() != dto.roomNumbers().size()) {
            return ResponseEntity.badRequest().body(null);
        }

        // Map DTO to entity using fetched entities
        Exam updatedExam = ExamMapper.toEntity(dto, programs, rooms);
        updatedExam.setId(examId);

        // Save updated exam
        Exam savedExam = examService.saveExam(updatedExam);
        ExamResponseDTO responseDTO = ExamMapper.toDto(savedExam);

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Integer examId) {
        if (examService.getExamById(examId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/date/{date}")
    public List<Exam> getExamsByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return examService.getExamsByDate(date);
    }

    @GetMapping("/program/{programCode}")
    public List<Exam> getExamsByProgramCode(@PathVariable Integer programCode) {
        return examService.getExamsByProgramCode(programCode);
    }

    @GetMapping("/date-range")
    public List<Exam> getExamsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return examService.getExamsByDateRange(startDate, endDate);
    }
}
