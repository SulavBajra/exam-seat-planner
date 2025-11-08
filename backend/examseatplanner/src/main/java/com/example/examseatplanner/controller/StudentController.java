package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.StudentRequestDTO;
import com.example.examseatplanner.dto.StudentResponseDTO;
import com.example.examseatplanner.exception.StudentAlreadyExistException;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {


    private StudentService studentService;

    public StudentController(StudentService studentService){
        this.studentService = studentService;
    }

    @GetMapping
    public List<StudentResponseDTO> getAllStudents() {
        return studentService.getAllStudents();
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable String studentId) {
        Optional<Student> student = studentService.getStudentById(studentId);
        return student.map(s -> ResponseEntity.ok(studentService.convertToDTO(s))) // or studentMapper.toDTO(s)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping
    public StudentResponseDTO createStudent(@RequestBody @Valid StudentRequestDTO request) {
        return studentService.createStudent(request);
    }

    // @PostMapping("/bulk")
    // public ResponseEntity<?> createStudentsBulk(@RequestBody List<StudentRequestDTO> students) {
    //     try {
    //         for (StudentRequestDTO dto : students) {
    //             studentService.createStudent(dto);
    //         }
    //         return ResponseEntity.ok().body("Successfully uploaded " + students.size() + " students.");
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //                 .body("Failed to upload students: " + e.getMessage());
    //     }
    // }


    @PostMapping("/bulk")
    public ResponseEntity<?> createStudentsBulk(@RequestBody List<StudentRequestDTO> students) {
        List<Map<String, Object>> results = new ArrayList<>();

        for (StudentRequestDTO dto : students) {
            Map<String, Object> result = new HashMap<>();
            result.put("programCode", dto.programCode());
            result.put("semester", dto.semester());
            result.put("roll", dto.roll());

            try {
                StudentResponseDTO saved = studentService.createStudent(dto);
                result.put("status", "success");
                result.put("message", "Student created successfully");
                result.put("studentId", saved.studentId());
            } catch (StudentAlreadyExistException e) {
                result.put("status", "error");
                result.put("message", "Student already exists");
            } catch (Exception e) {
                result.put("status", "error");
                result.put("message", e.getMessage());
            }

            results.add(result);
        }

        // You can also summarize overall outcome:
        long successCount = results.stream().filter(r -> "success".equals(r.get("status"))).count();
        long errorCount = results.size() - successCount;

        Map<String, Object> response = new HashMap<>();
        response.put("summary", Map.of("success", successCount, "error", errorCount));
        response.put("details", results);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{studentId}")
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable String studentId,
                                                            @RequestBody @Valid StudentRequestDTO dto) {
        try {
            StudentResponseDTO updated = studentService.updateStudent(studentId, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/upload-excel")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        try {
            studentService.importStudentsFromExcel(file);
            return ResponseEntity.ok("Students uploaded successfully");
        } catch (Exception e) {
            e.printStackTrace(); // Logs full error trace
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process file: " + e.getMessage());
        }
    }


    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable String studentId) {
        if (!studentService.getStudentById(studentId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        studentService.deleteStudent(studentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> deleteAllStudents() {
        studentService.deleteAllStudents();
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/semester/{semester}")
    public List<StudentResponseDTO> getStudentsBySemester(@PathVariable int semester) {
        List<Student> students = studentService.getStudentsBySemester(semester);
        return studentService.convertToDTOList(students);
    }

    @GetMapping("/program/{programCode}/semester/{semester}")
    public List<StudentResponseDTO> getStudentsByProgramAndSemester(
            @PathVariable Integer programCode,
            @PathVariable int semester) {
        List<Student> students = studentService.getStudentsByProgramCodeAndSemester(programCode, semester);
        return studentService.convertToDTOList(students);
    }
}