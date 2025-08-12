package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.BulkExamRequestDTO;
import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.repository.ExamRepository;
import com.example.examseatplanner.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;
    private final ExamRepository examRepository;

    @Autowired
    public ExamController(ExamService examService,
                          ExamRepository examRepository) {
        this.examService = examService;
        this.examRepository = examRepository;
    }

    @GetMapping
    public List<Exam> getAllExams() {
        return examService.getAllExams();
    }

    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExamById(@PathVariable Integer examId) {
        return examService.getExamById(examId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rooms/{examId}")
    public ResponseEntity<List<Integer>> getRoomNoByExamId(@PathVariable Integer examId) {
        List<Integer> roomNumbers = examRepository.findRoomNumbersByExamId(examId);
        if (roomNumbers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(roomNumbers);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<ExamResponseDTO>> createBulkExams(
            @Valid @RequestBody BulkExamRequestDTO request) {

        List<ExamResponseDTO> createdExams = new ArrayList<>();

        for (ExamRequestDTO exam : request.exams()) {
            try {
                ExamResponseDTO responseDTO = examService.createExamFromDto(exam);
                createdExams.add(responseDTO);
            } catch (IllegalArgumentException e) {
               e.printStackTrace();
            }
        }

        return ResponseEntity.ok(createdExams);
    }



    @PostMapping
    public ResponseEntity<ExamResponseDTO> createExam(@RequestBody @Valid ExamRequestDTO dto) {
        try {
            ExamResponseDTO responseDTO = examService.createExamFromDto(dto);
            return ResponseEntity.created(URI.create("/api/exams/" + responseDTO.id()))
                    .body(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{examId}")
    public ResponseEntity<ExamResponseDTO> updateExam(@PathVariable Integer examId,
                                                      @RequestBody @Valid ExamRequestDTO dto) {
        try {
            ExamResponseDTO responseDTO = examService.updateExamFromDto(examId, dto);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Integer examId) {
        boolean deleted = examService.deleteExam(examId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
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
