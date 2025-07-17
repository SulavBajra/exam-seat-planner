package com.example.examseatplanner.controller;

import com.example.examseatplanner.dto.StudentRequestDTO;
import com.example.examseatplanner.model.Program;
import com.example.examseatplanner.model.Student;
import com.example.examseatplanner.model.Subject;
import com.example.examseatplanner.service.ProgramService;
import com.example.examseatplanner.service.StudentService;
import com.example.examseatplanner.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final ProgramService programService;

    @Autowired
    public StudentController(StudentService studentService,
                             SubjectService subjectService,
                             ProgramService programService){
        this.studentService = studentService;
        this.subjectService = subjectService;
        this.programService = programService;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<Student> registerStudent(@RequestBody StudentRequestDTO dto){
        Program program = programService.findByProgramName(dto.program()).orElseThrow(()-> new RuntimeException("Program Not Found"));
        List<Subject> subjects = dto.subjects().stream()
                .map(name -> subjectService.findBySubjectName(name).orElseThrow(() -> new RuntimeException("Subject not found: " + name)))
                .toList();

        String studentId = studentService.generateStudentCode(
                dto.enrollYear(),
                program.getProgramCode(),
                dto.roll()
        );

        Student student = new Student(subjects,program,dto.enrollYear(),dto.semester(),dto.roll());
        student.setStudentId(studentId);

        return ResponseEntity.ok(studentService.registerStudent(student));
    }


}
