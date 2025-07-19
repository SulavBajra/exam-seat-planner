package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ExamRequestDTO;
import com.example.examseatplanner.model.Exam;
import com.example.examseatplanner.service.ExamService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService){
        this.examService = examService;
    }

    @GetMapping("/date")
    public List<Exam> getByExamDate(@RequestBody String date){
        return examService.findByDate(date);
    }

    @PostMapping("/create")
    public ResponseEntity<ExamRequestDTO> createExam(@Validated @RequestBody ExamRequestDTO examRequestDTO){
        return ResponseEntity.ok(examService.createExam(examRequestDTO));
    }

}
