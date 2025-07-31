package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.StudentRequestDTO;
import com.example.examseatplanner.dto.StudentResponseDTO;
import com.example.examseatplanner.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponseDTO> getStudents(){
        return studentService.findAllStudents();
    }

    @GetMapping("{studentId}")
    public Optional<StudentResponseDTO> getStudentById(@PathVariable String studentId){
        return studentService.findByStudentId(studentId);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<StudentResponseDTO> registerStudent(@Validated @RequestBody StudentRequestDTO dto) {
        return ResponseEntity.ok(studentService.registerFromDTO(dto));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadStudents(@RequestParam("file") MultipartFile file) {
        studentService.importFromExcel(file);
        return ResponseEntity.ok("Students imported successfully");
    }
}
