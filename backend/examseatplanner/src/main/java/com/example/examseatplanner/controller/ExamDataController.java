package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.ExamDataDTO;
import com.example.examseatplanner.service.ExamDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exam-data")
public class ExamDataController {

    @Autowired
    private ExamDataService examDataService;

    @GetMapping("/{examId}/data")
    public ResponseEntity<ExamDataDTO> getExamData(@PathVariable Integer examId) {
        try {
            ExamDataDTO examData = examDataService.getExamData(examId);
            return ResponseEntity.ok(examData);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }
}
