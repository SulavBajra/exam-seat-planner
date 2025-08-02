package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.dto.ExamResponseDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.repository.ExamRepository;
import com.example.examseatplanner.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("api/exams")
public class ExamController {

    private final ExamService examService;
    private final ExamRepository examRepository;

    public ExamController(ExamService examService,ExamRepository examRepository){
        this.examService = examService;
        this.examRepository = examRepository;
    }

    @GetMapping("/date")
    public ResponseEntity<List<Exam>> getByExamDate(@RequestParam String date) {
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
        final LocalDate dt = (LocalDate) dtf.parseBest(date);
        List<Exam> exams = examRepository.findByDate(dt);
        return ResponseEntity.ok(exams);
    }

    @PostMapping("/create")
    public ResponseEntity<ExamResponseDTO> createExam(@Validated @RequestBody ExamRequestDTO examRequestDTO) {
        return ResponseEntity.ok(examService.createExam(examRequestDTO));
    }


}
