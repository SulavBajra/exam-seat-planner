package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.service.ExamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@Validated
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public ResponseEntity<List<ExamResponseDTO>> getAllExams() {
        List<ExamResponseDTO> exams = examService.getAllExams();
        return ResponseEntity.ok(exams);
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ExamResponseDTO> getExamById(@PathVariable Integer examId) {
        ExamResponseDTO exam = examService.getExamById(examId);
        return ResponseEntity.ok(exam);
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Exam>> getExamsByDate(
            @PathVariable
            @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in format yyyy-MM-dd")
            String date) {
        List<Exam> exams = examService.getExamsByDate(date);
        return ResponseEntity.ok(exams);
    }

    @PostMapping
    public ResponseEntity<ExamResponseDTO> createExam(@Valid @RequestBody ExamRequestDTO examRequestDTO) {
        ExamResponseDTO createdExam = examService.createExam(examRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdExam);
    }

    @PutMapping("/{examId}")
    public ResponseEntity<ExamResponseDTO> updateExam(
            @PathVariable Integer examId,
            @Valid @RequestBody ExamRequestDTO examRequestDTO) {
        ExamResponseDTO updatedExam = examService.updateExam(examId, examRequestDTO);
        return ResponseEntity.ok(updatedExam);
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable Integer examId) {
        examService.deleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{examId}/allocate-seats")
    public ResponseEntity<String> allocateSeats(@PathVariable Integer examId) {
        examService.allocateSeatsForExam(examId);
        return ResponseEntity.ok("Seats allocated successfully for exam " + examId);
    }
}