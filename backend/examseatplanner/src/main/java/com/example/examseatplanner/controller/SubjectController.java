package com.example.examseatplanner.controller;

import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectService.getAllSubjects();
    }

    @GetMapping("/{subjectCode}")
    public ResponseEntity<Subject> getSubject(@PathVariable Integer subjectCode) {
        return subjectService.getSubjectByCode(subjectCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/program/{programCode}")
    public List<Subject> getSubjectsByProgram(@PathVariable Integer programCode) {
        return subjectService.getSubjectsByProgramCode(programCode);
    }

    @PostMapping
    public Subject createSubject(@RequestBody Subject subject) {
        return subjectService.saveSubject(subject);
    }

    @DeleteMapping("/{subjectCode}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Integer subjectCode) {
        subjectService.deleteSubject(subjectCode);
        return ResponseEntity.noContent().build();
    }
}
